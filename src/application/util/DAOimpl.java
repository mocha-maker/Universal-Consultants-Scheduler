package application.util;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

import static application.util.Loc.*;

public class DAOimpl extends DAO {


    /*  ======================
        ADD OBJECTS
        ======================
        Adds objects to Database.
        */

    public static void addCust(Customer newCust) {

    }

    public static void addAppt(Appointment newAppt) {

    }

        /*  ======================
        UPDATE OBJECTS
        ======================
        retrieves existing object using the record id
        */

    public static void updateCust(int index, Customer newCust) {
        // Check if customer exists at index
        if (getAllCustomers().get(index) != null) {
            getAllCustomers().set(index,newCust); // Replace customer data at index with newCust data
        }
    }

    public static void updateAppt(int index, Appointment newAppt) {
        // Check if appointment exists at index
        if (getAllAppointments().get(index) != null) {
            getAllAppointments().set(index,newAppt); // Replace appointment data at index with newAppt data
        }
    }

    /*  ======================
        DELETE OBJECTS
        ======================
        call delete methods based on selection from table
        Parts can be deleted if it is assigned to a Product
        Products cannot be deleted if they have associated Parts
        */
    public static boolean deleteCust(int custId) {
        // Retrieve Part Index from allParts

        for (Customer c : getAllCustomers()) { // loop through all products
            if (c.getId() == custId) {
                // if partId found, Remove Part
                getAllCustomers().remove(c); //
                return true;
            }
        }
        return false; // return deletion success to caller
    }


    /* ======================
        GETTERS
        For use for TableViews
       ======================*/

    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Customers Database.");
            prepQuery("SELECT * FROM customers JOIN first_level_divisions USING (Division_ID) JOIN countries USING (Country_ID)");

            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");

            int i = 0;
            while(rs.next()) {
                // set result to variables
                System.out.println("Setting Results to Customer Variables.");
                int custId = rs.getInt("Customer_ID");
                String custName = rs.getString("Customer_Name");
                String custPhone = rs.getString("Phone");
                String custAddress = rs.getString("Address");
                String custCode = rs.getString("Postal_Code");
                String custState = rs.getString("Division");
                String custCountry = rs.getString("Country");

                // construct Customer object using result
                System.out.println("Constructing Customer Object.");
                Customer custResult = new Customer(custId,
                        custName,
                        custPhone,
                        custAddress,
                        custCode,
                        custState,
                        custCountry);

                // add Customer object to Observable List
                allCustomers.add(custResult);
                i++;
                System.out.println("Customer added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allCustomers;
    }

    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Appointment Database.");
            prepQuery("SELECT * FROM appointments JOIN contacts USING (Contact_ID) JOIN customers USING (Customer_ID) JOIN users USING (User_ID)");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Appointment Variables.");
                int apptID = rs.getInt("Appointment_ID");
                String apptTitle = rs.getString("Title");
                String apptDesc = rs.getString("Description");
                String apptLoc = rs.getString("Location");
                String apptType = rs.getString("Type");

                String apptStart = dateToString(getLocalDateTime(rs.getTimestamp("Start")),"hh:mm a");
                String apptEnd = dateToString(getLocalDateTime(rs.getTimestamp("End")),"hh:mm a");

                int apptContact = rs.getInt("Contact_ID");
                int apptCustID = rs.getInt("Customer_ID");
                int apptUserID = rs.getInt("User_ID");

                // construct Appointment object using result
                System.out.println("Constructing Appointment Object.");
                Appointment apptResult = new Appointment(apptID,
                        apptTitle,
                        apptDesc,
                        apptLoc,
                        apptType,
                        apptStart,
                        apptEnd,
                        apptContact,
                        apptCustID,
                        apptUserID);

                // add Appointment object to Observable List
                allAppointments.add(apptResult);
                i++;
                System.out.println("Appointment added to Observable List. (" + i + ")");

            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allAppointments;
    }

    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Contacts Database.");
            prepQuery("SELECT * FROM contacts");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Contact Variables.");
                int contact_id = rs.getInt("Contact_ID");
                String contact_name = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                // construct Contact object using result
                System.out.println("Constructing Contact Object.");
                Contact contactResult = new Contact(contact_id,
                        contact_name,
                        email);

                // add Contact object to Observable List
                allContacts.add(contactResult);
                i++;
                System.out.println("Contact added to Observable List. (" + i + ")");

            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allContacts;
    }

    // end of class
}

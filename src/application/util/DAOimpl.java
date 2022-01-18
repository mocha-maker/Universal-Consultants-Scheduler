package application.util;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static application.util.Alerts.errorMessage;
import static application.util.Alerts.infoMessage;
import static application.util.Loc.*;

public class DAOimpl extends DAO {

    // for use in appointment creation and logging other activity
    private static User activeUser;
    private static ObservableList<String> divisions = FXCollections.observableArrayList();

    /**
     * setActiveUser
     * Record Active User upon log in
     * @param loggedUser the user who successfully logged into this session
     */
    public static void setActiveUser(User loggedUser) {
        activeUser = loggedUser;
    }

    /**
     * getActiveUser
     * @return activeUser - the recorded user who logged into this session
     */
    public static User getActiveUser() {
        return activeUser;
    }

    /*  ======================
        ADD RECORDS
        ======================
        Adds records to Database.
        */

    /**
     *
     * @param newCust
     */
    public static void addCust(Customer newCust) {

        int index = newCust.getId();
        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
        // sqlquery to add new customer
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO customers VALUES (?,?,?,?,?,?,?,?,?,?)");

            System.out.println("Setting Parameters.");
            ps.setInt(1, index);
            ps.setString(2, newCust.getCustomerName());
            ps.setString(3, newCust.getAddress());
            ps.setString(4, newCust.getPostalCode());
            ps.setString(5, newCust.getPhone());
            ps.setTimestamp(6, current);
            ps.setString(7, getActiveUser().getUserName());
            ps.setTimestamp(8, current);
            ps.setString(9, getActiveUser().getUserName());
            ps.setInt(10, getDivisionID(newCust.getDivision()));

            try {
                ps.executeUpdate();
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
            infoMessage("Customer Created.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param newAppt
     */
    public static void addAppt(Appointment newAppt) {

        int index = newAppt.getId();
        Timestamp current = Timestamp.valueOf(LocalDateTime.now());

        // check if new appointments times are valid
        // 1. Appointment is on the same day locally
        // 2. Appointment is within business hours
            // error if invalid

        // check if new appointments times overlap with another appointment for the same customer and contact
            // error if overlap

        // if no issues -> sqlquery to add appointment "INSERT INTO table (cols) VALUES(?,?,...)"
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO appointments " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            System.out.println("Setting Parameters.");
            ps.setInt(1, index);
            ps.setString(2, newAppt.getTitle());
            ps.setString(3, newAppt.getDescription());
            ps.setString(4, newAppt.getLocation());
            ps.setString(5, newAppt.getType());
            ps.setTimestamp(6, Timestamp.valueOf(newAppt.getStart()));
            ps.setTimestamp(7, Timestamp.valueOf(newAppt.getEnd()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, getActiveUser().getUserName());
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(11, getActiveUser().getUserName());
            ps.setInt(12, newAppt.getCustomerId());
            ps.setInt(13, newAppt.getUserId());
            ps.setInt(14, newAppt.getContact().getId());

        } catch (SQLException ex) {

        }


    }

    /*  ======================
        UPDATE OBJECTS
        ======================
        retrieves existing object using the record id
        */

    public static void updateCust(Customer newCust) {
        int index = newCust.getId();
        System.out.println("Customer ID is " + index);
        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
        // sql query if customer record exists in db
        try {
            System.out.println("Querying Customer DB for existence of customer id.");
            prepQuery("SELECT COUNT(Customer_ID) AS COUNT FROM customers WHERE Customer_ID = " + index);
            ResultSet rs = getResult();
            int count = 0;
            while (rs.next()) {
                 count = rs.getInt("COUNT");
                }
            if (count > 0) {
                PreparedStatement ps = getConnection().prepareStatement("UPDATE customers SET " +
                        "Customer_Name = ?," +
                        "Phone = ?, " +
                        "Address = ?, " +
                        "Postal_Code = ?, " +
                        "Last_Update = ?, " +
                        "Last_Updated_By = ?, " +
                        "Division_ID = ? " +
                        "WHERE Customer_ID = " + index);

                System.out.println("Setting Parameters.");
                ps.setString(1,newCust.getCustomerName());
                ps.setString(2,newCust.getPhone());
                ps.setString(3,newCust.getAddress());
                ps.setString(4,newCust.getPostalCode());
                ps.setTimestamp(5,current);
                ps.setString(6,getActiveUser().getUserName());
                ps.setInt(7,getDivisionID(newCust.getDivision()));

                try {
                    ps.executeUpdate();
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                }
                infoMessage("Customer Updated.");
            } else {
                errorMessage("Update Customer", "Customer does not exist.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void updateAppt(Appointment newAppt) {
        int index = newAppt.getId();

        // sql query if appointment record exists in db
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

        for (Customer c : getAllCustomers()) { // loop through all Customers
            if (c.getId() == custId) {
                // if found, Remove Customer
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

    /**
     * Queries DB to build a new Observable list
     * @return
     */
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

    public static ObservableList<String> getAllCountries() {
        ObservableList<String> allCountries = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Countries Database for Unique Items.");
            prepQuery("SELECT * FROM countries ORDER BY Country ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Countries.");
                String countryResult = rs.getString("Country");
                // add Type String to Observable List
                allCountries.add(countryResult);
                i++;
                System.out.println(countryResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allCountries;
    }

    public static ObservableList<String> getAllRegions(String country) {
        ObservableList<String> allRegions = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Regions Database for Unique Items.");
            prepQuery("SELECT * FROM first_level_divisions JOIN countries USING (Country_ID) WHERE Country = '" + country +"' ORDER BY Division ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Regions.");
                String regionResult = rs.getString("Division");
                // add Type String to Observable List
                allRegions.add(regionResult);
                i++;
                System.out.println(regionResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allRegions;
    }

    /**
     *
     * @return
     */
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

                Contact apptContact = getAllContacts().get(rs.getInt("Contact_ID") - 1);
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

    public static ObservableList<String> getAppointmentTypes() {
        ObservableList<String> allTypes = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Appointments Database for Unique Types.");
            prepQuery("SELECT DISTINCT Type FROM appointments");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Appointment Type.");
                String typeResult = rs.getString("Type");
                // add Type String to Observable List
                allTypes.add(typeResult);
                i++;
                System.out.println(typeResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allTypes;
    }

    /*  ======================
        DELETE OBJECTS
        ======================
        Deleted objects from Database.
        */

    /**
     * TODO: create customer deletion
     * @param cust
     */
    public static void deleteCust(Customer cust) {
        System.out.println("Attempting to delete customer record.");

        // retrieve customer id from object
        int custId = cust.getId();
        // sqlquery to count # of appointments associated with customer
        try {
            prepQuery("SELECT COUNT(*) AS Total_Appointments FROM appointments JOIN customers USING (Customer_ID) WHERE Customer_ID = " + custId);
            ResultSet rs = getResult();
            while (rs.next()) {
                int apptCount = 0;
                apptCount = rs.getInt("Total_Appointments");

                // if no appointments -> delete
                if (apptCount == 0) {

                    try {

                        // sqlquery to delete record
                        prepQuery("DELETE FROM customers WHERE Customer_ID = " + custId);

                        // sqlquery to check if record no longer exists
                        prepQuery("SELECT COUNT(*) AS Count FROM customers WHERE Customer_ID = " + custId);
                        rs = getResult();
                        while (rs.next()) {
                            Integer count = getResult().getInt("Count");
                            if ( count == 0 ) {
                                // give alert on successful deletion
                                infoMessage("Customer ID " + custId + " and Name '" + cust.getCustomerName() + "' Successfully deleted.");
                            } else {
                                errorMessage("Delete Customer", "Unable to delete customer.");
                            }
                        }
                    } catch (SQLException e) {
                        printSQLException(e);
                    }
                    } else {
                    // if appointments exist -> give error
                    errorMessage("Referential Integrity", "Cannot delete this customer record due to having " + apptCount + " associated appointments.");
                }
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }

    }

    /**
     * TODO: create appointment cancellation
     * @param appt
     */
    public static void cancelAppt(Appointment appt) {
        System.out.println("Attempting to delete appointment record.");

        // retrieve customer id from object
        int apptId = appt.getId();

        try {
            // sqlquery to delete record
            prepQuery("DELETE FROM appointments WHERE Appointment_ID = " + apptId);

            // check for record
            prepQuery("SELECT COUNT(*) AS Count FROM appointments WHERE Appointment_ID = " + apptId);
            ResultSet rs = getResult();
            while (rs.next()) {
                Integer count = getResult().getInt("Count");
                if (count == 0) {
                    // give alert on successful deletion
                    infoMessage("Appointment with ID " + apptId + " successfully deleted.");
                } else {
                    errorMessage("Appointment Deletion", "Appointment not deleted.");
                }
            }

        } catch (SQLException ex) {
            printSQLException(ex);
        }

    }

    public static int getDivisionID(String divisionName) {
        System.out.println("Retrieving Division ID for Division " + divisionName);
        int divId = 0;
        try {
            prepQuery("SELECT * FROM first_level_divisions WHERE Division = '" + divisionName +"'");
            ResultSet rs = getResult();
            while (rs.next()) {
                divId = rs.getInt("Division_ID");
                System.out.println("Division ID Retrieved = " + divId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return divId;
    }

    /**
     * Queries table for the first available ID to use in a new record
     * @param table the table to be queried
     * @return availableID the first available ID in the table queried
     */
    public static int getAvailableID(String table) {
        int availableID = 1;

        try {
            prepQuery("SELECT * FROM " + table);
            ResultSet rs = getResult();
            while (rs.next()) {
                if (availableID == rs.getInt(1)) {
                    availableID++;
                } else {
                    break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return availableID;
    }

    // end of class
}

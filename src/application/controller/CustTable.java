package application.controller;

import application.model.Appointment;
import application.model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.infoMessage;


public class CustTable extends TableBase<Customer> implements Initializable {
    /*  ======================
        CUSTOMER TABLE ELEMENTS
        ======================*/


    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/
    public void addColumns() {
        // Set specially formatted columns
        final TableColumn<?, ?> nameCol = new TableColumn<>("Contact");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));


        tableView.getColumns().addAll(getStringColumn(Customer.class, "customerName"),
                getStringColumn(Customer.class, "phone"),
                getStringColumn(Customer.class, "address"),
                getStringColumn(Customer.class,"postalCode"),
                getStringColumn(Customer.class,"division"),
                getStringColumn(Customer.class,"country"));

    }

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateTable() {
        tableView.getItems().clear();
        tableView.setItems(getAllCustomers()); }

    /*  ======================
        Event Handling
        ======================*/

    public void deleteCustRecord(ActionEvent e) {
        Customer customer = tableView.getSelectionModel().getSelectedItem();

        if (customer != null) {
            boolean confirm = confirmMessage("Delete Customer", "Are you sure you want to delete " + customer.getCustomerName() + " with  ID " + customer.getId() + "? \n" +
                    "All related appointments will also be deleted.");

            int count = 0;
            for (Appointment appointment : allAppointments) {
                if (appointment.getCustomer().getId() == customer.getId()) {
                    count++;
                }
            }

            if (confirm) {
                boolean deleted = deleteRecord(customer);
                if (deleted) {

                    infoMessage("Customer ID: " + customer.getId() + "\nCustomer Name: " + customer.getCustomerName() + "\nSuccessfully Deleted with " + count + " Appointment(s)");
                }
                updateTable();
            }
        } else {
            infoMessage("Please select a record for deletion.");
        }

    }


    /**
     *
     * @return SQL statement to delete customer record
     */
    protected String getDeleteStatement() {
        return "DELETE FROM customers WHERE Customer_ID = ?";
    }

    /**
     *
     * @return SQL statement to delete appointment dependencies
     */
    protected String getDeleteDependencies() {return "DELETE FROM appointments WHERE Customer_ID = ?";}


    /**
     * Queries DB to build a new Observable list
     *
     */
    public static ObservableList<Customer> getAllCustomers() {
        allCustomers.clear();

        try {
            System.out.println("Querying Customers Database.");
            prepQuery("SELECT * FROM customers JOIN first_level_divisions USING (Division_ID) JOIN countries USING (Country_ID) ORDER BY Customer_ID ASC");

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


    // end of class
}

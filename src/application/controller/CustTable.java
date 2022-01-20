package application.controller;

import application.model.Appointment;
import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.controller.ApptTable.allAppointments;
import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.infoMessage;



public class CustTable extends TableBase<Customer> implements Initializable {
    /*  ======================
        CUSTOMER TABLE ELEMENTS
        ======================*/

    public static ObservableList<Customer> allCustomers;

    public TableView<Customer> allCustomersTable;
    public TableColumn<?,?> custID;
    public TableColumn<?,?> custName;
    public TableColumn<?,?> custPhone;
    public TableColumn<?,?> custAddress;
    public TableColumn<?,?> custCode;
    public TableColumn<?,?> custState;
    public TableColumn<?,?> custCountry;


    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/
    public void setColumns() {
        System.out.println("Setting Customer Table Columns.");
        custID.setCellValueFactory(new PropertyValueFactory<>("id"));
        custName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        custAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        custCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        custState.setCellValueFactory(new PropertyValueFactory<>("division"));
        custCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
    }

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateTable() { allCustomersTable.setItems(getAllCustomers()); }

    /*  ======================
        Event Handling
        ======================*/
    /**
     * Open Customer Record
     * TODO: Update record title depending on which button is clicked
     */
    public void toCustRecord(ActionEvent e) throws IOException {
        // capture selected customer from table
        Customer customer = allCustomersTable.getSelectionModel().getSelectedItem();

        // record which button was clicked
        String button = ((Button)e.getSource()).getText();
        System.out.println(button);
        String action = "add";
        if ( button.contains("New") ) {
            action = "add";
        } else if ( button.contains("Update") ) {
            action = "edit";

        }
        System.out.println("Action needed = " + action);



        if (customer != null || action.equals("add")) {
            // Transfer parameters to Controller
            FXMLLoader loader = setLoader("CustRecord");
            Parent root = loader.load();

            CustRecord controller = loader.getController();
            controller.getParams(action, customer);

            popupScene(root, "Customer Record");
            updateTable();

            // send button action and table row item
        } else if ( action.equals("edit") ){
            infoMessage("Please select a record for modification.");
        }

    }

    public void deleteCustRecord(ActionEvent e) {
        Customer customer = allCustomersTable.getSelectionModel().getSelectedItem();

        if (customer != null) {
            boolean confirm = confirmMessage("Delete Customer", "Are you sure you want to delete " + customer.getCustomerName() + " with  ID " + customer.getId() + "? \n" +
                    "All related appointments will also be deleted.");

            int count = 0;
            for (Appointment appointment : allAppointments) {
                if (appointment.getCustomerId() == customer.getId()) {
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
        allCustomers = FXCollections.observableArrayList();

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



    // end of class
}

package application.controller;

import application.model.Appointment;
import application.model.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Customer Table Controller
 * Manages the Customer Table View
 */
public class CustTable extends TableBase<Customer> implements Initializable {


    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/

    /**
     * Creates and Sets TableView Columns
     */
    public void addColumns() {
        // Set specially formatted columns
        final TableColumn<?, ?> nameCol = new TableColumn<>("Contact");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Set string columns for table
        tableView.getColumns().addAll(getStringColumn(Customer.class, "customerName"),
                getStringColumn(Customer.class, "phone"),
                getStringColumn(Customer.class, "address"),
                getStringColumn(Customer.class,"addressCode"),
                getStringColumn(Customer.class,"division"),
                getStringColumn(Customer.class,"country"));
    }

    /**
     * Get list of all customers
     * @return list of all customers
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

    /**
     * Deletes a Customer record with a confirmation that related appointments will also be deleted
     * Checks if a valid record is selected
     */
    public void deleteCustRecord() {
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
     * Get SQL customer delete statement
     * @return SQL statement to delete customer record
     */
    protected String getDeleteStatement() {
        return "DELETE FROM customers WHERE Customer_ID = ?";
    }

    /**
     * Get SQL customer delete appointment dependencies statement
     * @return SQL statement to delete appointment dependencies
     */
    protected String getDeleteDependencies() {return "DELETE FROM appointments WHERE Customer_ID = ?";}





    // end of class
}

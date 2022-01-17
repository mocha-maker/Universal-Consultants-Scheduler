package application.controller;

import application.model.Appointment;
import application.model.Customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.infoMessage;
import static application.util.DAOimpl.deleteCust;
import static application.util.DAOimpl.getAllCustomers;


public class CustTable extends Base {
    /*  ======================
        CUSTOMER TABLE ELEMENTS
        ======================*/

    public TableView<Customer> allCustomersTable;
    public TableColumn<?,?> custID;
    public TableColumn<?,?> custName;
    public TableColumn<?,?> custPhone;
    public TableColumn<?,?> custAddress;
    public TableColumn<?,?> custCode;
    public TableColumn<?,?> custState;
    public TableColumn<?,?> custCountry;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setColumns();
        updateCustomersTable();

    }

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
    public void updateCustomersTable() { allCustomersTable.setItems(getAllCustomers()); }

    /*  ======================
        Event Handling
        ======================*/
    /**
     * Open Customer Record
     * TODO: Update record title depending on which button is clicked
     */
    public void toCustRecord(ActionEvent e) throws IOException {
        // Declare Local Variables
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

        // capture selected customer from table
        Customer customer = allCustomersTable.getSelectionModel().getSelectedItem();

        if (customer != null || action == "add") {
            // Transfer parameters to Controller
            FXMLLoader loader = setLoader("CustRecord");
            Parent root = loader.load();

            CustRecord controller = loader.getController();
            controller.getParams(action, customer);

            popupScene(root, "Customer Record");
            updateCustomersTable();

            // send button action and table row item
        } else if ( action == "edit" && customer == null){
            infoMessage("Please select a record for modification.");
        }

    }

    public void deleteCustRecord(ActionEvent e) {
        Customer customer = allCustomersTable.getSelectionModel().getSelectedItem();

        if (customer != null) {
            boolean confirm = confirmMessage("Delete Customer", "Are you sure you want to delete " + customer.getCustomerName() + " with  ID " + customer.getId() + "?");

            if (confirm) {
                deleteCust(customer);
                updateCustomersTable();
            }
        } else {
            infoMessage("Please select a record for deletion.");
        }

    }

    // end of class
}

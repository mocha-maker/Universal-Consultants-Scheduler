package application.controller;

import application.model.Appointment;
import application.model.Customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import static application.util.DAOimpl.getAllCustomers;


public class CustTable extends TableBase<Customer> implements Initializable {
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



        if (customer != null || action == "add") {
            // Transfer parameters to Controller
            FXMLLoader loader = setLoader("CustRecord");
            Parent root = loader.load();

            CustRecord controller = loader.getController();
            controller.getParams(action, customer);

            popupScene(root, "Customer Record");
            updateTable();

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
                deleteRecord(customer);
                updateTable();
            }
        } else {
            infoMessage("Please select a record for deletion.");
        }

    }



    protected String getDeleteStatement() {
        return "DELETE FROM customers WHERE Customer_ID = ?";
    }

    // end of class
}

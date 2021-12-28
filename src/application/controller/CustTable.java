package application.controller;

import application.model.Customer;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

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
    public void updateCustomersTable() {
        allCustomersTable.setItems(getAllCustomers());

    }

}

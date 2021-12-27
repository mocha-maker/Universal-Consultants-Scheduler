package application.controller;

import application.util.DAOimpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public class CustTable extends Base {
    /*  ======================
        CUSTOMER TABLE ELEMENTS
        ======================*/

    public TableView<CustTable> allCustomersTable;
    public TableColumn custID;
    public TableColumn custName;
    public TableColumn custPhone;
    public TableColumn custAddress;
    public TableColumn custCode;
    public TableColumn custState;
    public TableColumn custCountry;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setColumns();
        updateCustomersTable();

    }

    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/
    public void setColumns() {
        custID.setCellValueFactory(new PropertyValueFactory<CustTable,Integer>("custid"));

    }

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateCustomersTable() {
        allCustomersTable.setItems(DAOimpl.getAllCustomers());
    }

}

package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import application.model.Record;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Abstract Table Controller
 * Manages
 * @param <T> the model to use for the tableviews
 */
public abstract class TableBase<T extends Record> extends Base implements Initializable {

    /*  ======================
        TABLE BASE FXML ELEMENTS
        ======================*/
    @FXML
    protected TableView<T> tableView;
    @FXML
    protected Button filterButton;
    @FXML
    protected Button addButton;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;

    /*  ======================
        STATIC LISTS FOR TABLE AND FORM USE
        ======================*/

    static ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    /**
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final TableColumn<T, Long> idColumn = new TableColumn<>("ID");

        idColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getId()).asObject());

        tableView.getColumns().add(idColumn);
        addColumns();
        updateTable();
        tableView.refresh();
    }

    /*  ======================
        SET TABLEVIEW STRUCTURE
        ======================*/

    /**
     * Required method for extended classes to generate columns for the TableView
     */
    protected abstract void addColumns();

    /**
     * Creates a TableColumn to load into the TableView
     * @param tClass - the model class
     * @param colName - the name of the field of the class
     * @return the String TableColumn to be created
     */
    protected TableColumn<T, String> getStringColumn(Class<T> tClass, String colName) {
        try {
            final Field field = tClass.getDeclaredField(colName);
            field.setAccessible(true);
            final String k = String.format("%s", toCapitalized(field.getName()));
            System.out.println(k);
            TableColumn<T,String> col = new TableColumn<>(k);
            col.setCellValueFactory(param -> {
                try {
                    return new SimpleStringProperty(field.get(param.getValue()).toString());
                } catch (IllegalAccessException ex) {
                    System.out.println(ex);
                }
                return null;
            });
            return col;
        } catch (NoSuchFieldException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Populates Table with class records
     *
     */
    protected abstract void updateTable();

    /**
     *
     * @return the selected item from the TableView
     */
    private T getSelection() {
        return tableView.getSelectionModel().getSelectedItem();
    }


    /**
     * Checks if the record to be updated exists in the database
     * @param record - the record to be queried
     * @return whether a record can be updated or not
     */
    protected boolean updatable(T record) {
        System.out.println("Checking for record existence in database.");

        boolean updatable = false;
        int id = record.getId();
        String table = record.getClass().getSimpleName().toLowerCase();
        String tableCapitalized = table.substring(0,1).toUpperCase() + table.substring(1);

        try {
            prepQuery("SELECT COUNT(*) AS count FROM " + table + "s WHERE " + tableCapitalized + "_ID = " + id);
            ResultSet rs = getResult();
            while (rs.next()) {
                System.out.println(rs.getInt("count"));
                if (rs.getInt("count")>0) {
                    updatable = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return updatable;
    }


    /*  ======================
        DELETE OBJECTS
        ======================
        call delete methods based on selection from table
        Parts can be deleted if it is assigned to a Product
        Products cannot be deleted if they have associated Parts
        */

    /**
     *
     * @return delete query statement
     */
    protected abstract String getDeleteStatement();

    /**
     * Only used by
     * @return the delete dependencies statement
     */
    protected abstract String getDeleteDependencies();

    /**
     * Initiate delete record process
     * @param record - the record to be deleted
     * @return delete status
     */
    public boolean deleteRecord(T record) {
        boolean deleted;
        // If record is a customer then delete appointment dependencies
        if (record.getClass() == Customer.class) {
            deleted = deleteFromDB(record, getDeleteDependencies());
                if (deleted) {
                    deleted = deleteFromDB(record, getDeleteStatement());
                } else {
                    errorMessage("Delete Dependencies", "Issues with deleting dependencies. Unable to delete customer.");
                }
        } else {
            deleted = deleteFromDB(record, getDeleteStatement());
        }
        return deleted;
    }

    /**
     *
     * @param record - the record to be deleted
     * @param statement - the delete statement
     * @return delete status
     */
    private boolean deleteFromDB(T record, String statement) {
        boolean deleted = false;
        int id = record.getId();
        String table = record.getClass().getSimpleName().toLowerCase();
        String tableCapitalized = table.substring(0,1).toUpperCase() + table.substring(1);

        System.out.println("Attempting to delete " + table + " record.");

        try {
            PreparedStatement ps = getConnection().prepareStatement(statement);
            ps.setInt(1, id);
            ps.executeUpdate();
            deleted = true;

        } catch (SQLException ex) {
            errorMessage(tableCapitalized + " Record Deletion", "Record could not be deleted.");
            printSQLException(ex);
        }
        return deleted;
    }


    /**
     *
     * @return list of all contacts
     */
    public static ObservableList<Contact> getAllContacts() {
        allContacts.clear();

        try {
            System.out.println("Querying Contacts Database.");
            prepQuery("SELECT * FROM contacts ORDER BY Contact_ID ASC");
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


    /**
     * loads the respective form window based on what the current controller is
     * @param actionEvent
     */
    @FXML
    protected void toRecords(ActionEvent actionEvent) {
        // Declare Local Variables
        // record which button was clicked
        Button pressed = (Button) actionEvent.getSource();
        System.out.println(pressed.getText());

        // capture selected record from table
        T obj = getSelection();
        String tClass = this.getClass().getSimpleName();


        if ( pressed.equals(addButton) || obj !=null) {

            if (tClass.contains("CustTable")) {
                loadCustomerRecord(obj, pressed);
            } else if (tClass.contains("ApptTable") || tClass.contains("Calendar")) {
                loadAppointmentRecord(obj, pressed);
            }
            updateTable();
        } else if ( pressed.equals(editButton) ) {
            infoMessage("Please select a record for modification.");
        }
    }

    /**
     * Loads Customer Form
     * @param obj - the selected customer to be loaded into the form
     * @param pressed - the button that was pressed
     */
    private void loadCustomerRecord(T obj, Button pressed) {
        FXMLLoader loader = setLoader("custRecord");
        try {
            Parent root = loader.load();
            CustRecord controller = loader.getController();
            controller.getParams(pressed.getText(), (Customer) obj);
            popupScene(root, "Customer Record");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads Appointment Form
     * @param obj - the selected appointment to be loaded into the form
     * @param pressed - the button that was pressed
     */
    private void loadAppointmentRecord(T obj, Button pressed) {
        FXMLLoader loader = setLoader("apptRecord");
        try {
            Parent root = loader.load();
            ApptRecord controller = loader.getController();
            controller.getParams(pressed.getText(), (Appointment) obj);
            popupScene(root, "Appointment Record");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // end of class
}

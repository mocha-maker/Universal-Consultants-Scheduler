package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import application.model.Record;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static application.util.Alerts.errorMessage;
import static application.util.Alerts.infoMessage;

public abstract class TableBase<T extends Record> extends Base implements Initializable {

    // TODO: set Base Table members
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


    protected RecordBase<T> recordForm;

    public static ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    public static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    /**
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*filterButton.setDisable(true);
        filterButton.setVisible(false);
        */
        final TableColumn<T, Long> idColumn = new TableColumn<>("ID");

        idColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getId()).asObject());

        tableView.getColumns().add(idColumn);
        addColumns();
        updateTable();
        tableView.refresh();
    }


    /*
    SET TABLEVIEW STRUCTURE
     */

    protected abstract void addColumns();

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
                    ex.printStackTrace();
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
     * @return
     */
    protected abstract void updateTable();


    private T getSelection() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    public ObservableList<T> getAllRecords() {
        return tableView.getItems();
    }

    public static <T> ObservableList<T> getAllRecords(T obj) {
        ObservableList<T> allRecords = FXCollections.observableArrayList();
        return allRecords;
    }



    protected boolean updatable(T record) {
        System.out.println("Checking for record existence in database.");
        boolean updatable = false;
        String table = record.getClass().getSimpleName().toLowerCase();
        System.out.println(table);
        int id = record.getId();
        String tableCapitalized = table.substring(0,1).toUpperCase() + table.substring(1);

        try {
            PreparedStatement ps = prepQuery("SELECT COUNT(*) AS count FROM " + table + "s WHERE " + tableCapitalized + "_ID = " + id);
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

    protected abstract String getDeleteStatement();

    protected abstract String getDeleteDependencies();

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

    private boolean deleteFromDB(T record, String statement) {
        boolean deleted = false;
        String table = record.getClass().getSimpleName().toLowerCase();


        // retrieve id from object
        int id = record.getId();
        String tableCapitalized = table.substring(0,1).toUpperCase() + table.substring(1);
        System.out.println("Attempting to delete " + table + " record.");

        try {
            // sqlquery to delete record
            PreparedStatement ps = getConnection().prepareStatement(statement);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println(deleted);
            deleted = true;

        } catch (SQLException ex) {
            errorMessage(tableCapitalized + " Record Deletion", "Record could not be deleted.");
            printSQLException(ex);
        }
        return deleted;
    }

    public static String getRelatedValue(int id, String table, String fieldName) {
        System.out.println("Retrieving related value for " + table);
        String fieldValue = "";
        try {
            prepQuery("SELECT * FROM " + table + " WHERE " + table.substring(0,1).toUpperCase() +"_ID = " + id);
            ResultSet rs = getResult();
            while (rs.next()) {
                fieldValue = rs.getString(fieldName);
                System.out.println("Field Value Retrieved = " + fieldValue);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return fieldValue;
    }

    /*
    EVENT HANDLING
     */

    // TODO: retrieve mode from button selection and feed to open record pop-up method

    private void viewRecord() {
        final T selected = getSelection();
        if (selected != null) {
            openRecord(selected);
        }
    }

    private void openRecord(T record) {

    }

    // TODO: Searchbar with auto filter

    @FXML
    private void addFilter() {}

    private void filterResults(ActionEvent actionEvent) {
        TextField source = (TextField)actionEvent.getSource();
        String q = source.getText(); // retrieve entered string from searchbox
        ObservableList<?> objs = FXCollections.observableArrayList();

        // check results
        if (objs.size() == 0) {
            try {
                //objs = lookupRecords(T,Integer.parseInt(q));
            } catch (NullPointerException ex) {
                ex.printStackTrace();

            }
        }
    }


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
    };

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

    public T getIndex(int id, ObservableList<T> allRecords) {
        T foundRecord = null;
        for (T r : allRecords) {
            if (r.getId() == id) {
                foundRecord = r;
                break;
            }
        }
        return foundRecord;
    }

    // end of class
}

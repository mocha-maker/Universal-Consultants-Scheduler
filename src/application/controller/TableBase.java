package application.controller;

import application.model.Customer;
import application.model.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static application.util.Alerts.errorMessage;

public abstract class TableBase<T extends Record> extends Base implements Initializable {

    // set Base Table members
    @FXML
    protected TableView<T> tableView;
    @FXML
    protected Button filterButton;
    @FXML
    private Button deleteButton;
    protected RecordBase<T> recordForm;




    public TableBase() {

    };

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


        //idColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getId()).asObject());
        //tableView.getColumns().add(idColumn);
        setColumns();
        updateTable();
        //tableView.refresh();
    }


    /*
    SET TABLEVIEW STRUCTURE
     */



    /**
     * Adds columns to the table according to class T
     */
    protected abstract void setColumns();

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
        boolean updatable = false;
        String table = record.getClass().getSimpleName().toLowerCase();
        try {
            PreparedStatement ps = prepQuery("SELECT COUNT(*) AS count FROM " + table + "s");
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

        System.out.println("Attempting to delete " + table + " record.");

        // retrieve id from object
        int id = record.getId();
        String tableCapitalized = table.substring(0,1).toUpperCase() + table.substring(1);

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

    private T lookupRecords(T obj, Integer id) {
        ObservableList<T> allRecords = getAllRecords(obj);
        T foundRecord = null;

        for (T r : allRecords) {
            if (r.getId() == id) {
                foundRecord = r;
                break;
            }
        }
        return foundRecord;
    }

/*    private ObservableList<T> lookupRecords(String q) {
        ObservableList<T> allRecords = getAllRecords(X);
        ObservableList<T> foundRecords = FXCollections.observableArrayList();

        for (T r : allRecords) {
            if (r.getName().toLowerCase().contains(q)) {
                foundRecords.add(r);
                break;
            }
        }
        return foundRecords;
    }*/



    // end of class
}

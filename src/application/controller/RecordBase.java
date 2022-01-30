package application.controller;

import application.model.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Abstract Class for Record Views
 * @param <T> - the model class to be used in the form
 */
public abstract class RecordBase<T extends Record> extends Base implements Initializable {

    @FXML
    Text formTitle = new Text();
    Boolean formTypeNew;
    T record = null;


    @FXML
    Button saveButton;

    /**
     * Pass parameters from Table Controller to Record Controllers
     * @param action - the button action that was taken
     * @param obj - the selected record from the table
     */
    protected abstract void getParams(String action,T obj) ;

    /**
     * Default Record initialization to set the form structures and listeners
     * @param url FXML Location
     * @param resourceBundle the FXML resource bundle to use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();
        addListeners();
    }

    /**
     * Add listeners for validation and boolean binding
     */
    protected abstract void addListeners();

    /**
     * Set combobox options
     */
    protected abstract void setComboBoxes();

    /**
     * General String Validation Method from a TextField
     * @param field - the TextField to update the style if invalid input
     * @param input - the input from the textfield
     * @param pattern - the regex pattern to match input to
     * @return
     */
    Boolean validateField(TextField field, String input, String pattern) {
        if (input.matches(pattern)) {
            field.setStyle("");
            return true;
        } else {
            field.setStyle("-fx-text-box-border: red;-fx-focus-color: red;;");
            System.out.println("Invalid Entry.");
            return false;
        }
    }

    /**
     * General String Validation Method from a ComboBox
     * @param field - the TextField to update the style if invalid input
     * @param input - the input from the textfield
     * @param pattern - the regex pattern to match input to
     * @return
     */
    Boolean validateField(ComboBox field, String input, String pattern) {
        if (input.matches(pattern)) {
            field.setStyle("");
            return true;
        } else {
            field.setStyle("-fx-text-box-border: red;-fx-focus-color: red;;");
            System.out.println("Invalid Entry.");
            return false;
        }
    }

    
    /*  ======================
        ADD RECORDS
        ======================
        Adds records to Database.
        */

    /**
     *
     * @return the insert SQL statement
     */
    protected abstract String getInsertStatement();

    /**
     * Initiate adding a record to the database
     * @param record - the record to be added
     * @param params - the list of values to be set
     * @return status of adding the record to the database
     */
    public boolean addRecord(T record, List<Object> params) {
        return addToDB(record,params);
    }

    /**
     * Add the record to the database as a private method
     * @param record - the record to be added
     * @param params - the parameters to be set
     * @return add to database status
     */
    private boolean addToDB(T record, List<Object> params) {
        int recordId = record.getId();
        String table = record.getClass().getSimpleName().toLowerCase();

        System.out.println(table + " ID is " + recordId);
        // sql query if customer record exists in db
        try {
            PreparedStatement ps = getConnection().prepareStatement(getInsertStatement());

            int paramCount = ps.getParameterMetaData().getParameterCount();

            System.out.println("There are " + paramCount + " parameters to set");

            System.out.println("Setting Parameters.");
            setParameters(ps,params);

            try {
                System.out.println("Executing Insert.");
                ps.executeUpdate();
                return true;

            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;

    }


    /*  ======================
        UPDATE OBJECTS
        ======================
        retrieves existing object using the record id
        */

    /**
     *
     * @return update SQL statement
     */
    protected abstract String getUpdateStatement();

    /**
     * Initiate record update process
     * @param record - the record to be updated
     * @param params - the values to update
     * @return update record status
     */
    public boolean updateRecord(T record, List<Object> params) {
        return updateDB(record,params);
    }

    /**
     *
     * @param record
     * @param params
     * @return
     */
    private boolean updateDB(T record, List<Object> params) {
        boolean updated = false;
        int recordId = record.getId();
        String table = record.getClass().getSimpleName().toLowerCase();

        System.out.println(table + " ID is " + recordId);
        // sql query if customer record exists in db
        try {
            PreparedStatement ps = getConnection().prepareStatement(getUpdateStatement());

            int paramCount = ps.getParameterMetaData().getParameterCount();

            System.out.println("There are " + paramCount + " parameters to set.");

            System.out.println("Setting Parameters.");
            setParameters(ps,params);

            try {
                System.out.println("Executing update.");
                ps.executeUpdate();
                updated = true;
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return updated;
    }


    /**
     * Queries table for the first available ID to use in a new record
     * @param table the table to be queried
     * @return availableID the first available ID in the table queried
     */
    public String genId(String table) {
        System.out.println("Generating an id...");
        int id = 1;
        if (table != null) {
            String tableName = table + "s";
            String tableId = table.substring(0, 1).toUpperCase() + table.substring(1) + "_ID";
            System.out.println("Querying " + tableId + " from table " + tableName);

            try {
                prepQuery("SELECT MAX(" + tableId + ") FROM " + tableName);
                ResultSet rs = getResult();

                while (rs.next()) {
                    id = rs.getInt("MAX(" + tableId + ")") + 1;

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Generated ID = " + id);
        }
        return String.valueOf(id);
    }

        private void setParameters(PreparedStatement ps, List<Object> parameters) throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i+1, parameters.get(i));
            }
        }
    }

    public T lookupRecords(T obj, ObservableList<T> allRecords) {
        T foundRecord = null;
        int id = obj.getId();
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



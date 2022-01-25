package application.controller;

import application.model.Record;
import application.model.User;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static application.util.Alerts.infoMessage;

public abstract class RecordBase<T extends Record> extends Base implements Initializable {

    @FXML
    Text formTitle = new Text();
    Boolean formTypeNew;
    T record = null;


    @FXML
    Button saveButton;
    
    protected abstract void getParams(String action,T obj) ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();
        addListeners();

    }

    protected abstract void addListeners();

    protected abstract void setComboBoxes();

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

    protected abstract String getInsertStatement();

    public void addRecord(T record, List<Object> params) {
        addToDB(record,params);
    }

    private void addToDB(T record, List<Object> params) {
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
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
            infoMessage(table + " Updated.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }


    /*  ======================
        UPDATE OBJECTS
        ======================
        retrieves existing object using the record id
        */

    protected abstract String getUpdateStatement();

    public boolean updateRecord(T record, List<Object> params) {
        boolean updated = updateDB(record,params);
        return updated;
    }

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

    public List<Object> getFieldValues(Object... args) {
        List<Object> values = FXCollections.observableArrayList();
        for (Object arg : args) {
            values.add(arg);
        }
        return values;
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



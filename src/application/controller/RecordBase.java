package application.controller;

import application.model.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static application.util.Alerts.infoMessage;

public abstract class RecordBase<T extends Record> extends Base implements Initializable {

    /*  ======================
        ADD RECORDS
        ======================
        Adds records to Database.
        */

    protected abstract String getInsertStatement();

    public void addRecord(T record, ObservableList<Object> params) {
        addToDB(record,params);
    }

    private void addToDB(T record, ObservableList<Object> params) {
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

    public void updateRecord(T record, ObservableList<Object> params) {
        updateDB(record,params);
    }

    private void updateDB(T record, ObservableList<Object> params) {
        int recordId = record.getId();
        String table = record.getClass().getSimpleName().toLowerCase();

        System.out.println(table + " ID is " + recordId);
        // sql query if customer record exists in db
        try {
            PreparedStatement ps = getConnection().prepareStatement(getUpdateStatement());

            int paramCount = ps.getParameterMetaData().getParameterCount();

            System.out.println("There are " + paramCount + " parameters to set");

            System.out.println("Setting Parameters.");
            setParameters(ps,params);

            try {
                System.out.println("Executing update.");
                ps.executeUpdate();
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
            infoMessage(table + " Updated.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ObservableList<Object> getParameters(Object... args) {
        ObservableList<Object> params = FXCollections.observableArrayList();
        for (Object arg : args) {
            params.add(arg);
        }
        return params;
    }

    private void setParameters(PreparedStatement ps, ObservableList<Object> parameters) throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i+1, parameters.get(i));
            }
        }
    }


    // end of class
}



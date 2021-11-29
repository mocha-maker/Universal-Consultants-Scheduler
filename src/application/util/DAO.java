package application.util;

import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public interface DAO {
    /*  ======================
        Initialize Data Structure
        ======================*/

     // ObservableList<Record> allRecords = FXCollections.observableArrayList(); // TODO: change to work with DB



    /* ======================
        GETTERS
       ======================*/

    // retrieve login information

    // retrieve customer list

    // retrieve contacts list

    // retrieve appointments list

    /* ======================
        ADDERS
       ======================*/

    // add new record
    default void executeInsert(String query, List<Object> arguments, BiConsumer<SQLException, Long> handler) {
        try (
                Connection connection = DBC.connection;
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            setArguments(stmt, arguments);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    handler.accept(null, generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            //printSQLException(ex);
            handler.accept(ex, null);
        }
    }

    void setArguments(PreparedStatement stmt, List<Object> arguments);

    /* ======================
        UPDATERS
       ======================*/

    // update customer

    // update appointment



    /* ======================
        DELETERS
       ======================*/

    // delete customer

    // delete appointment

}

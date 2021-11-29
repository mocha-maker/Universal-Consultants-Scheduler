package application.util;

import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public abstract class DAO {
    /*  ======================
        Initialize Data Structure
        ======================*/

    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList(); // TODO: change to work with DB



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

    // add new customer

    // add new appointment

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

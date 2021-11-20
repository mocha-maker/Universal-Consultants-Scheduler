package application.util;

import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public class DAO {
    /*  ======================
        Initialize Data Structure
        ======================*/

    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList(); // TODO: change to work with DB



    /* ======================
        GETTERS
       ======================*/

    public static ObservableList<Customer> getAllCustomers() { return allCustomers;     }

    /* ======================
        UPDATERS
       ======================*/

    /* ======================
        ADDERS
       ======================*/

    /* ======================
        DELETERS
       ======================*/
}

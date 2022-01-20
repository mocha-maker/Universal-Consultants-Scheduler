package application.util;

import application.model.User;
import javafx.collections.FXCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public abstract class DAO extends DBC {

    public static List<User> activeUser = FXCollections.observableArrayList();


    /*  ======================
        Initialize Data Structure
        ======================*/

    // private static String q;
    private static Statement sm;
    private static ResultSet rs;
    private static PreparedStatement ps;


    /* ======================
        QUERY LAMBDA
       ======================*/

    /**
     *
     * @param q
     */
    public static PreparedStatement prepQuery(String q) {
        ps = null;
        try {
            ps = DBC.getConnection().prepareStatement(q);
            sm = DBC.getConnection().createStatement();
            if(q.toLowerCase().startsWith("select"))
                rs=sm.executeQuery(q);
            if(q.toLowerCase().startsWith("delete")||
                    q.toLowerCase().startsWith("insert")||
                    q.toLowerCase().startsWith("update"))
                sm.executeUpdate(q);
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        return ps;
    }

    /**
     *
     * @return rs - ResultSet
     */
    public static ResultSet getResult(){
        return rs;
    }


    /* ======================
        Exception Handling
        ======================*/

    protected static void printSQLException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    /**
     * setActiveUser
     * Record Active User upon log in
     * @param loggedUser the user who successfully logged into this session
     */
    public static void setActiveUser(User loggedUser) {
        activeUser.add(loggedUser);
    }

    /**
     * getActiveUser
     * @return activeUser - the recorded user who logged into this session
     */
    public static User getActiveUser() {
        return activeUser.get(0);
    }

    //@Override
    private void executeQuery() {

    }

}

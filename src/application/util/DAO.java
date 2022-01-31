package application.util;

import application.model.User;
import javafx.collections.FXCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * This class interfaces with the database and connects the model to the view_controller
 */
public abstract class DAO extends DBC {

    /**
     * the active user holding list
     */
    private static final List<User> activeUser = FXCollections.observableArrayList();

    /**
     * The result set
     */
    private static ResultSet rs;


    /* ======================
        QUERY LAMBDA
       ======================*/

    /**
     * Prepares a query statement
     * Triages based on how the query starts.
     * @param q the query to prep
     * @return the prepared statement
     */
    public static PreparedStatement prepQuery(String q) {
        PreparedStatement ps = null;
        try {
            ps = DBC.getConnection().prepareStatement(q);

            Statement sm = DBC.getConnection().createStatement();

            // triage depending on query start to use the correct execute method
            if(q.toLowerCase().startsWith("select"))
                rs= sm.executeQuery(q);

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
     * Get result set
     * @return the result set from the prepped query
     */
    public static ResultSet getResult(){
        return rs;
    }


    /* ======================
        Exception Handling
        ======================*/

    /**
     * Print SQL Exception information
     * @param ex the SQL Exception given
     */
    public static void printSQLException(SQLException ex) {
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
        activeUser.clear();
        activeUser.add(loggedUser);
    }

    /**
     * getActiveUser
     * @return activeUser - the recorded user who logged into this session
     */
    public static User getActiveUser() {
        return activeUser.get(0);
    }


}

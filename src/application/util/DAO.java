package application.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public abstract class DAO extends DBC{
    /*  ======================
        Initialize Data Structure
        ======================*/

    // private static String q;
    private static Statement sm;
    private static ResultSet rs;


    /* ======================
        EXECUTE QUERY LAMBDA
       ======================*/


    public static void makeQuery(String q) {

        try {
            sm = DBC.getConnection().createStatement();

            if(q.toLowerCase().startsWith("select"))
                rs=sm.executeQuery(q);
            if(q.toLowerCase().startsWith("delete")||
                    q.toLowerCase().startsWith("insert")||
                    q.toLowerCase().startsWith("update"))
                sm.executeUpdate(q);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ResultSet getResult(){
        return rs;
    }


    /* ======================
        ADDERS
       ======================*/

    // add new record



    /* ======================
        UPDATERS
       ======================*/



    /* ======================
        Exception Handling
        ======================*/

    protected static void printSQLException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

}

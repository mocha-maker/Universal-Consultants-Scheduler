package application.util;

// From code repository

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class
 */
abstract class DBC {

    /* ======================
        SQL DATABASE VARIABLES
       ======================*/

    /**
     * the protocol
     */
    private static final String protocol = "jdbc";
    /**
     * the sql vendor
     */
    private static final String vendor = ":mysql:";
    /**
     * the location to connect to
     */
    private static final String location = "//localhost/";
    /**
     * the database to connect to
     */
    private static final String databaseName = "client_schedule";
    /**
     * build the JDBC Url
     */
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    /**
     * the jdbc driver
     */
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    /**
     * the database username for login
     */
    private static final String userName = "sqlUser"; // User
    /**
     * the database user password
     */
    private static final String password = "Passw0rd!"; // Password

    /**
     * Connection
     */
    private static Connection connection; // Connection Interface

    /**
     * Get current SQL connection.
     * Checks if there is an existing open connection. Creates a connection if none exists.
     * @return current SQL connection
     * @throws SQLException SQL exception is connection is unable to connect
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            openConnection();
        }
        return connection;
    }

    /**
     * Closes open connection.
     * Checks if a connection exists and closes it.
     * @return the closed connection
     * @throws SQLException SQL exception is connection is unable to disconnect
     */
    public static Connection exitConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            closeConnection();
        }
        return connection;
    }

    /**
     * Connects to the database
     */
    private static void openConnection() {

        System.out.println("Attempting to connect to database...");
        try {
            Class.forName(driver); // Locate driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * Terminate connection
     */
    public static void closeConnection() {
        System.out.println("Attempting to disconnect from database...");
        try {
            connection.close();
            System.out.println("Successfully Disconnected.");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

}

package application.util;

// From code repository

import java.sql.Connection;
import java.sql.DriverManager;


public class DBC {

    // Set SQL Database connector variables
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // User
    private static final String password = "Passw0rd!"; // Password
    public static Connection connection; // Connection Interface

    // Establish Connection
    public static void openConnection() {
        System.out.println("Attempting to connect to database...");
        try {
            Class.forName(driver); // Locate driver
            connection = DriverManager.getConnection (jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
    // Terminate Connection

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

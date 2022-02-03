package application.controller;

import application.model.User;
import application.util.Loc;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static application.util.Loc.*;

/**
 * Login Controller
 * Manages the login window and initiates database connection. Supports English and French localization.
 */
public class Login extends Base implements Initializable {

    /*  ======================
        FXML Variables
        ======================*/
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Label location;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set Locale and update labels
        location.setText(Loc.getZone().getID()); // timezone label

        // autoFillLoginAsTest();
        testTime();


    }

    private void testTime() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Local Time: " + now);
        System.out.println("Converted to Timestamp: " + convertTo(now,"UTC"));
        System.out.println("Timestamp from Local: " + toTimestamp(now));
    }


    /**
     * Used to speed through login during development
     */
    private void autoFillLoginAsTest() {
        usernameTF.setText("test");
        passwordTF.setText("test");
    }


    /**
     * Validates the given credentials against the database in a private method
     * @param rs the result set
     * @param password the password entered
     * @return the validated user id or -1
     */
    private int validateCredentials(ResultSet rs, String user, String password) {
        int result = -1;
                try {
                    if ((rs.next() && (rs.getString("Password").trim().equals(password.trim())) && (rs.getString("User_Name").trim().equals(user.trim())))) {
                        result = rs.getInt("User_ID");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        return result;
    }

    /**
     * Login action handler for when either the {Enter} key is pressed in the user or password textfields or when the "Sign In" button is pressed.
     * Retrieves the text entered and checks whether input is complete and valid
     */
    public void loginHandler() {
        // retrieve entered credentials
        final String user = usernameTF.getText();
        final String pass = passwordTF.getText();
        boolean result = false;

        // Check if text fields are both filled
        if (user.length() != 0 && pass.length() != 0) {

            String stmt = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = '" + user + "'";
            prepQuery(stmt);
            final int userID = validateCredentials(getResult(), user, pass);

            if (userID != -1) {
                System.out.println(user);
                System.out.println(pass);
                result = true;
                System.out.println("Successfully Logged In.");

                // Set Active User

                User loggedUser = new User(userID,usernameTF.getText());
                setActiveUser(loggedUser);

                // Change Window
                vController.loadMainWindow();

            } else {
                // If unable to validate credentials, give error, close connection, and reset input
                errorMessage("Login Error", Loc.getBundle().getString("error.loginInvalid"));
                closeConnection();
                usernameTF.setText("");
                passwordTF.setText("");
            }
            loginActivity(user, result);


        } else {
            // If at least one text field is empty, give error
            errorMessage("Login Error", Loc.getBundle().getString("error.loginEmpty"));
        }
    }

    /**
     * Generates a text file with appended login activity in UTC
     * @param user the username entered for the attempt
     * @param result whether the attempt was successful or not
     */
    protected static void loginActivity(String user, Boolean result) {

        // Convert result boolean to string
        String status = (result ? "Succeeded" : "Failed");

        // The time of the attempt converted to UTC
        String timestamp = dateToString(convertToZDT(LocalDateTime.now(),"UTC"), "yyyy-MM-dd hh:mm:ss a zzz");

        FileWriter logFile;

        try {
            logFile = new FileWriter("login_activity.txt", true);
            logFile.write("[" + timestamp + "] Login attempt by user: " + user + " - Attempt " + status + "\n");
            logFile.close();
            System.out.println("Login Activity Recorded.");

        } catch (IOException e) {

            System.out.println("In catch block.");
            e.printStackTrace();
        }
    }


    // End of class
}

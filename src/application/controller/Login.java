package application.controller;

import application.util.DateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class Login extends MainC implements Initializable {

    // Set FXML variables
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Label location;
    @FXML
    private Label username;
    @FXML
    private Label password;
    @FXML
    private Text welcome;
    @FXML
    private Button signin;

    // Validate Data Entry
    @FXML
/*    private void loginDataEntry(ActionEvent event) {
        final String username = usernameTF.getText();
        final String password = passwordTF.getText();
        if (username.length() != 0 && password.length() != 0) {
            final List<Object> arguments = new ArrayList<>();
            arguments.add(username);
            final long userId = executeQuery("SELECT User_ID, Password " +
                    "FROM users " +
                            "WHERE User_Name = ? " +
                            "LIMIT 1", arguments, this::validateCredentials);
            // log login attempt
            if (userId != -1) {
                // collect userId and load next scene
            } else {
                // display invalid credentials error
            }
        }
    }*/

    // Validate Credentials with Database
    private long validateCredentials(SQLException ex, ResultSet results) {
        long result = -1L;
        if (ex == null) {
            final String username = usernameTF.getText();
            final String password = passwordTF.getText();
            try {
                   // if (results.next() && (results.getString("Password").trim().equals(hashPassword().trim()) || username == "test" && password == "test"))
                {
                    result = results.getLong("User_ID");
                }
            }
                    catch (SQLException exc) {
                // print SQL Exception;
            }
        }
        return result;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Language Resource Pack Loaded: " + Locale.getDefault().getLanguage());
        // Set Locale and update labels
        welcome.setText(getRB().getString("login.welcome"));
        username.setText(getRB().getString("login.username") + ": ");
        password.setText(getRB().getString("login.password") + ": ");
        signin.setText(getRB().getString("login.login"));
        location.setText(DateTime.getTimeZone().getID()); // timezone label
    }

    // Go to next Screen

    // End of class
}

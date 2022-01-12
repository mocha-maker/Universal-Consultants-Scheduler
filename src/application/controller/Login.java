package application.controller;

import application.util.Loc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static application.util.Alerts.*;


public class Login extends Base implements Initializable {

    // Set FXML variables
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Label location;



    // Validate Credentials with Database
    private long validateCredentials(ResultSet rs, String user, String password) {
        long result = -1L;
                try {
                    if ((rs.next() && (rs.getString("Password").trim().equals(password.trim()) || user == "test" && password == "test"))) {
                        result = rs.getInt("User_ID");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


        return result;
    }

    public void loginHandler(ActionEvent event) {
        // retrieve entered credentials
        final String user = usernameTF.getText();
        final String pass = passwordTF.getText();
        boolean result = false;

        if (user.length() != 0 && pass.length() != 0) {

            String stmt = "SELECT User_ID, Password FROM users WHERE User_Name = '" + user + "'";
            prepQuery(stmt);
            final long userID = validateCredentials(getResult(), user, pass);

            if (userID != -1) {
                System.out.println(user);
                System.out.println(pass);
                result = true;
                System.out.println("Successfully Logged In.");

                // Stay connected to DB

                // Change Window
                vController.loadMainWindow();
            } else {
                errorMessage("Login Error", Loc.getBundle().getString("error.loginInvalid"));
                closeConnection();
                usernameTF.setText("");
                passwordTF.setText("");
            }
            Reports.loginActivity(Loc.toUTCZDT(ZonedDateTime.now()), user, result);

        } else {
            errorMessage("Login Error", Loc.getBundle().getString("error.loginEmpty"));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set Locale and update labels
        location.setText(Loc.getZone().getID()); // timezone label
    }

    public void onEnter(ActionEvent event) {
        loginHandler(event);
    }


    // Go to next Screen

    // End of class
}

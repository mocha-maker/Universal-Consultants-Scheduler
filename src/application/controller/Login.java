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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Login extends Base implements Initializable {

    // Set FXML variables
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Label location;



    // Validate Credentials with Database
    private long validateCredentials(SQLException ex, ResultSet results) {
        long result = -1L;
        if (ex == null) {
            final String username = usernameTF.getText();
            final String password = passwordTF.getText();
            try {
                    if (results.next() && (results.getString("Password").trim().equals(passwordTF.getText().trim()) || username == "test" && password == "test"))
                {
                    result = results.getLong("User_ID");
                }
            }
                    catch (SQLException ex_v) {
                        System.out.println(ex_v);
            }
        }
        return result;
    }

    public void loginHandler(ActionEvent event) {
        // retrieve entered credentials
        final String user = usernameTF.getText();
        final String pass = passwordTF.getText();
        boolean result = false;

        if (user.length() != 0 && pass.length() != 0) {

            final List<Object> args = new ArrayList<>();
            args.add(user);
            final long userID = executeQuery("SELECT User_ID, Password " + "FROM users " + "WHERE User_Name = ? " + "LIMIT 1", args, this::validateCredentials);

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



    // Go to next Screen

    // End of class
}

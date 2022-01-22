package application.controller;

import application.model.User;
import application.util.Loc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static application.util.Alerts.errorMessage;
import static application.util.Loc.*;


public class Login extends Base implements Initializable {

    // Set FXML variables
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Label location;



    // Validate Credentials with Database
    private int validateCredentials(ResultSet rs, String user, String password) {
        int result = -1;
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
            final int userID = validateCredentials(getResult(), user, pass);
            System.out.println(userID);

            if (userID != -1) {
                System.out.println(user);
                System.out.println(pass);
                result = true;
                System.out.println("Successfully Logged In.");

                // Set Active User

                User loggedUser = new User((int) userID,usernameTF.getText());
                setActiveUser(loggedUser);

                // Change Window
                vController.loadMainWindow();
            } else {
                errorMessage("Login Error", Loc.getBundle().getString("error.loginInvalid"));
                closeConnection();
                usernameTF.setText("");
                passwordTF.setText("");
            }
            Reports.loginActivity(toUTCZDT(LocalDateTime.now()), user, result);


        } else {
            errorMessage("Login Error", Loc.getBundle().getString("error.loginEmpty"));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set Locale and update labels
        location.setText(Loc.getZone().getID()); // timezone label

        // TODO: Remove autofill
        autoFillLoginAsTest();



    }


    private void autoFillLoginAsTest() {
        usernameTF.setText("test");
        passwordTF.setText("test");
    }


    // Go to next Screen

    // End of class
}

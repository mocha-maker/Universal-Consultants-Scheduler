package application.controller;

import application.util.Loc;

import java.net.URL;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.ResourceBundle;

public class Main extends Base {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // TODO: Check if there are any appointments within 15 mins upon login
        // Run Loc getLocalTime and compare to appointment local date-times.
        System.out.println("Checking for upcoming Appointments.");

    }

    public void appointmentIn15() {
        // Get Local Time

        // Run Query on Appointments based on returned date and time

        // Check if Result Set is not empty

        // Trigger an Alert
    }


}

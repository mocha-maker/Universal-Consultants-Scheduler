package application.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Reports extends Base {

    // CONTACT REPORTS
    // Appointment Summary by Month

    // Appointment Summary by Type

    // Schedule

    // CUSTOMER REPORTS
    // Appointments Summary by Month

    // Appointments Summary by Type


    // Login Activity Report
    public static void loginActivity(ZonedDateTime time, String user, Boolean result) {
        FileWriter logFile = null;
        String status = (result ? "Succeeded" : "Failed");
        String timestamp = time.format(DateTimeFormatter.ofPattern("MM-dd-YYYY hh:mm:ss a zzz"));
        try {
            logFile = new FileWriter("login_activity.txt", true);
            logFile.write("[" + timestamp + "] Login attempt by user: " + user + " - Attempt " + status + "\n");
            logFile.close();
        } catch (IOException e) {
            System.out.println("In catch block.");
            e.printStackTrace();
        }
    }


    // PRINT JOB
}

package application.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static application.util.Loc.dateToString;

public class Reports extends Base {

    // CONTACT REPORTS
    // Appointment Summary by Month

    // Appointment Summary by Type

    // Schedule

    // CUSTOMER REPORTS
    // Appointments Summary by Month

    // Appointments Summary by Type


    // Login Activity Report
    public static void loginActivity(ZonedDateTime zonedDateTime, String user, Boolean result) {
        FileWriter logFile;
        String status = (result ? "Succeeded" : "Failed");
        String timestamp = dateToString(zonedDateTime, "yyyy-MM-dd hh:mm:ss a zzz");
        System.out.println(timestamp);
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


    // PRINT JOB
}

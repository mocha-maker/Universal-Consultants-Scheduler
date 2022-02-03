package application.controller;

import application.util.Loc;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import static application.util.Loc.*;

/**
 * Main Window Controller.
 * Manages the main window that includes the sidebar and the main viewing node.
 */
public class MainView extends Base {

    @FXML
    Text activeUser;
    @FXML
    Text localTime;
    @FXML
    Text localZone;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set user text to menu
        try {
            activeUser.setText("Welcome " + getActiveUser().toString() + "!");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        // Set live clock
        setClock();

        // Loads Calendar on first load
        try {
            switchScene("cal");
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkUpcomingAppts();

    }

    /**
     * Check if there are any appointments within 15 mins upon login and show the respective popup message
     */
    private void checkUpcomingAppts() {
        // Get Local Time
        LocalDateTime nowUTC = LocalDateTime.now();
        Timestamp now = toTimestamp(nowUTC);
        System.out.println("Timestamp - " + now);
        Timestamp inQuarterHour = toTimestamp(nowUTC.plusMinutes(15));
        System.out.println("Timestamp - " + inQuarterHour);

        long timeDifference = 15L;

        StringBuilder printAppointments = new StringBuilder();


        try {
            System.out.println("Checking for upcoming Appointments.");
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM appointments WHERE Start >= ? AND Start <= ?");
            ps.setTimestamp(1,now);
            ps.setTimestamp(2,inQuarterHour);
            ps.executeQuery();

            ResultSet rs = ps.getResultSet();

            // Check if Result Set is not empty
            System.out.println("Fetch size = " + rs.getFetchSize());
            while (rs.next()) {
                LocalDateTime upcoming = timeStampToLocal(rs.getTimestamp("Start"));
                System.out.println("Upcoming Timestamp to Local: " + upcoming);

                // get earliest upcoming appointment time difference
                if (timeDifference > nowUTC.until(upcoming, ChronoUnit.MINUTES)) {
                    timeDifference = nowUTC.until(upcoming, ChronoUnit.MINUTES);
                }
                printAppointments.append("\nAppointment #").append(rs.getInt("Appointment_ID")).append(" for ").append(dateToString(upcoming, "MMM d, YYYY hh:mm a"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (!printAppointments.toString().equals("")) {
            // Popup upcoming appointments and when the earliest one is.
            printAppointments.insert(0, "You have an appointment with in " + timeDifference + " minutes!\n");
            infoMessage(printAppointments.toString());

        } else { // no appointments
            infoMessage("You have no upcoming appointments.");
        }
    }

    /**
     * Creates a live clock to appear in the sidebar that is in the system's local timezone
     * lambda: allows customized formatting of a clock animation in text form with less lines of code
     */
    private void setClock() {

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            // set time to use
            LocalDateTime currentTime = LocalDateTime.now();
            // format clock text
            localTime.setText(dateToString(currentTime,"hh:mm:ss a"));
        }),
                // set update frequency
                new KeyFrame(Duration.seconds(1))
        );

        // set how long animation will last
        clock.setCycleCount(Animation.INDEFINITE);

        // start clock animation
        clock.play();

        // set local timezone text label
        localZone.setText(Loc.getZone().getID());
    }



}

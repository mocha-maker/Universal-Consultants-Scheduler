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

        // set user text to menu
        try {
            activeUser.setText("Welcome " + getActiveUser().toString() + "!");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        // set live clock
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
     * TODO: Check if there are any appointments within 15 mins upon login
     */
    private void checkUpcomingAppts() {
        // Get Local Time
        LocalDateTime nowUTC = convertTo(LocalDateTime.now(),"UTC");
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
                LocalDateTime upcoming = convertTo(timeStampToLocal(rs.getTimestamp("Start")), "UTC");

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

    private void setClock() {
        // Live Clock
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            localTime.setText(dateToString(currentTime,"hh:mm:ss a"));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        localZone.setText(Loc.getZone().getID());
    }



}

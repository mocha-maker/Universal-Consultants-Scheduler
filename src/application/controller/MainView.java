package application.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Run Loc getLocalTime and compare to appointment local date-times.
        checkUpcomingAppts();

        try {
            activeUser.setText(getActiveUser().toString());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        setClock();

        try {
            switchScene("cal"); // Loads Calendar on first load
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO: Check if there are any appointments within 15 mins upon login
     */
    public void checkUpcomingAppts() {
        System.out.println("Checking for upcoming Appointments.");
        // Get Local Time
        Timestamp now = toTimestamp(LocalDateTime.now());
        // Run Query on Appointments based on returned date and time


        // Check if Result Set is not empty

        // Trigger an Alert
    }

    protected void setClock() {
        // Live Clock
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            localTime.setText(dateToString(currentTime,"hh:mm:ss a"));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


}

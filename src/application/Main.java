package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.util.ConnectDB;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class Main extends Application {
    /**
     * Default application point of execution.
     * @param args the supplied command-line arguments
     */
    public static void main(String[] args) {

        //ConnectDB.openConnection(); // Establish Connection to SQL DB

        TimeDate();
        ConvertTime(ZonedDateTime.now());


        // launch(args);

        //ConnectDB.closeConnection(); // Close Connection to SQL DB

    }
    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println("Loading Application");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view_controller/main_login.fxml")));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }



    public static void TimeDate() {
        // Timezone App

        LocalDate parisDate = LocalDate.of(2021,11,19);
        LocalTime parisTime = LocalTime.of(05, 00);
        ZoneId parisZoneID = ZoneId.of("Europe/Paris");

        ZonedDateTime parisZDT = ZonedDateTime.of(parisDate, parisTime, parisZoneID);
        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        System.out.println("Local Zone ID: " + localZoneId);

        Instant parisToUTCInstant = parisZDT.toInstant();
        ZonedDateTime parisInstantToZDT = parisToUTCInstant.atZone(ZoneId.of("UTC"));
        ZonedDateTime parisToLocalZDT = parisZDT.withZoneSameInstant(localZoneId);
        ZonedDateTime UTCToLocalZDT = parisToUTCInstant.atZone(localZoneId);

        //System.out.println("Local: " + ZonedDateTime.now());
        System.out.println("Paris: " + parisZDT);

        System.out.println("Paris -> Local: " + parisToLocalZDT);
        System.out.println("Paris -> UTC: " + parisToUTCInstant);
        System.out.println("Paris -> UTC: " + parisInstantToZDT.toLocalTime());
        System.out.println("UTC -> Local: " + UTCToLocalZDT.toLocalDate());
    }

/*    public ZonedDateTime toLocal(zdt) {

    }*/

    public static String ConvertTime(ZonedDateTime zonedTime) {
        String convertedTime = zonedTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        System.out.println("12H time: " + convertedTime);
        return convertedTime;
    }

}

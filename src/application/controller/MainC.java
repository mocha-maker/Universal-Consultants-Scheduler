package application.controller;

import application.util.DateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainC implements Initializable {
    @FXML
    private AnchorPane mainPane; // main viewing pane
    private Pane view; // child pane of mainPane
    private String appf = "/application/fxml/";

    /**
     * Initialize mainController. This is the main application controller for the main view that contains the menu and loadable pages.
     *
     * @param url FXML Location
     * @param resourceBundle the FXML resource bundle to use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Main Controller loaded.");
        System.out.println("");
    }

    // Scene Management
    /**
     * Generic Scene Switcher. This is used as the template for switching scenes.
     *
     */
    public void switchScene(MouseEvent event, String fileName) throws IOException {
        view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(appf + fileName + ".fxml")));
        mainPane.getChildren().setAll(view);
    }

    @FXML
    public void getView(Pane view) {
        System.out.println("Loading Login Page...");
        mainPane.getChildren().setAll(view);
    }

    @FXML
    private void menuLogout(MouseEvent event) throws IOException {
        // TODO: add a confirmation msg
        System.out.println("Logging out...");
        // TODO: disconnect from database
        switchScene(event,"login");
    }

    @FXML
    private void menuAppointment(MouseEvent event) throws IOException {
        switchScene(event,"appt");
    }

    @FXML
    private void menuCalendar(MouseEvent event) throws IOException {
        switchScene(event,"cal");
    }

    @FXML
    private void menuCustomers(MouseEvent event) throws IOException {
        switchScene(event,"cust");
    }

    @FXML
    private void menuReports(MouseEvent event) throws IOException {
        switchScene(event,"reports");
    }


    public static ResourceBundle getRB() {

/*        Locale french = new Locale("fr","FR");
        Locale english = new Locale("en","EN");*/
        ResourceBundle rb = ResourceBundle.getBundle("/application/resources/lang" , Locale.getDefault());
/*        if(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("fr")) {

            return rb;
        }*/
        return rb;
    }

    public static void executeQuery() {

    }

    // end of class
}


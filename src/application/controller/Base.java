package application.controller;

import application.util.DAO;
import application.util.Loc;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class Base extends DAO implements Initializable {
    @FXML
    private AnchorPane mainPane; // main viewing pane
    private Pane view; // child pane of mainPane
    private String appf = "/application/view/";
    private ResourceBundle resourceBundle;
    protected View vController;

    /**
     * Initialize Base. This is the main application controller for the main view that contains the menu and loadable pages.
     *
     * @param url FXML Location
     * @param resourceBundle the FXML resource bundle to use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Scene Management

    /**
     * Generic Scene Switcher. This is used as the template for switching scenes.
     *
     */

    protected void setViewController(View vController) {
        this.vController = vController;
    }

    public void switchScene(MouseEvent event, String fileName) throws IOException {
        view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(appf + fileName + ".fxml")));
        mainPane.getChildren().setAll(view);
    }

    public void showMain(final Scene scene, final Stage primaryStage) {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("views/main.fxml"), Loc.getBundle());
            scene.setRoot(loader.load());
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("error opening main view:");
            System.out.println(e);
        }
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



    /*  ======================
        DIALOGS
        ======================
        Dialog pop-ups for errors and confirmations
        */

    /**
     * Error Alert Constructor
     * @param title the dialog title
     * @param msg the message to show in dialog
     */
    public void errorMessage(String title, String msg) {
        // Create alert and set parameters
        Alert error = new Alert(Alert.AlertType.ERROR);
        if (!error.isShowing()) { // prevent additional dialogs
            error.setTitle("ERROR: " + title);
            error.setHeaderText("");
            error.setContentText(msg);
            error.showAndWait();
        }
    }
    /**
     * Warning Alert Constructor
     * @param title the dialog title
     * @param msg the message to show in dialog
     */
    public void warningMessage(String title, String msg) {
        // Create alert and set parameters
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (!warning.isShowing()) { // prevent additional dialogs
            warning.setTitle("WARNING: " + title);
            warning.setHeaderText("");
            warning.setContentText(msg);
            warning.showAndWait();
        }
    }

    /**
     * Info Alert Constructor
     * @param msg the message to show in dialog
     */
    public void infoMessage(String msg) {
        // Create alert and set parameters
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Information");
        error.setHeaderText("");
        error.setContentText(msg);
        error.showAndWait();
    }

    /**
     * Simple Confirmation Dialog
     * @param title the title of the dialog
     * @param msg the message to show in dialog
     * @return boolean confirmation input
     */
    public boolean confirmMessage(String title, String msg) {

        // Create alert and set parameters
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Please Confirm " + title);
        confirm.setHeaderText("");
        confirm.setContentText(msg);

        // buttons to include in dialog
        ButtonType continueButton = new ButtonType("Continue");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(continueButton,cancelButton); // creates the buttons in dialog

        Optional<ButtonType> result = confirm.showAndWait(); // show dialog
        return result.get() == continueButton; // user chose CANCEL or closed the dialog
    }


    // end of class
}


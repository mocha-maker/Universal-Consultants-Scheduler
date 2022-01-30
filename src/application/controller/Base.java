package application.controller;

import application.util.DAO;
import application.util.Loc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Abstract Base Controller Class used for all controllers
 */
public abstract class Base extends DAO implements Initializable {
    @FXML
    private AnchorPane mainPane; // main viewing pane
    private final String appf = "/application/view/";
    final ResourceBundle rb = Loc.getBundle();
    protected View vController;
/*    private Button backButton;
    private Stack<Pane> sceneHistory;*/

    /**
     * Initialize Base. This is the main application controller for the main view that contains the menu and loadable pages.
     *
     * @param url FXML Location
     * @param resourceBundle the FXML resource bundle to use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /* ======================
        SCENE MANAGEMENT
       ======================*/

    /**
     *
     *
     * @param vController
     */
    protected void setViewController(View vController) {
        this.vController = vController;
    }

    protected FXMLLoader setLoader(String fileName) {
        return new FXMLLoader(getClass().getResource(appf + fileName + ".fxml"));
    }

    /**
     * Changes the scene on the main pane without opening a new window
     * @param fileName - the name of the FXML file to be opened
     * @throws IOException
     */
    public void switchScene(String fileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(appf + fileName + ".fxml"));
        // child pane of mainPane
        Pane view = loader.load();
        loader.getController();
        mainPane.getChildren().setAll(view);

    }

    /**
     * Opens a new window
     * @param root - the Parent Loaded
     * @param title - the title to appear on the window
     * @throws IOException
     */
    public void popupScene(Parent root, String title) throws IOException {
        Stage stage = new Stage(); // new Stage aka Window

        // Create New Scene
        Scene scene = new Scene(root);
        stage.setTitle(title); // Window Title
        stage.setScene(scene); // Load Scene on Stage
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait(); // Show Stage
    }


    /**
     * Switches scene to appointment table view
     * @throws IOException
     */
    @FXML
    private void menuAppointments() throws IOException {
        switchScene("appt");
    }

    /**
     * Switches scene to calendar view
     * @throws IOException
     */
    @FXML
    private void menuCalendar() throws IOException {
        switchScene("cal");
    }

    /**
     * Switches scene to customer table view
     * @throws IOException
     */
    @FXML
    private void menuCustomers() throws IOException {
        switchScene("cust");
    }

    /**
     * Switches scene to reports view
     * @throws IOException
     */
    @FXML
    private void menuReports() throws IOException {
        switchScene("reports");
    }

    /**
     * Closes the database connection and the main window then loads the login window.
     */
    @FXML
    private void menuLogout() {
        closeConnection();
        try {
            vController.loadLoginWindow();
        } catch (Exception e) {
            errorMessage("Loading Error", "Failed to load login window.");
        }
    }

    /**
     * Generic Pattern Validator
     * @param field the text field to validate
     * @param pattern the regex pattern to match input against
     * @param msg the message to display on error
     * @return validation status (true or false)
     */
    public boolean validation(TextField field, String pattern, String msg) {
        if (!field.getText().matches(pattern)) {
            errorMessage("", "Input contains invalid characters. " + msg);
            return false;
        }
        return true;
    }

    /**
     * Used to close the window on forms
     * @param event - on exit
     * @throws IOException
     */
    @FXML
    public void exitButton(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Cancel Button on returns to the previous scene.
     * @param actionEvent when clicking the "Cancel" button
     * @throws IOException exceptions unload
     */
    @FXML
    private void cancelButton(ActionEvent actionEvent) {
        Boolean confirm = confirmMessage("Cancel Activity","Are you sure you want to cancel? \nAny changes you've made will not be saved.");

        if (confirm) {
            exitButton(actionEvent);
        }
    }

    /**
     * Quick add arguments to a list
     * @param args
     * @return the list of arguments
     */
    public List<Object> toList(Object... args) {
        return List.of(args);
    }

    /**
     * Formats a string to be capitalized
     * @param s - the string to be formatted
     * @return the formatted string
     */
    public String toCapitalized(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }


    /* DIALOG BOXES */

    /**
     * Error Alert Constructor
     * @param title the dialog title
     * @param msg the message to show in dialog
     */
    public static void errorMessage(String title, String msg) {
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
    final void warningMessage(String title, String msg) {
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
    public static void infoMessage(String msg) {
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
    public static boolean confirmMessage(String title, String msg) {

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


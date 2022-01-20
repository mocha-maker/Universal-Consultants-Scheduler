package application.controller;

import application.util.DAO;
import application.util.Loc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.errorMessage;

/**
 * Abstract Base Controller Class
 */
public abstract class Base extends DAO implements Initializable {
    @FXML
    private AnchorPane mainPane; // main viewing pane
    private Pane view; // child pane of mainPane
    private String appf = "/application/view/";
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(appf + fileName + ".fxml"));
        return loader;
    }

    public void switchScene(String fileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(appf + fileName + ".fxml"));
        view = loader.load();
        mainPane.getChildren().setAll(view);
        loader.getController();
    }

    public void popupScene(Parent root, String title) throws IOException {
        Stage stage = new Stage(); // new Stage aka Window

        // Create New Scene
        Scene scene = new Scene(root);
        stage.setTitle(title); // Window Title
        stage.setScene(scene); // Load Scene on Stage
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait(); // Show Stage
    }


    @FXML
    private void menuAppointment(MouseEvent event) throws IOException {
        switchScene("appt");
    }

    @FXML
    private void menuCalendar(MouseEvent event) throws IOException {
        switchScene("cal");
    }

    @FXML
    private void menuCustomers(MouseEvent event) throws IOException {
        switchScene("cust");
    }

    @FXML
    private void menuReports(MouseEvent event) throws IOException {
        switchScene("reports");
    }

    @FXML
    private void menuLogout(MouseEvent event) {
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

// TODO: Remove Back Buttons
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
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    public List<Object> toList(Object... args) {
        return List.of(args);
    }

    // end of class
}


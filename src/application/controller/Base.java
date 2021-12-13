package application.controller;

import application.util.Alerts;
import application.util.DAO;
import application.util.Loc;
import javafx.collections.ObservableArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static application.util.Alerts.*;

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

    public void switchScene(MouseEvent event, String fileName) throws IOException {
        view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(appf + fileName + ".fxml")));
        mainPane.getChildren().setAll(view);
        // sceneHistory.add(view); TODO: back button functionality
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

    @FXML
    private void menuLogout(MouseEvent event) {
        closeConnection();
        try {
            vController.loadLoginWindow();
        } catch (Exception e) {
            errorMessage("Loading Error", "Failed to load login window.");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
/*        sceneHistory.pop();
        mainPane.getChildren().setAll(sceneHistory.pop());*/

    }






    // end of class
}


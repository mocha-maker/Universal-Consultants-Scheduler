package application.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class manages the Stages and Scenes to ensure the proper controllers are loaded for each scene.
 */
public final class View extends Base {
    private final Scene scene;
    private final Stage primaryStage;


    public View(final Scene scene, final Stage primaryStage) {
        this.scene = scene;
        this.primaryStage = primaryStage;
    }

    public void loadLoginWindow() {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/login.fxml"), rb);
            scene.setRoot(loader.load());
            loader.<Login>getController().setViewController(this);
            primaryStage.setTitle(rb.getString("app.title"));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception ex) {
            System.out.println("Error opening Login Window");
            ex.printStackTrace();
        }
    }

    public void loadMainWindow() {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/main.fxml"), rb);
            scene.setRoot(loader.load());
            Main mainController = loader.getController();
            mainController.setViewController(this);
            primaryStage.show();
        } catch (Exception ex) {
            System.out.println("Error opening Main Window:");
            ex.printStackTrace();
        }
    }


    public void loadPopup(ActionEvent actionEvent, String title, Parent root) {
        Stage stage = new Stage(); // new Stage
        setViewController(this);
        // Create New Scene
        Scene scene = new Scene(root);

        stage.setTitle(title); // Title
        stage.setScene(scene); // Load Scene on Stage
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait(); // Show Stage
    }

    public Stage getStage() {
        return primaryStage;
    }

}

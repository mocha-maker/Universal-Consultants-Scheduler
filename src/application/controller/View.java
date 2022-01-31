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

            // set contorller class
            loader.<Login>getController().setViewController(this);

            // prepare login stage
            primaryStage.setTitle(rb.getString("app.title"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
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
            // set controller class
            MainView mainViewController = loader.getController();
            mainViewController.setViewController(this);

            // prepare main stage
            primaryStage.setTitle(rb.getString("app.title"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception ex) {
            System.out.println("Error opening Main Window:");
            ex.printStackTrace();
        }
    }


}

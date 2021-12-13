package application.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class manages the Stages and Scenes to ensure the proper controllers are loaded for each scene.
 */
public final class View extends Base {
    private final Scene scene;
    private final Stage primaryStage;


    public View(final Scene scene, final Stage primaryStage) {
        this.scene = scene;
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                closeConnection();
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void loadLoginWindow() throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/login.fxml"), rb);
        scene.setRoot(loader.load());
        loader.<Login>getController().setViewController(this);
        primaryStage.setTitle(rb.getString("app.title"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadMainWindow() {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/main.fxml"), rb);
            scene.setRoot(loader.load());
            final Main mainController = loader.getController();
            mainController.setViewController(this);
            primaryStage.show();
        } catch (Exception ex) {
            System.out.println("Error opening Main Window:");
            System.out.println(ex);
        }
    }

    public Stage getStage() {
        return primaryStage;
    }

}

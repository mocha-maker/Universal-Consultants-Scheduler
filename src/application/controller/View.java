package application.controller;

import application.util.Loc;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public final class View extends Base {
    private final Scene scene;
    private final Stage primaryStage;
    private final ResourceBundle rb = Loc.getBundle();

    public View(final Scene scene, final Stage primaryStage) {
        this.scene = scene;
        this.primaryStage = primaryStage;
    }

    public void loadLoginWindow() throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"), rb);
        scene.setRoot(loader.load());
        loader.<Login>getController().setViewController(this);
        primaryStage.setTitle(rb.getString("app.title"));
    }

    public void loadMainWindow() {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"), rb);
            scene.setRoot(loader.load());
            final Main mainController = loader.getController();
            mainController.setViewController(this);
            primaryStage.show();
        } catch (Exception ex) {
            System.out.println("Error opening Main Window:");
            System.out.println(ex);
        }
    }

}

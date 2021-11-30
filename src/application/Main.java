package application;

import application.controller.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

import static application.util.Loc.*;

public class Main extends Application {

    /**
     * Default application point of execution.
     * @param args the supplied command-line arguments
     */
    public static void main(String[] args) {

        launch(args);

    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        // locale test
        // Locale.setDefault(new Locale("fr","CA"));


        setLocaleBundle();
        System.out.println("Loading Application");
        final Scene scene = new Scene(new AnchorPane());
        View vController = new View(scene, primaryStage);
        vController.loadLoginWindow();

    }

// end
}

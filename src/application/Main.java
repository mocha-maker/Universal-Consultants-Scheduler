package application;

import application.controller.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

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
        // testLang(); // select language pack to load

        setLocaleBundle();
        System.out.println("Loading Application...");
        final Scene scene = new Scene(new AnchorPane());
        View vController = new View(scene, primaryStage);
        vController.loadLoginWindow();

    }

    // Testing functions for Language packs
    public void testLang()  {
        Scanner sel = new Scanner(System.in);
        System.out.println("Enter language package to use (1 = English 2 = French)");
        String lang = sel.nextLine();
        switch(lang) {
            case "2":
                Locale.setDefault(new Locale("fr", "CA"));
                break;
            case "1":
                break;
            default:
                Locale.setDefault(new Locale("en", "US"));
        }
    }

// end
}

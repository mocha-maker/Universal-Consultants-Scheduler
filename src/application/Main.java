package application;

import application.controller.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.util.Locale;
import java.util.Scanner;

import static application.util.Loc.*;

/**
 * Main Application.
 */
public class Main extends Application {

    /**
     * Default application point of execution.
     * @param args the supplied command-line arguments
     */
    public static void main(String[] args) {

        launch(args);

    }
    @Override
    public void start(Stage primaryStage) {

        // overrideSystemDefaults();

        setLocaleBundle();
        System.out.println("Loading Application...");
        final Scene scene = new Scene(new AnchorPane());
        View vController = new View(scene, primaryStage);
        vController.loadLoginWindow();

    }

    /*  ======================
        FOR TESTING ONLY
        ======================*/

    private void overrideSystemDefaults() {
        testTimeZone();
        testLang();
    }

    private void testTimeZone() {
        String timezone = newScanner("Enter a timezone E.g. America/Los_Angeles");
        System.setProperty("user.timezone", timezone);
    }

    private void testLang()  {
        String lang = newScanner("Enter language package to use (1 = English 2 = French)");

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

    private String newScanner(String msg) {
        Scanner sel = new Scanner(System.in);
        System.out.println(msg);
        return sel.nextLine();
    }

// end
}

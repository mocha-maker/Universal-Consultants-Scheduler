package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.time.ZonedDateTime;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/login.fxml"), getBundle());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(getBundle().getString("app.title"));
        primaryStage.setScene(scene);
        primaryStage.show();



    }








}

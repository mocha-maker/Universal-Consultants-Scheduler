package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.util.ConnectDB;

import java.util.Objects;

public class Main extends Application {
    /**
     * Default application point of execution.
     * @param args the supplied command-line arguments
     */
    public static void main(String[] args) {

        //ConnectDB.openConnection(); // Establish Connection to SQL DB
        launch(args);
        //ConnectDB.closeConnection(); // Close Connection to SQL DB

    }
    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println("Loading Application");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view_controller/sample.fxml")));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }





}

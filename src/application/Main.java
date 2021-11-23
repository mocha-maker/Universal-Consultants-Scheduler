package application;

import application.controller.MainC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

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
        // locale test
        Locale.setDefault(new Locale("fr"));

        System.out.println("Loading Application");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Universal Consultants Scheduling Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();




/*        mainController controller = loader.<mainController>getController();
        Pane view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/login.fxml")));
        controller.getView(view);*/

    }








}

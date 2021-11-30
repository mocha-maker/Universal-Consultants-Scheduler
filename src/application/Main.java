package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        URL url = getClass().getResource("view/login.fxml");

        FXMLLoader loader = new FXMLLoader(url, getBundle());
        System.out.println(getBundle().getString("login.login"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(getBundle().getString("app.title"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }

// end
}

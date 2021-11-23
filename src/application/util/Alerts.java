package application.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {

    /*  ======================
        DIALOGS
        ======================
        Dialog pop-ups for errors and confirmations
        */

    /**
     * Error Alert Constructor
     * @param title the dialog title
     * @param msg the message to show in dialog
     */
    public void errorMessage(String title, String msg) {
        // Create alert and set parameters
        Alert error = new Alert(Alert.AlertType.ERROR);
        if (!error.isShowing()) { // prevent additional dialogs
            error.setTitle("ERROR: " + title);
            error.setHeaderText("");
            error.setContentText(msg);
            error.showAndWait();
        }
    }
    /**
     * Warning Alert Constructor
     * @param title the dialog title
     * @param msg the message to show in dialog
     */
    public void warningMessage(String title, String msg) {
        // Create alert and set parameters
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (!warning.isShowing()) { // prevent additional dialogs
            warning.setTitle("WARNING: " + title);
            warning.setHeaderText("");
            warning.setContentText(msg);
            warning.showAndWait();
        }
    }

    /**
     * Info Alert Constructor
     * @param msg the message to show in dialog
     */
    public void infoMessage(String msg) {
        // Create alert and set parameters
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Information");
        error.setHeaderText("");
        error.setContentText(msg);
        error.showAndWait();
    }

    /**
     * Simple Confirmation Dialog
     * @param title the title of the dialog
     * @param msg the message to show in dialog
     * @return boolean confirmation input
     */
    public boolean confirmMessage(String title, String msg) {

        // Create alert and set parameters
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Please Confirm " + title);
        confirm.setHeaderText("");
        confirm.setContentText(msg);

        // buttons to include in dialog
        ButtonType continueButton = new ButtonType("Continue");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(continueButton,cancelButton); // creates the buttons in dialog

        Optional<ButtonType> result = confirm.showAndWait(); // show dialog
        return result.get() == continueButton; // user chose CANCEL or closed the dialog
    }

}

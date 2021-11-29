package application.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Appt extends Base {
    // Navigation
    @FXML
    private void apptAddEdit(MouseEvent event) throws IOException {
        switchScene(event,"appt_addedit");
    }
}

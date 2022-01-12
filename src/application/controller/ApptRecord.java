package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.util.DAOimpl.getAllContacts;

public class ApptRecord extends Base{

    @FXML
    Text apptRecordTitle;
    @FXML
    TextField apptId;
    @FXML
    TextField userId;
    @FXML
    ComboBox apptType;
    @FXML
    ComboBox<Customer> customerComboBox;
    @FXML
    ComboBox<Contact> contactComboBox;
    @FXML
    TextField apptTitle;
    @FXML
    TextField apptDesc;
    @FXML
    TextField apptLoc;
    @FXML
    DatePicker apptStart;
    @FXML
    DatePicker apptEnd;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setComboBoxes();
    }

    // set record title depending on button pressed (pass variable from controller)
    /**
     * Cancel Button on returns to the previous scene.
     * @param actionEvent when clicking the "Cancel" button
     * @throws IOException exceptions unload
     */
    public void cancelButton(ActionEvent actionEvent) throws IOException {
        switchScene("apptTable");
    }

    // receive parameters
    public void getParams(String action, Appointment appointment) {
        System.out.println("Transferring parameters to new controller.");

        System.out.println("Updating Title String...");
        switch (action) {
            case "add":
                apptRecordTitle.setText("Add New Appointment");
                break;
            case "edit" :
                apptRecordTitle.setText("Edit Existing Appointment");
                break;
            default:
                break;
        }

        // TODO: Populate Form
        apptId.setText(String.valueOf(appointment.getId()));
        apptTitle.setText(appointment.getTitle());
        apptDesc.setText(appointment.getDescription());
    }

    /*  ======================
        COMBO BOX MANAGEMENT
        ======================*/
    public void setComboBoxes() {
        System.out.println("Setting Contacts Combo Box.");
        contactComboBox.setItems(getAllContacts());
    }

}

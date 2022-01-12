package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.util.DAOimpl.getAllContacts;
import static application.util.DAOimpl.getAllCustomers;

public class ApptRecord extends Base{

    Boolean formTypeNew = true;
    Appointment formAppointment = null;
    ObservableList<Contact> allContacts;

    @FXML
    Text apptRecordTitle;
    @FXML
    TextField apptId;
    @FXML
    TextField userId;
    @FXML
    ComboBox<String> apptType;
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
        populateForm();
    }

    // set record title depending on button pressed (pass variable from controller)
    /**
     * Cancel Button on returns to the previous scene.
     * @param actionEvent when clicking the "Cancel" button
     * @throws IOException exceptions unload
     */
    @FXML
    private void cancelButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    // receive parameters
    public void getParams(String action, Appointment appointment) {
        System.out.println("Transferring parameters to new controller.");

        System.out.println("Updating Title String...");
        switch (action) {
            case "add":
                apptRecordTitle.setText("Add New Appointment");
                formTypeNew = true;
                break;
            case "edit" :
                apptRecordTitle.setText("Edit Existing Appointment");
                formTypeNew = false;
                break;
            default:
                break;
        }
        formAppointment = appointment;

    }

    // TODO: Populate Form
    private void populateForm() {
        System.out.println("Populating Form...");
        // set id

        if (!formTypeNew) {
            apptId.setText(String.valueOf(formAppointment.getId()));
            apptTitle.setText(formAppointment.getTitle());
            apptDesc.setText(formAppointment.getDescription());
            apptLoc.setText(formAppointment.getLocation());

            // TODO: Set combo box values
            Contact apptContact = allContacts.get(formAppointment.getContactId() - 1);
            System.out.println(apptContact);
            contactComboBox.setValue(apptContact);

            Customer apptCustomer = getAllCustomers().get(formAppointment.getCustomerId() - 1);
            customerComboBox.setValue(apptCustomer);

            // TODO: Set dates

        }
    }

    /*  ======================
        TODO: COMBO BOX MANAGEMENT
        ======================*/
    private void setComboBoxes() {
        System.out.println("Starting Combo box Population...");
        setContactComboBox();
        setCustomerComboBox();
        setAppointmentType();

    }


    private void setContactComboBox() {
        System.out.println("Setting Contacts Combo Box...");
        allContacts = getAllContacts();
        contactComboBox.setItems(allContacts);
        contactComboBox.setPromptText("You must select a contact.");


    }

    private void getContact(ActionEvent event) {
        StringBuilder sb = new StringBuilder("");
        Contact contact = contactComboBox.getSelectionModel().getSelectedItem();
        if (contact == null) {
            System.out.println("CB: Null");
        } else {
            System.out.println("CB: " + contact.getName());
        }
    }

    private void setCustomerComboBox() {
        System.out.println("Setting Customer Combo Box...");
        customerComboBox.setItems(getAllCustomers());
        customerComboBox.setPromptText("You must select a customer.");
    }

    private void setAppointmentType() {
    }
}

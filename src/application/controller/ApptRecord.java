package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static application.util.DAOimpl.*;
import static application.util.Loc.*;

public class ApptRecord extends RecordBase<Appointment> {

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
    ComboBox<String> apptTypeComboBox;
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
    DatePicker apptStartDate;
    @FXML
    ChoiceBox apptStartHour, apptStartMinute, apptStartMeridiem;

    @FXML
    DatePicker apptEndDate;
    @FXML
    ChoiceBox apptEndHour, apptEndMinute, apptEndMeridiem;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();


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
        System.out.println("Setting Selected Appointment.");
        formAppointment = appointment;

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

        populateForm();
    }

    // TODO: Populate Form
    private void populateForm() {
        System.out.println("Populating Form...");

        if (!formTypeNew) {
            userId.setText(String.valueOf(formAppointment.getUserId()));
            apptId.setText(String.valueOf(formAppointment.getId()));
            apptTitle.setText(formAppointment.getTitle());
            apptDesc.setText(formAppointment.getDescription());
            apptLoc.setText(formAppointment.getLocation());


            // TODO: Set combo box values
            Contact apptContact = getAllContacts().get(formAppointment.getContactId()-1);
            System.out.println(apptContact);
            contactComboBox.setValue(apptContact);

            Customer apptCustomer = getAllCustomers().get((formAppointment.getCustomerId() - 1));
            customerComboBox.setValue(apptCustomer);

            apptTypeComboBox.setValue(formAppointment.getType());

            // TODO: Set dates in datepicker and time choice boxes
            apptStartDate.setValue(formAppointment.getStart().toLocalDateTime().toLocalDate());
            apptStartHour.setValue(getHour(formAppointment.getStart().toLocalDateTime()));
            apptStartMinute.setValue(getMinute(formAppointment.getStart().toLocalDateTime()));
            apptStartMeridiem.setValue(getMeridiem(formAppointment.getStart().toLocalDateTime()));


            apptEndDate.setValue(formAppointment.getEnd().toLocalDateTime().toLocalDate());
            apptEndHour.setValue(getHour(formAppointment.getEnd().toLocalDateTime()));
            apptEndMinute.setValue(getMinute(formAppointment.getEnd().toLocalDateTime()));
            apptEndMeridiem.setValue(getMeridiem(formAppointment.getEnd().toLocalDateTime()));

        } else {
            // set id (auto-gen if new record)
            apptId.setText(String.valueOf(genId()));
            userId.setText(String.valueOf(getActiveUser().getId()));
        }
    }

    private int genId() {
        // find the highest id of the table\
        int id = 1;
        try {
            prepQuery("SELECT MAX(Appointment_ID) FROM appointments");
            ResultSet rs = getResult();

            while (rs.next()) {
                id = rs.getInt("MAX(Appointment_ID)") + 1;

            }
        } catch (SQLException e) {
                e.printStackTrace();
            }
        System.out.println("Generated ID = " + id);
        return id;
    }

    /*  ======================
        TODO: COMBO BOX MANAGEMENT
        ======================*/
    private void setComboBoxes() {
        System.out.println("Starting Combo box Population...");
        setAppointmentType();
        setContactComboBox();
        setCustomerComboBox();
        setTimeChoices();
    }


    private void setContactComboBox() {
        System.out.println("Setting Contacts Combo Box...");
        contactComboBox.setPromptText("Select a contact.");

        try {
            allContacts = getAllContacts();
            contactComboBox.getItems().addAll(allContacts);
        } catch (NullPointerException ex) {
            System.out.println("No Contacts Found");
        }

    }


    private void setCustomerComboBox() {
        System.out.println("Setting Customer Combo Box...");
        customerComboBox.setPromptText("Select a customer.");

        try {
            customerComboBox.getItems().addAll(getAllCustomers());
        } catch (NullPointerException ex) {
            System.out.println("No Customers Found");
        }
    }

    private void setTimeChoices() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        ObservableList<String> minutes = FXCollections.observableArrayList();
        ObservableList<String> meridiems = FXCollections.observableArrayList();

        for (int i = 1; i < 13; i++) {
            hours.add(String.format("%02d",i));
        }
        for (int i = 0; i < 60; i+=5) {
            minutes.add(String.format("%02d",i));
        }

        meridiems.addAll("AM","PM");

        apptStartHour.setItems(hours);
        apptEndHour.setItems(hours);

        apptStartMinute.setItems(minutes);
        apptEndMinute.setItems(minutes);

        apptStartMeridiem.setItems(meridiems);
        apptEndMeridiem.setItems(meridiems);
    }

    private void setAppointmentType() {
        System.out.println("Setting Appointment Type Choices...");
        apptTypeComboBox.setEditable(true);

        try {
            apptTypeComboBox.getItems().addAll(getAppointmentTypes());

        } catch (NullPointerException ex) {
            System.out.println("No Appointment Types Found");
        }

    }

    @FXML
    private void saveAppointment(ActionEvent actionEvent) {
        // Record Time Values
        Timestamp start = toTimestamp(apptStartDate.getValue(),apptStartHour.getValue().toString(),apptStartMinute.getValue().toString(),apptStartMeridiem.getValue().toString());
        Timestamp end = toTimestamp(apptEndDate.getValue(),apptEndHour.getValue().toString(),apptEndMinute.getValue().toString(),apptEndMeridiem.getValue().toString());;

        Appointment newAppt = new Appointment(Integer.parseInt(apptId.getText()),
                apptTitle.getText(),
                apptDesc.getText(),
                apptLoc.getText(),
                apptTypeComboBox.getValue(),
                start,
                end,
                contactComboBox.getValue().getId(),
                customerComboBox.getValue().getId(),
                Integer.parseInt(userId.getText()));

        ObservableList<Object> params;

        // check form type
        if (formTypeNew) {
            params = toObservableList(newAppt.getId(),
                    newAppt.getTitle(),
                    newAppt.getDescription(),
                    newAppt.getLocation(),
                    newAppt.getType(),
                    newAppt.getStart(),
                    newAppt.getEnd(),
                    getCurrentTime(),
                    getActiveUser().getUserName(),
                    getCurrentTime(),
                    getActiveUser().getUserName(),
                    newAppt.getCustomerId(),
                    newAppt.getUserId(),
                    newAppt.getContactId()
            );
            addRecord(newAppt,params);
            exitButton(actionEvent);
        } else {
            params = toObservableList(newAppt.getTitle(),
                    newAppt.getDescription(),
                    newAppt.getLocation(),
                    newAppt.getType(),
                    newAppt.getStart(),
                    newAppt.getEnd(),
                    getCurrentTime(),
                    getActiveUser().getUserName(),
                    newAppt.getCustomerId(),
                    newAppt.getUserId(),
                    newAppt.getContactId(),
                    newAppt.getId());
            updateRecord(newAppt, params);
            exitButton(actionEvent);
        }
    }


    public String getInsertStatement() {
        return "INSERT INTO appointments " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    public String getUpdateStatement() {
        return "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
    }

// end of class
}

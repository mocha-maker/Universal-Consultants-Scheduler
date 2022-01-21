package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import static application.controller.ApptTable.allContacts;
import static application.controller.ApptTable.getAllContacts;
import static application.controller.CustTable.allCustomers;
import static application.controller.CustTable.getAllCustomers;
import static application.util.Alerts.infoMessage;
import static application.util.Loc.*;

@SuppressWarnings("rawtypes")
public class ApptRecord extends RecordBase<Appointment> {

    Boolean formTypeNew = true;
    Appointment formAppointment = null;

    private static List<Object> params;


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
    ChoiceBox<String> apptStartHour;
    @FXML
    ChoiceBox<String> apptStartMinute;
    @FXML
    ChoiceBox<String> apptStartMeridiem;

    @FXML
    DatePicker apptEndDate;
    @FXML
    ChoiceBox<String> apptEndHour;
    @FXML
    ChoiceBox<String> apptEndMinute;
    @FXML
    ChoiceBox<String> apptEndMeridiem;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();

    }

    // set record title depending on button pressed (pass variable from controller)


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
            Contact apptContact = allContacts.get(formAppointment.getContact().getId()-1);
            System.out.println(apptContact);
            contactComboBox.setValue(apptContact);

            Customer apptCustomer = allCustomers.get((formAppointment.getCustomerId() - 1));
            customerComboBox.setValue(apptCustomer);

            apptTypeComboBox.setValue(formAppointment.getType());

            // TODO: Set dates in datepicker and time choice boxes
            LocalDateTime startLocal = convertToLocal(formAppointment.getStart());
            apptStartDate.setValue(startLocal.toLocalDate());
            apptStartHour.setValue(getHour(startLocal));
            apptStartMinute.setValue(getMinute(startLocal));
            apptStartMeridiem.setValue(getMeridiem(startLocal));

            LocalDateTime endLocal = convertToLocal(formAppointment.getEnd());
            System.out.println(formAppointment.getEnd());
            System.out.println(endLocal);
            apptEndDate.setValue(endLocal.toLocalDate());
            apptEndHour.setValue(getHour(endLocal));
            apptEndMinute.setValue(getMinute(endLocal));
            apptEndMeridiem.setValue(getMeridiem(endLocal));

        } else {
            apptId.setText(genId("appointment"));
            userId.setText(String.valueOf(getActiveUser().getId()));
        }
    }

    /*  ======================
        TODO: COMBO BOX MANAGEMENT
        ======================*/
    private void setComboBoxes() {
        System.out.println("Starting Combo box Population...");
        if (allCustomers == null) { getAllCustomers(); }
        if (allContacts == null) { getAllContacts(); }

        setAppointmentType();
        setContactComboBox();
        setCustomerComboBox();
        setTimeChoices();
    }


    private void setContactComboBox() {
        System.out.println("Setting Contacts Combo Box...");
        contactComboBox.setPromptText("Select a contact.");

        try {
            contactComboBox.getItems().addAll(allContacts);
        } catch (NullPointerException ex) {
            System.out.println("No Contacts Found");
        }

    }


    private void setCustomerComboBox() {
        System.out.println("Setting Customer Combo Box...");
        customerComboBox.setPromptText("Select a customer.");

        try {
            customerComboBox.getItems().addAll(allCustomers);
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

    public String getInsertStatement() {
        return "INSERT INTO appointments " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    public String getUpdateStatement() {
        return "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
    }

    @FXML
    private void saveAppointment(ActionEvent actionEvent) {
        // Record Time Values
        Timestamp start = getTimestamp(apptStartDate.getValue(),apptStartHour.getValue().toString(),apptStartMinute.getValue().toString(),apptStartMeridiem.getValue().toString());
        Timestamp end = getTimestamp(apptEndDate.getValue(),apptEndHour.getValue().toString(),apptEndMinute.getValue().toString(),apptEndMeridiem.getValue().toString());;

        // Create appointment object
        Appointment newAppt = new Appointment(Integer.parseInt(apptId.getText()),
                apptTitle.getText(),
                apptDesc.getText(),
                apptLoc.getText(),
                apptTypeComboBox.getValue(),
                start,
                end,
                contactComboBox.getValue(),
                customerComboBox.getValue().getId(),
                Integer.parseInt(userId.getText()));

        // Create parameters list

        // check form type
        if (formTypeNew) {
            //
            params = toList(newAppt.getId(),
                    newAppt.getTitle(),
                    newAppt.getDescription(),
                    newAppt.getLocation(),
                    newAppt.getType(),
                    newAppt.getStart(),
                    newAppt.getEnd(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    newAppt.getCustomerId(),
                    newAppt.getUserId(),
                    newAppt.getContact().getId()
            );
            addRecord(newAppt,params);
            exitButton(actionEvent);
        } else {
            params = toList(newAppt.getTitle(),
                    newAppt.getDescription(),
                    newAppt.getLocation(),
                    newAppt.getType(),
                    newAppt.getStart(),
                    newAppt.getEnd(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    newAppt.getCustomerId(),
                    newAppt.getUserId(),
                    newAppt.getContact().getId(),
                    newAppt.getId());
            boolean updated = updateRecord(newAppt, params);
            exitButton(actionEvent);
            if (updated) { infoMessage("Appointment ID: " + newAppt.getId() + "\nSuccessfully Updated");}
        }
    }

    // GETTERS

    public static ObservableList<String> getAppointmentTypes() {
        ObservableList<String> allTypes = FXCollections.observableArrayList();
        try {
            System.out.println("Querying Appointments Database for Unique Types.");
            prepQuery("SELECT DISTINCT Type FROM appointments");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Appointment Type.");
                String typeResult = rs.getString("Type");
                // add Type String to Observable List
                allTypes.add(typeResult);
                i++;
                System.out.println(typeResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allTypes;
    }


    // Date Time Validation

    //

// end of class
}

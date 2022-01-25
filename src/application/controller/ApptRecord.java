package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.List;
import java.util.ResourceBundle;

import static application.controller.ApptTable.allContacts;
import static application.controller.ApptTable.getAllContacts;
import static application.controller.CustTable.allCustomers;
import static application.controller.CustTable.getAllCustomers;
import static application.util.Alerts.errorMessage;
import static application.util.Alerts.infoMessage;
import static application.util.Loc.*;

@SuppressWarnings("rawtypes")
public class ApptRecord extends RecordBase<Appointment> {

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


    // Validation variables
    Boolean typeValid;
    Boolean customerValid;
    Boolean titleValid;
    Boolean descValid;
    Boolean locValid;
    Boolean startDateTimeValid;
    Boolean endDateTimeValid;

    @Override
    protected void addListeners() {
        apptTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (newVal != oldVal) {
                    typeValid = validateField(apptTypeComboBox, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
                }
            }
        });
    }



    /**
     * Transfer parameters from other controllers to this one
     * @param action
     * @param appointment
     */
    @Override
    protected void getParams(String action, Appointment appointment) {
        System.out.println("Transferring parameters to new controller.");
        System.out.println("Setting Selected Customer.");
        record = appointment;
        LocalDateTime startLocal;
        LocalDateTime endLocal;

        System.out.println(action);

        switch (action) {
            case "New":
                System.out.println("Updating Title String...");
                apptRecordTitle.setText("Add New Appointment");
                formTypeNew = true;
                System.out.println(formTypeNew);

                apptId.setText(genId("appointment"));
                userId.setText(String.valueOf(getActiveUser().getId()));

                // TODO: Set Default times
                startLocal = LocalDateTime.now();
                apptStartDate.setValue(startLocal.toLocalDate());
                apptStartHour.setValue(getHour(startLocal));
                apptStartMinute.setValue(getMinute(startLocal));
                apptStartMeridiem.setValue(getMeridiem(startLocal));

                endLocal = LocalDateTime.now().plusMinutes(30);
                apptEndDate.setValue(endLocal.toLocalDate());
                apptEndHour.setValue(getHour(endLocal));
                apptEndMinute.setValue(getMinute(endLocal));
                apptEndMeridiem.setValue(getMeridiem(endLocal));

                break;
            case "Edit" :
                System.out.println("Updating Title String...");
                apptRecordTitle.setText("Edit Existing Appointment");
                formTypeNew = false;
                System.out.println(formTypeNew);

                userId.setText(String.valueOf(record.getUserId()));
                apptId.setText(String.valueOf(record.getId()));
                apptTitle.setText(record.getTitle());
                apptDesc.setText(record.getDescription());
                apptLoc.setText(record.getLocation());

                Contact apptContact = allContacts.get(record.getContact().getId()-1);
                contactComboBox.setValue(apptContact);

                Customer apptCustomer = record.getCustomer();
                customerComboBox.setValue(apptCustomer);

                apptTypeComboBox.setValue(record.getType());

                startLocal = record.getStart();
                apptStartDate.setValue(startLocal.toLocalDate());
                apptStartHour.setValue(getHour(startLocal));
                apptStartMinute.setValue(getMinute(startLocal));
                apptStartMeridiem.setValue(getMeridiem(startLocal));

                endLocal = record.getEnd();
                apptEndDate.setValue(endLocal.toLocalDate());
                apptEndHour.setValue(getHour(endLocal));
                apptEndMinute.setValue(getMinute(endLocal));
                apptEndMeridiem.setValue(getMeridiem(endLocal));

                break;
            default:
                break;
        }

    }


    /*  ======================
        TODO: COMBO BOX MANAGEMENT
        ======================*/
    @Override
    protected void setComboBoxes() {
        System.out.println("Starting Combo box Population...");
        getAllCustomers();
        getAllContacts();
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
        customerComboBox.getItems().clear();
        System.out.println("Setting Customer Combo Box...");
        customerComboBox.setPromptText("Select a customer.");
        getAllCustomers();
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

        LocalDateTime start = getStartDateTime();
        LocalDateTime end = getEndDateTime();

        if (validateDateTimes(start, end)) {
            System.out.println("DateTimes are valid.");

            // Create appointment object
            Appointment newAppt = new Appointment(Integer.parseInt(apptId.getText()),
                    apptTitle.getText(),
                    apptDesc.getText(),
                    apptLoc.getText(),
                    apptTypeComboBox.getValue(),
                    start,
                    end,
                    contactComboBox.getValue(),
                    customerComboBox.getValue(),
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
                        toTimestamp(convertTo(newAppt.getStart(),"UTC")),
                        toTimestamp(convertTo(newAppt.getEnd(),"UTC")),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        newAppt.getCustomer().getId(),
                        newAppt.getUserId(),
                        newAppt.getContact().getId()
                );
                addRecord(newAppt,params);
                exitButton(actionEvent);
            } else {
                params = toList(newAppt.getId(),
                        newAppt.getTitle(),
                        newAppt.getDescription(),
                        newAppt.getLocation(),
                        newAppt.getType(),
                        toTimestamp(convertTo(newAppt.getStart(),"UTC")),
                        toTimestamp(convertTo(newAppt.getEnd(),"UTC")),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        newAppt.getCustomer().getId(),
                        newAppt.getUserId(),
                        newAppt.getContact().getId()
                );
                boolean updated = updateRecord(newAppt, params);
                exitButton(actionEvent);
                if (updated) { infoMessage("Appointment ID: " + newAppt.getId() + "\nSuccessfully Updated");}
            }
        } else {
            System.out.println("DateTimes are not valid.");
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

    // EVENT HANDLERS
    @FXML
    private void addNewCust(ActionEvent actionEvent) throws IOException {
        String action = "add";
        Customer customer = null;
        FXMLLoader loader = setLoader("CustRecord");
        Parent root = loader.load();
        CustRecord controller = loader.getController();
        controller.getParams(action, customer);
        popupScene(root, "Customer Record");
        setCustomerComboBox();
    }

    // Date Time Validation

    private LocalDateTime getStartDateTime() {
        return formatTimeSelection(apptStartDate.getValue(),
                Integer.parseInt(apptStartHour.getValue()),
                Integer.parseInt(apptStartMinute.getValue()),
                apptStartMeridiem.getValue());
    }

    private LocalDateTime getEndDateTime() {
        return formatTimeSelection(apptEndDate.getValue(),
                Integer.parseInt(apptEndHour.getValue()),
                Integer.parseInt(apptEndMinute.getValue()),
                apptEndMeridiem.getValue());
    }

    public Boolean validateDateTimes(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(end)) {
            if( isInBusinessHours(start, end) ) {
                if (isWithinBusinessDay(start, end)) {
                    return true;
                } else {
                    errorMessage("Date Validation", "Appointment Duration is longer than a business day of 14 hours.");
                }
                errorMessage("Date Validation", "Appointment Times are outside of Business Hours.");
            }
            return false;
        } else {
            errorMessage("Date Validation", "Start time is after End time.");
            return false;
        }
    }

    private Boolean isWithinBusinessDay(LocalDateTime start, LocalDateTime end) {
        Duration between = Duration.between(start,end);
        Duration maxhours = Duration.ofHours(14);
        if ( between.compareTo(maxhours) > 0) {
            return false;
        }
        return true;
    }

    private Boolean isInBusinessHours(LocalDateTime start, LocalDateTime end) {
        System.out.println("Business Starting Hours: " + getBusinessStart());
        System.out.println("Business Closing Hours: " + getBusinessEnd());
        if (start.isAfter(getBusinessEnd()) || start.isBefore(getBusinessStart())) {
            errorMessage("Date Validation", "Start date is out of bounds.");
        } else if (end.isBefore(getBusinessStart()) || end.isAfter(getBusinessEnd())) {
            errorMessage("Date Validation", "End datetime is out of bounds.");
        }
        return true;
    }

    private LocalDateTime getBusinessStart() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(8,0,0,0)).atZone(ZoneId.of("America/New_York")).toLocalDateTime();
    }

    private LocalDateTime getBusinessEnd() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0,0)).atZone(ZoneId.of("America/New_York")).toLocalDateTime();
    }
    //

// end of class
}

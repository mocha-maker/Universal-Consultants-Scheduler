package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import application.model.User;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static application.controller.ApptTable.allContacts;
import static application.controller.ApptTable.getAllContacts;
import static application.controller.CustTable.allCustomers;
import static application.controller.CustTable.getAllCustomers;
import static application.util.Loc.*;

/**
 * Appointment Record Controller
 * Manages the Appointment Record Form for Adding and Editing Appointments
 */
public class ApptRecord extends RecordBase<Appointment> {

    /**
     * Appointment object holder
     */
    Appointment newAppt;

    private String printOverlaps;

    @FXML
    Text apptRecordTitle;
    @FXML
    TextField apptId;
    @FXML
    ComboBox<User> userComboBox;
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

    Boolean isAllValid;
    Boolean titleValid;
    Boolean descValid;
    Boolean locValid;

    /**
     * Change Listeners to validate fields
     */
    @Override
    protected void addListeners() {

        apptTitle.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                titleValid = validateField(apptTitle, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
            }
        });

        apptDesc.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                descValid = validateField(apptDesc, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
            }
        });

        apptLoc.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                locValid = validateField(apptLoc, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
            }
        });
    }

    private Boolean getIsAllValid() {

        return titleValid && descValid && locValid && !customerComboBox.getSelectionModel().isEmpty();
    }


    /**
     * Transfer parameters from other controllers to this one
     * @param action what the user is intending to do
     * @param appointment the selected appointment from the table
     */
    @Override
    protected void getParams(String action, Appointment appointment) {
        System.out.println("Transferring parameters to new controller.");
        System.out.println("Setting Selected Customer.");
        record = appointment;
        LocalDateTime startLocal;
        LocalDateTime endLocal;

        System.out.println(action);
        bindSaveButton();

        switch (action) {
            case "New":
                System.out.println("Updating Title String...");
                apptRecordTitle.setText("Add New Appointment");
                formTypeNew = true;

                apptId.setText(genId("appointment"));
                userComboBox.setValue(getActiveUser());

                startLocal = LocalDateTime.now();
                apptStartDate.setValue(startLocal.toLocalDate());
                apptStartHour.setValue(getHour(startLocal));
                apptStartMinute.setValue(getMinute(startLocal.plusMinutes((65-startLocal.getMinute())%5)));
                apptStartMeridiem.setValue(getMeridiem(startLocal));

                endLocal = LocalDateTime.now().plusMinutes(30);
                apptEndDate.setValue(endLocal.toLocalDate());
                apptEndHour.setValue(getHour(endLocal));
                apptEndMinute.setValue(getMinute(endLocal.plusMinutes((65-endLocal.getMinute())%5)));
                apptEndMeridiem.setValue(getMeridiem(endLocal));

                break;
            case "Edit" :
                System.out.println("Updating Title String...");
                apptRecordTitle.setText("Edit Existing Appointment");
                formTypeNew = false;

                userComboBox.setValue(getAllUsers().get(record.getUserId()-1));
                Contact apptContact = allContacts.get(record.getContact().getId()-1);
                contactComboBox.setValue(apptContact);

                Customer apptCustomer = record.getCustomer();
                customerComboBox.setValue(apptCustomer);

                apptTypeComboBox.setValue(record.getType());
                apptId.setText(String.valueOf(record.getId()));
                apptTitle.setText(record.getTitle());
                apptDesc.setText(record.getDescription());
                apptLoc.setText(record.getLocation());

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

    /**
     * Binds saveButton to the other string fields to ensure all fields are completed
     */
    private void bindSaveButton() {
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
                () ->   // how to calculate value
                        apptTitle.getText().isEmpty() ||
                        apptDesc.getText().isEmpty() ||
                        apptLoc.getText().isEmpty() ||
                        apptTypeComboBox.getSelectionModel().isEmpty() ||
                        contactComboBox.getSelectionModel().isEmpty(),

                        // elements to look at
                        apptTitle.textProperty(),
                        apptDesc.textProperty(),
                        apptLoc.textProperty(),
                        apptTypeComboBox.selectionModelProperty(),
                        contactComboBox.selectionModelProperty()
        ));
    }


    /*  ======================
        COMBO BOX MANAGEMENT
        ======================*/

    /**
     * Set Combo Boxes values and defaults
     */
    @Override
    protected void setComboBoxes() {
        System.out.println("Starting Combo box Population...");
        getAllCustomers();
        getAllContacts();

        setUserComboBox();
        setAppointmentType();
        setContactComboBox();
        setCustomerComboBox();
        setTimeChoices();
    }


    /**
     * Sets contact combo box values and prompt text
     */
    private void setContactComboBox() {
        System.out.println("Setting Contacts Combo Box...");
        contactComboBox.setPromptText("Select a contact.");

        try {
            contactComboBox.getItems().addAll(allContacts);
        } catch (NullPointerException ex) {
            System.out.println("No Contacts Found");
        }

    }

    /**
     * Sets customer combo box values and prompt text
     */
    private void setCustomerComboBox() {
        System.out.println("Setting Customer Combo Box...");
        customerComboBox.setPromptText("Select a customer.");
        if (customerComboBox.getItems().size() > 0) {
            customerComboBox.getItems().clear();
            getAllCustomers();
        }

        try {
            customerComboBox.getItems().addAll(allCustomers);
        } catch (NullPointerException ex) {
            System.out.println("No Customers Found");
        }
    }

    /**
     * Sets time choices
     */
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

    /**
     * set user combo box
     */
    private void setUserComboBox() {
        System.out.println("Setting User Choices...");
        try {
            userComboBox.getItems().addAll(getAllUsers());

        } catch (NullPointerException ex) {
            System.out.println("No Appointment Types Found");
        }
    }

    /**
     * set appointment types
     */
    private void setAppointmentType() {
        System.out.println("Setting Appointment Type Choices...");
        try {
            apptTypeComboBox.getItems().addAll(getAppointmentTypes());

        } catch (NullPointerException ex) {
            System.out.println("No Appointment Types Found");
        }
    }

    /**
     *
     * @return appointment SQL insert statement
     */
    public String getInsertStatement() {
        return "INSERT INTO appointments " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    /**
     *
     * @return appointment SQL update statement
     */
    public String getUpdateStatement() {
        return "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
    }

    /**
     * Inititate save appointment process
     * @param actionEvent the button press event
     */
    @FXML
    private void saveAppointment(ActionEvent actionEvent) {
        // Record Time Values

        LocalDateTime start = getStartDateTime();
        LocalDateTime end = getEndDateTime();

        if (getIsAllValid()) {

            // Create appointment object
            newAppt = new Appointment(Integer.parseInt(apptId.getText()),
                    apptTitle.getText(),
                    apptDesc.getText(),
                    apptLoc.getText(),
                    apptTypeComboBox.getValue(),
                    start,
                    end,
                    contactComboBox.getValue(),
                    customerComboBox.getValue(),
                    userComboBox.getValue().getId());

            if (validateDateTimes(start, end)) {
                System.out.println("DateTimes are valid.");


                // Create parameters list

                // check form type
                List<Object> params;
                if (formTypeNew) {

                    params = toList(newAppt.getId(),
                            newAppt.getTitle(),
                            newAppt.getDescription(),
                            newAppt.getLocation(),
                            newAppt.getType(),
                            toTimestamp(newAppt.getStart()),
                            toTimestamp(newAppt.getEnd()),
                            getCurrentTimestamp(),
                            getActiveUser().getUserName(),
                            getCurrentTimestamp(),
                            getActiveUser().getUserName(),
                            newAppt.getCustomer().getId(),
                            newAppt.getUserId(),
                            newAppt.getContact().getId()
                    );
                    boolean added = addRecord(newAppt, params);
                    if (added) {
                        infoMessage("Appointment ID: " + newAppt.getId() + "\nSuccessfully Added");
                    }
                    exitButton(actionEvent);
                } else {
                    params = toList(
                            newAppt.getTitle(),
                            newAppt.getDescription(),
                            newAppt.getLocation(),
                            newAppt.getType(),
                            toTimestamp(newAppt.getStart()),
                            toTimestamp(newAppt.getEnd()),
                            getCurrentTimestamp(),
                            getActiveUser().getUserName(),
                            newAppt.getCustomer().getId(),
                            newAppt.getUserId(),
                            newAppt.getContact().getId(),
                            newAppt.getId() // Needed for WHERE param
                    );
                    boolean updated = updateRecord(newAppt, params);
                    exitButton(actionEvent);
                    if (updated) {
                        infoMessage("Appointment ID: " + newAppt.getId() + "\nSuccessfully Updated");
                    }
                }
            } else {
                System.out.println("DateTimes are not valid.");
            }
        } else {
            errorMessage("Invalid Data Entry","Missing or invalid information. Please check that all fields are entered using valid data.");
        }
    }

    // GETTERS

    /**
     * Get list of users
     * @return list of usernames and ids
     */
    private ObservableList<User> getAllUsers() {
        ObservableList<User> allUsers = FXCollections.observableArrayList();

        try {
            prepQuery("SELECT User_ID, User_Name FROM users ORDER BY User_ID ASC");
            ResultSet rs = getResult();
            while (rs.next()) {
                int userId = rs.getInt("User_ID");
                String userName = rs.getString("User_Name");
                allUsers.add(new User(userId,userName));
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return allUsers;
    }

    /**
     * Get list of appointment types
     * @return list of appointment types
     */
    public static ObservableList<String> getAppointmentTypes() {
        ObservableList<String> allTypes = FXCollections.observableArrayList();

        allTypes.add("Planning Session");
        allTypes.add("De-Briefing");

        System.out.println("Retrieving Observable List.");
        return allTypes;
    }

    // EVENT HANDLERS

    /**
     * Initiate add a new customer method
     * @throws IOException in case the popup is unable to open
     */
    @FXML
    private void addNewCust() throws IOException {
        FXMLLoader loader = setLoader("CustRecord");
        Parent root = loader.load();
        CustRecord controller = loader.getController();
        controller.getParams("New", null);
        popupScene(root, "Customer Record");
        setCustomerComboBox();
    }

    // Date Time Validation

    /**
     *
     * @return appointment start LocalDateTime input
     */
    private LocalDateTime getStartDateTime() {
        return formatTimeSelection(apptStartDate.getValue(),
                Integer.parseInt(apptStartHour.getValue()),
                Integer.parseInt(apptStartMinute.getValue()),
                apptStartMeridiem.getValue());
    }

    /**
     *
     * @return appointment end LocalDateTime input
     */
    private LocalDateTime getEndDateTime() {
        return formatTimeSelection(apptEndDate.getValue(),
                Integer.parseInt(apptEndHour.getValue()),
                Integer.parseInt(apptEndMinute.getValue()),
                apptEndMeridiem.getValue());
    }

    /**
     * Converts input for appointment date-times to LocalDateTime
     * @param date - the date selected
     * @param hour - the hour selected
     * @param minute - the minute selected
     * @param meridiem - the time of day (meridiem) selected
     * @return LocalDateTime converted
     */
    public static LocalDateTime formatTimeSelection(LocalDate date, int hour, int minute, String meridiem) {
        // Convert hour to 24-clock
        if (meridiem.equals("PM") && hour < 12) {
            hour = hour + 12 ;
        }

        LocalDateTime dateTime = date.atTime(hour,minute,0,0);
        System.out.println(dateTime);
        return dateTime;
    }

    /**
     * Initiates Date Validations
     * 1. If start date is after the current datetime
     * 2. If start and end date times are in order
     * 3. If the appointment is less than 14 hours TODO: delete if redundant
     * 4. If start and end date times are within the business day
     * 5. If appointment overlaps with other appointments (not including itself)
     *
     * @param start - the entered start datetime
     * @param end - the entered end datetime
     * @return date validation status
     */
    public Boolean validateDateTimes(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(LocalDateTime.now())) {
            if (start.isBefore(end)) {
                if (isWithinBusinessDay(start, end)) {
                    if (isInBusinessHours(start, end)) {
                        if (isNotOverlapping(start, end)) {
                            return true;
                        } else {
                            errorMessage("Date Validation", "Appointment overlap exists with this customer for the following appointment(s):" + printOverlaps);
                        }
                    } else {
                        errorMessage("Date Validation", "Appointment times are outside of Business Hours. \nTimes must be within 8AM - 10PM Eastern");
                    }
                } else {
                    errorMessage("Date Validation", "Appointment is longer than a business day of 14 hours.");
                }
            } else {
                errorMessage("Date Validation", "Appointments need to start before it ends.");
            }
        } else {
            if (formTypeNew) {
                errorMessage("Date Validation", "New appointments can only be made for a future date and time.");
            } else {
                errorMessage("Date Validation", "Appointments can only be updated to a future date and time.");
            }
        }

        return false;
    }

    /**
     *
     * @return LocalDateTime business start based on EST
     */
    private LocalDateTime getBusinessStart() {
        return convertTo(LocalDateTime.of(getStartDateTime().toLocalDate(), LocalTime.of(8,0,0,0)),"America/New_York");
    }

    /**
     *
     * @return LocalDateTime business end based on EST
     */
    private LocalDateTime getBusinessEnd() {
        return convertTo(LocalDateTime.of(getStartDateTime().toLocalDate(), LocalTime.of(22,0,0,0)),"America/New_York");
    }

    /**
     * Date validation for whether the appointment date times are less than 14 hours
     * Converts
     * @param start - the start date time entered
     * @param end - the end date time entered
     * @return appointment length validation status
     */
    private Boolean isWithinBusinessDay(LocalDateTime start, LocalDateTime end) {
        Duration between = Duration.between(start,end);
        Duration maxhours = Duration.ofHours(14);
        return between.compareTo(maxhours) <= 0;
    }

    /**
     * Date Validation for whether the appointment date times occur within business hours
     * Converts entered date times to eastern before comparing with business hours
     * @param start - the start date time entered
     * @param end - the end date time entered
     * @return business hours validation status
     */
    private Boolean isInBusinessHours(LocalDateTime start, LocalDateTime end) {
        // Get business day starts and times based on the appointment start datetime
        LocalDateTime businessStart = getBusinessStart();
        LocalDateTime businessEnd = getBusinessEnd();

        System.out.println("Business Starting Hours: " + getBusinessStart());
        System.out.println("Business Closing Hours: " + getBusinessEnd());

        // Convert entered dates to Eastern Time
        LocalDateTime startEST = convertTo(start,"America/New_York");
        LocalDateTime endEST = convertTo(end,"America/New_York");

        System.out.println("Appointment Start in EST: " + startEST);
        System.out.println("Appointment End in EST: " + endEST);

        // make sure start is not before or after business hours on the given day
        if (startEST.isAfter(businessEnd) || startEST.isBefore(businessStart)) {
            System.out.println("Start datetime is out of bounds.");
            return false;
        } else if (endEST.isAfter(businessEnd)) { // check if end is after hours
            System.out.println("End datetime is out of bounds.");
            return false;
        } else {
            return true;
        }
    }


    /**
     * Date Validation for whether the appointment date times overlap with other appointments of the same customer,
     * not including the appointment being created or edited
     * @param start - the start date time entered
     * @param end - the end date time entered
     * @return overlapping appointments status
     */
    private Boolean isNotOverlapping(LocalDateTime start, LocalDateTime end) {
        int custId = newAppt.getCustomer().getId();
        int apptId = newAppt.getId();
        printOverlaps = "";

        try {
            prepQuery("SELECT * FROM appointments WHERE Customer_ID = " + custId + " AND Appointment_ID != " + apptId);
            ResultSet rs = getResult();
            while (rs.next()) {
                int oldApptId = rs.getInt("Appointment_ID");
                LocalDateTime oldStart = timeStampToLocal(rs.getTimestamp("Start"));
                LocalDateTime oldEnd = timeStampToLocal(rs.getTimestamp("End"));

                System.out.println("Checking Appointment #" + oldApptId);
                if ( (end.isBefore (oldStart)  && start.isBefore (oldStart)) || (end.isAfter (oldEnd) && start.isAfter (oldEnd))) {
                    System.out.println("No overlap found.");
                } else {
                    printOverlaps = printOverlaps + "\nAppointment #" + oldApptId + " - From " + oldStart.toLocalTime() + " to " + oldEnd.toLocalTime();
                    return false;
                }
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        return true;
    }


// end of class
}

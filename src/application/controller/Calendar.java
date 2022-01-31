package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static application.controller.CustTable.getAllCustomers;
import static application.util.Loc.*;

/**
 * Calendar Controller
 * Manages the Calendar View.
 */
public final class Calendar extends TableBase<Appointment> implements Initializable {

    private final LocalDate today = LocalDate.now();
    private LocalDate picked;

    /*  ======================
        FXML ELEMENTS
        ======================*/
    @FXML
    private Text currentView;

    /* TOGGLE ELEMENTS */
    @FXML
    private RadioButton monthRadio;
    @FXML
    private RadioButton weekRadio;
    @FXML
    private final ToggleGroup filterSelect = new ToggleGroup();

    /* DATE NAVIGATION */

    @FXML
    private Button  prevBtn, nextBtn;
    @FXML
    private DatePicker periodPicker;

    /*  ======================
        VIEW STRUCTURE
        ======================*/

    /**
     * Add Columns
     * Adds columns using Generic Table Column Adder and setCellValueFactory
     * lambdas: used to format and select specific information to populate the tableview
     */
    protected void addColumns() {

        // Create and set specially formatted columns
        final TableColumn<Appointment, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getContact().getName()));

        final TableColumn<Appointment, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(param -> new SimpleStringProperty(dateToString(param.getValue().getStart(),"yyyy-MM-dd hh:mm a")));

        final TableColumn<Appointment, String> endCol =new TableColumn<>("End");
        endCol.setCellValueFactory(param -> new SimpleStringProperty(dateToString(param.getValue().getEnd(),"yyyy-MM-dd hh:mm a")));

        final TableColumn<Appointment, String> custIdCol =new TableColumn<>("CustomerId");
        custIdCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getCustomer().getId())));

        final TableColumn<Appointment, Integer> userIdCol =new TableColumn<>("UserId");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Create and set string columns
        tableView.getColumns().addAll(getStringColumn(Appointment.class, "title"),
                getStringColumn(Appointment.class, "description"),
                getStringColumn(Appointment.class, "location"),
                contactCol,
                getStringColumn(Appointment.class,"type"),
                startCol,
                endCol,
                custIdCol,
                userIdCol);

        setListener();
        setToolTips();
        setDefaults();

    }

    /**
     * Sets tooltips to assist in how to use calendar
     */
    private void setToolTips() {

        prevBtn.setTooltip(new Tooltip("Go to previous period."));
        nextBtn.setTooltip(new Tooltip("Go to next period."));

        periodPicker.setTooltip(new Tooltip("Select a period."));
    }

    /**
     * Creates a listener for the period picker to handle when a new date is selected
     * lambda: adds functionality to the date picker's valueProperty listener to allow dynamic triaging of handlers in a simple way
     */
    private void setListener() {

        periodPicker.valueProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                picked = periodPicker.getValue();
                System.out.println("Updating dates to match period...");
                if (monthRadio.isSelected()) {
                    monthPickerHandler();
                } else if (weekRadio.isSelected()) {
                    weekPickerHandler();
                }
                updateTable();
            }
        }));
    }

    /**
     * Initiates the setting default values of elements process
     */
    private void setDefaults() {
        monthRadio.setToggleGroup(filterSelect);
        weekRadio.setToggleGroup(filterSelect);
        periodPicker.setValue(getFirstOfMonth(today));
        setPicked();
    }

    /**
     * Updates the table with filtered appointments based on selected dates and filters
     */
    public void updateTable() {
        tableView.getItems().clear();
        tableView.setItems(getFilteredAppointments());
        currentView.setText(getViewTitle());
    }

    /**
     * Get Calendar View Title
     * @return the view title for the current filter
     */
    private String getViewTitle() {
        String viewTitle = "";
        Toggle selected = filterSelect.getSelectedToggle();

        if (selected.equals(monthRadio)) {
            viewTitle = "Appointments in the month of " + toCapitalized(periodPicker.getValue().getMonth().toString().toLowerCase());
        } else if (selected.equals(weekRadio)) {
            viewTitle = "Appointings within the week of " + periodPicker.getValue().toString();
        }

        return viewTitle;
    }

    /**
     * Get first of the month
     * @param localDate selected date in picker
     * @return first of the month
     */
    private static LocalDate getFirstOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(1);
    }
    /**
     * Get beginning of the week
     * @param localDate selected date in picker
     * @return beginning of the week
     */
    private static LocalDate getBeginOfWeek(LocalDate localDate) {
        DayOfWeek firstDayofWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return localDate.with(TemporalAdjusters.previousOrSame(firstDayofWeek));
    }
    /**
     * Get first of the next month
     * @param localDate selected date in picker
     * @return first of the next month
     */
    private static LocalDate getNextMonth(LocalDate localDate) {
        return getFirstOfMonth(localDate.plusMonths(1));
    }
    /**
     * Get beginning of next week
     * @param localDate selected date in picker
     * @return beginning of next week
     */
    private static LocalDate getNextWeek(LocalDate localDate) {
        return getBeginOfWeek(localDate.plusWeeks(1));
    }
    /**
     * Get first of the previous month
     * @param localDate selected date in picker
     * @return first of the previous month
     */
    private static LocalDate getPrevMonth(LocalDate localDate) {
        return getFirstOfMonth(localDate.minusMonths(1));
    }
    /**
     * Get first of the previous week
     * @param localDate selected date in picker
     * @return first of the previous week
     */
    private static LocalDate getPrevWeek(LocalDate localDate) {
        return getBeginOfWeek(localDate.minusWeeks(1));
    }

    /**
     * Queries the database for appointments based on the selected filter and dates
     * @return list of filtered appointments
     */
    private ObservableList<Appointment> getFilteredAppointments() {
        allAppointments = FXCollections.observableArrayList();
        String startDate = getStartPeriod();
        String endDate = getEndPeriod();

        try {
            System.out.println("Querying Appointment Database.");
            prepQuery("SELECT * FROM appointments JOIN contacts USING (Contact_ID) JOIN customers USING (Customer_ID) JOIN users USING (User_ID) " +
                    "WHERE Start >= " + startDate + " AND " +
                    "End < " + endDate + " ORDER BY Start ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Appointment Variables.");
                int apptID = rs.getInt("Appointment_ID");
                String apptTitle = rs.getString("Title");
                String apptDesc = rs.getString("Description");
                String apptLoc = rs.getString("Location");
                String apptType = rs.getString("Type");

                LocalDateTime apptStart = timeStampToLocal(rs.getTimestamp("Start"));
                LocalDateTime apptEnd = timeStampToLocal(rs.getTimestamp("End"));

                Contact apptContact = getAllContacts().get(rs.getInt("Contact_ID")-1);
                Customer apptCust = getAllCustomers().get(rs.getInt("Customer_ID")-1);
                int apptUserID = rs.getInt("User_ID");

                // construct Appointment object using result
                System.out.println("Constructing Appointment Object.");
                Appointment apptResult = new Appointment(apptID,
                        apptTitle,
                        apptDesc,
                        apptLoc,
                        apptType,
                        apptStart,
                        apptEnd,
                        apptContact,
                        apptCust,
                        apptUserID);

                // add Appointment object to Observable List
                allAppointments.add(apptResult);
                i++;
                System.out.println("Appointment added to Observable List. (" + i + ")");

            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allAppointments;
    }

    /**
     * Sets the current period selected
     */
    private void setPicked() {
        picked = periodPicker.getValue();
    }

    /**
     * Get delete SQL statement
     * @return SQL statement to delete appointment record
     */
    protected String getDeleteStatement() {
        return "DELETE FROM appointments WHERE Appointment_ID = ?";
    }

    /**
     * blank delete dependencies method
     * @return empty string
     */
    protected String getDeleteDependencies() { return ""; }



    /*  ======================
        Event Handling
        ======================*/

    /**
     * Updates the table based on the change in filter
     */
    @FXML
    private void toggleFilter() {
        if (monthRadio.isSelected()) {
            // Update Table Filters
            periodPicker.setValue(getFirstOfMonth(picked));
        } else if (weekRadio.isSelected()) {
            // Update Table Filters
            periodPicker.setValue(getBeginOfWeek(picked));
        }
        updateTable();
    }

    /**
     * Automatically set the date picker's value to the first of the month
     */
    @FXML
    private void monthPickerHandler() {

        if (!picked.equals(getFirstOfMonth(picked))){
            periodPicker.setValue(getFirstOfMonth(picked));
        }
    }

    /**
     * Automatically set the date picker's value to the first of the week
     */
    @FXML
    private void weekPickerHandler() {
        if (!picked.equals(getBeginOfWeek(picked))){
            periodPicker.setValue(getBeginOfWeek(picked));
        }
    }

    /**
     * Manages the next and previous period arrow button actions
     * @param actionEvent the arrow press event
     */
    @FXML
    private void arrowsHandler(ActionEvent actionEvent) {
        picked = periodPicker.getValue();

        System.out.println("Changing periods...");
        if (monthRadio.isSelected()) {
            monthArrowsHandler(actionEvent);
        } else if (weekRadio.isSelected()) {
            weekArrowsHandler(actionEvent);
        }
        picked = periodPicker.getValue();
        updateTable();
    }

    /**
     * Manages the next and previous period arrow button actions if using the month filter
     */
    @FXML
    private void monthArrowsHandler(ActionEvent actionEvent) {

        if (actionEvent.getSource().equals(prevBtn)){
            System.out.println("Going to previous month.");
            periodPicker.setValue(getPrevMonth(picked));
        } else if (actionEvent.getSource().equals(nextBtn)){
            System.out.println("Going to next month.");
            periodPicker.setValue(getNextMonth(picked));
        } else {
            errorMessage("Invalid Action","An invalid action was taken.");
        }
    }

    /**
     * Manages the next and previous period arrow button actions if using the week filter
     * @param actionEvent the button press event
     */
    @FXML
    private void weekArrowsHandler(ActionEvent actionEvent) {

        if (actionEvent.getSource().equals(prevBtn)){
            System.out.println("Going to previous week.");
            periodPicker.setValue(getPrevWeek(picked));
        } else if (actionEvent.getSource().equals(nextBtn)){
            System.out.println("Going to next week.");
            periodPicker.setValue(getNextWeek(picked));
        } else {
            errorMessage("Invalid Action","An invalid action was taken.");
        }
    }

    /**
     *
     * @return the start of the period which is always the date in the date picker
     */
    private String getStartPeriod() {
        return "'" + dateToString(picked.atStartOfDay(),"yyyy-MM-dd hh:mm:ss") + "'";
    }


    /**
     *
     * @return the end of the period based on the start of the period and selected filter
     */
    private String getEndPeriod() {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        if (filterSelect.getSelectedToggle().equals(monthRadio)) {
            return "'" + dateToString(getNextMonth(picked).atStartOfDay(),pattern) + "'";
        } else if (filterSelect.getSelectedToggle().equals(weekRadio)) {
            return "'" + dateToString(getNextWeek(picked).atStartOfDay(),pattern) + "'";
        }
        return null;
    }

    /**
     * Deletes selected appointment with confirmation
     */
    @FXML
    public void deleteApptRecord() {
        Appointment appointment = tableView.getSelectionModel().getSelectedItem();

        if (updatable(appointment)) {
            // prompt for confirmation
            boolean confirm = confirmMessage("Delete Appointment", "Are you sure you want to delete Appointment ID " + appointment.getId() + " at " + appointment.getStart() + "?");

            if (confirm) {
                boolean deleted = deleteRecord(appointment);
                System.out.println(deleted);
                if (deleted) {
                    infoMessage("Appointment ID: " + appointment.getId() + "\nAppointment Type: " + appointment.getType() + "\nSuccessfully cancelled.");
                }
                updateTable();
            }

        } else {
            infoMessage("Please select a record for deletion.");
        }
    }

// end of class
}

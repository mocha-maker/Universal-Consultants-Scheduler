package application.controller;

import application.model.Appointment;
import application.model.Contact;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static application.util.Alerts.errorMessage;
import static application.util.Loc.*;

public final class Calendar extends TableBase<Appointment> implements Initializable {


    private LocalDate today = LocalDate.now();
    private LocalDate picked;

    // Set FXML Objects //
    @FXML
    private Text calendarView;

    // Toggle Elements
    @FXML
    private RadioButton monthRadio;
    @FXML
    private RadioButton weekRadio;
    @FXML
    private ToggleGroup filterSelect = new ToggleGroup();
    private static Boolean filterMonth = true;

    // Set Date Navigation Elements

    @FXML
    private Button  prevBtn, nextBtn;
    @FXML
    private DatePicker periodPicker;



    // TODO: Create Weekly and Monthly Views that include Appointments in the respective dates

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        addColumns();

        // set toggle listeners
        setDefaults();
        setToolTips();
    }

    // Populate Data

    private void setListener() {
        periodPicker.valueProperty().addListener(((observableValue, localDate, t1) -> {
            if (t1 != null) {
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

    private void setDefaults() {
        monthRadio.setToggleGroup(filterSelect);
        weekRadio.setToggleGroup(filterSelect);
        periodPicker.setValue(getFirstOfMonth(today));
        setPicked();
        setListener();
    }

    // Create Structure

    private void setToolTips() {
        prevBtn.setTooltip(new Tooltip("Go to previous period."));
        nextBtn.setTooltip(new Tooltip("Go to next period."));

        periodPicker.setTooltip(new Tooltip("Select a period."));
    }

    /**
     * Adds Columns using Generic Table Column Adder
     */
    protected void addColumns() {

        // Set specially formatted columns
        final TableColumn<Appointment, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        final TableColumn<Appointment, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(param -> new SimpleStringProperty(dateToString(param.getValue().getStart(),"yyyy-MM-dd hh:mm a")));

        final TableColumn<Appointment, String> endCol =new TableColumn<>("End");
        endCol.setCellValueFactory(param -> new SimpleStringProperty(dateToString(param.getValue().getEnd(),"yyyy-MM-dd hh:mm a")));

        final TableColumn<Appointment, Integer> custIdCol =new TableColumn<>("CustomerId");
        custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        final TableColumn<Appointment, Integer> userIdCol =new TableColumn<>("UserId");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        tableView.getColumns().addAll(getStringColumn(Appointment.class, "title"),
                getStringColumn(Appointment.class, "description"),
                getStringColumn(Appointment.class, "location"),
                contactCol,
                getStringColumn(Appointment.class,"type"),
                startCol,
                endCol,
                custIdCol,
                userIdCol);
    }

    public void setColumns() {}

    public void updateTable() {
        tableView.getItems().clear();
        tableView.setItems(getFilteredAppointments());
    }

    public ObservableList<Appointment> getFilteredAppointments() {
        allAppointments = FXCollections.observableArrayList();
        String startDate = getStartPeriod();
        String endDate = getEndPeriod();

        try {
            System.out.println("Querying Appointment Database.");
            prepQuery("SELECT * FROM appointments JOIN contacts USING (Contact_ID) JOIN customers USING (Customer_ID) JOIN users USING (User_ID) " +
                    "WHERE Start >= " + startDate + " AND " +
                    "End < " + endDate + " ORDER BY Start DESC");
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
                int apptCustID = rs.getInt("Customer_ID");
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
                        apptCustID,
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

    private void setPicked() {
        picked = periodPicker.getValue();
    }

    protected String getDeleteStatement() { return ""; }

    protected String getDeleteDependencies() { return ""; }


    // EVENT HANDLING
    @FXML
    private void toggleFilter(ActionEvent actionEvent) {
        if (monthRadio.isSelected()) {
            // Change Label?

            // Update Table Filters
            periodPicker.setValue(getFirstOfMonth(today));
        } else if (weekRadio.isSelected()) {
            // Change Label?

            // Update Table Filters
            periodPicker.setValue(getBeginOfWeek(today));
        }
        updateTable();
    }

    @FXML
    private void monthPickerHandler() {

        if (!picked.equals(getFirstOfMonth(picked))){
            periodPicker.setValue(getFirstOfMonth(picked));
        }
    }

    @FXML
    private void weekPickerHandler() {
        if (!picked.equals(getBeginOfWeek(picked))){
            periodPicker.setValue(getBeginOfWeek(picked));
        }
    }

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

    private String getStartPeriod() {
        return "'" + dateToString(picked.atStartOfDay(),"yyyy-MM-dd hh:mm:ss") + "'";
    }

    private String getEndPeriod() {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        if (filterSelect.getSelectedToggle().equals(monthRadio)) {
            return "'" + dateToString(getNextMonth(picked).atStartOfDay(),pattern) + "'";
        } else if (filterSelect.getSelectedToggle().equals(weekRadio)) {
            return "'" + dateToString(getNextWeek(picked).atStartOfDay(),pattern) + "'";
        }
        return null;
    }
    
    

// end of class
}

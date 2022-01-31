package application.controller;

import application.model.Contact;
import application.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static application.controller.CustRecord.getAllCountries;
import static application.controller.TableBase.allContacts;
import static application.controller.TableBase.allCustomers;

/**
 * The Reports Controller
 * Manages the Reports View, loads reports to GUI and generates reports in CSV
 */
public class Reports extends Base {

    /*  ======================
        REPORT VARIABLES
        ======================*/

    /**
     * the selected report
     */
    private String selectedReport;

    /*  ======================
        FXML ELEMENTS
        ======================*/

    @FXML
    Label reportLabel;
    @FXML
    TableView<List<String>> tableView;
    @FXML
    ComboBox<String> reportOptions;
    @FXML
    ComboBox<String> filterOptions;
    @FXML
    Button previewButton;
    @FXML
    Button generateButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setReportOptions();
        setButtons();

    }

    /*  ======================
        SET VIEW STRUCTURES
        ======================*/

    /**
     * Sets the button handlers for previewing and generating reports.
     */
    private void setButtons() {

        previewButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            if (!reportOptions.getSelectionModel().isEmpty()) {
                previewReport(
                        getReport(
                                getReportQuery(selectedReport)));
            } else {
                errorMessage("Unable to preview report.", "Please select a report to preview.");
            }
        });

        generateButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            if (!reportOptions.getSelectionModel().isEmpty()) {
                generateReport(
                        getReport(
                                getReportQuery(selectedReport)));
            } else {
                errorMessage("Unable to generate report.", "Please select a report to generate.");
            }
        });
    }

    /**
     * Sets the report title label
     */
    private void setReportLabel() {
        if (!reportOptions.getSelectionModel().isEmpty()) {
            reportLabel.setText(reportOptions.getValue());
            if(!filterOptions.getSelectionModel().isEmpty()) {
                reportLabel.setText(reportOptions.getValue() + " - " + filterOptions.getValue());
            }
        }
    }

    /**
     * Sets the reporting options in the ChoiceBox
     */
    private void setReportOptions() {
        ObservableList<String> reports = FXCollections.observableArrayList();
        reports.addAll("Monthly Appointments Summary",
                "Contacts Schedules",
                "Customers List By Country",
                "Appointments List By Customer");
        reportOptions.setItems(reports);
        reportOptions.setPromptText("Select a report...");

        ObservableList<String> options = FXCollections.observableArrayList();
        reportOptions.valueProperty().addListener((observableValue, old, newVal) -> {
            options.clear();
            filterOptions.setPromptText("");
            setReportLabel();
            switch (reportOptions.getValue()) {
                case "Monthly Appointments Summary":
                    filterOptions.setPromptText("Select a group by...");
                    options.addAll("Type","Country");
                    break;
                case "Contacts Schedules" :
                    filterOptions.setPromptText("Select a contact...");
                    for (Contact c : allContacts) {
                        options.add(c.getName());
                    }
                    break;
                case "Customers List By Country" :
                    filterOptions.setPromptText("Select a country...");
                    options.addAll(getAllCountries());
                    break;
                case "Appointments List By Customer" :
                    filterOptions.setPromptText("Select a customer...");
                    for (Customer c : allCustomers) {
                        options.add(c.getCustomerName());
                    }
                    break;
            }
        }
        );
        filterOptions.setItems(options);

        filterOptions.valueProperty().addListener((observableValue, oldVal, newVal) -> setReportLabel());
    }

    /*  ======================
        REPORT QUERIES
        ======================*/

    /**
     * Triages through query methods based on the selectedReport from the "reports" ChoiceBox
     * @param selectedReport - the report selected
     * @return - the query to get the report selected
     */
    private String getReportQuery(String selectedReport) {
        System.out.println("Running Query based on selected report.");
        if (!filterOptions.getSelectionModel().isEmpty()) {

            switch (selectedReport) {
                case "Monthly Appointments Summary":
                    return apptSummaryQuery();

                case "Contacts Schedules":
                    return reportScheduleQuery();

                case "Customers List By Country":
                    return customersByCountryQuery();

                case "Appointments List By Customer":
                    return appointmentsByCustomerQuery();

                default:
                    return "";
            }
        } else {
            errorMessage("Unable to get report","Please select a filter option.");
        }
        return "";
    }

    /**
     * Appointments Summary by Month and Country or Type
     * Shows the total number of customer appointments by filter Option per month
     * @return the query for an appointments summary report
     */
    private String apptSummaryQuery() {
        System.out.println("Returning Appointment Summary Query...");
         return "SELECT date_format(Start, '%Y %M') AS Month, " + filterOptions.getValue() + ", COUNT(*) FROM appointments \n" +
                 "JOIN customers USING (Customer_ID) \n" +
                 "JOIN first_level_divisions USING (Division_ID)\n" +
                 "JOIN countries USING (Country_ID)\n" +
                 "GROUP BY MONTH(Start), " + filterOptions.getValue() + " " +
                 "ORDER BY Start ASC" ;
    }

    /**
     * Schedule: a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
     * @return the query for the schedules of each contact
     */
    private String reportScheduleQuery() {
        System.out.println("Returning Contact Schedule Query...");

            return "SELECT Contact_Name, Appointment_ID, Start, End, Title, Location, Type " +
                    "FROM appointments " +
                    "JOIN contacts USING (Contact_ID) " +
                    "WHERE Contact_Name = '" + filterOptions.getValue() +"' " +
                    "order by Contact_ID";
    }

    /**
     * Customer List by Country
     * @return the query for a list of customers and their info by country
     */
    private String customersByCountryQuery() {
        System.out.println("Returning Customers By Country Query...");

            return "SELECT Customer_Name, Phone, Address, Division, Country " +
                    "FROM customers " +
                    "JOIN first_level_divisions USING (Division_ID) " +
                    "JOIN countries USING (Country_ID) " +
                    "WHERE Country = '" + filterOptions.getValue() + "' " +
                    "ORDER BY Country_ID;";
    }

    private String appointmentsByCustomerQuery() {
        System.out.println("Returning Appointments by Customer Query...");
        return "SELECT Appointment_ID, Start, End,  Type, Title, Location, Contact_Name " +
                "FROM appointments " +
                "JOIN contacts USING (Contact_ID) " +
                "JOIN customers USING (Customer_ID) " +
                "WHERE Customer_Name = '" + filterOptions.getValue() +"' " +
                "ORDER BY Start ASC";
    }

    /*  ======================
        REPORTING METHODS
        ======================*/

    /**
     * Runs the query and retrieves the ResultSet into a List of Lists Array for parsing
     * @param query - the query to run
     * @return List of Lists Array of ResultSet
     */
    private ObservableList<List<String>> getReport(String query) {
        ObservableList<List<String>> resultSetArray = FXCollections.observableArrayList();
        if (!query.isEmpty()) {
            // Querying database
            try {
                PreparedStatement ps = getConnection().prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                int numCols = rs.getMetaData().getColumnCount();

                // Build Header
                List<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= numCols; i++) {
                    row.add(rs.getMetaData().getColumnLabel(i));
                }
                System.out.println(row);
                resultSetArray.add(row);

                // Add rows
                while (rs.next()) {
                    row = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        row.add(rs.getString(i));
                    }
                    System.out.println(row);
                    resultSetArray.add(row);
                }

            } catch (SQLException e) {
                printSQLException(e);
            }
        } else {
            System.out.println("No query returned.");
        }

        System.out.println(resultSetArray.get(0).toString());
        return resultSetArray;
    }


    // Event Handling

    /**
     * Build strings to show in tableView
     * @param resultArray - the report results
     */
    private void previewReport(ObservableList<List<String>> resultArray) {

        tableView.getColumns().clear();
        System.out.println("Previewing report");
        System.out.println("Generating columns...");
        List<String> header = resultArray.get(0);
        resultArray.remove(0);

        for (int i=0;i< header.size();i++) {
            TableColumn<List<String>, String> col = new TableColumn<>(header.get(i));
            int j = i;
            col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(j)));
            tableView.getColumns().addAll(col);
        }
        tableView.setItems(resultArray);
    }

    /**
     * Prints report results to a csv file.
     * @param resultArray - the report results
     */
    public void generateReport(ObservableList<List<String>> resultArray) {
        boolean saveFileConfirm = confirmMessage("Save File","Do you want to save report as a CSV file?");

        if (saveFileConfirm) {
            System.out.println("Generating csv file.");
            String fileName = selectedReport.replace(" ", "_");
            File outputFile = new File(fileName + ".csv");

            FileWriter fileWriter;
            if (outputFile.exists()) {
                boolean overwrite = confirmMessage("Confirm Overwrite", "File already exists, would you like to overwrite it?");
                if (!overwrite) {
                    int i = 1;
                    while (outputFile.exists()) {
                        outputFile = new File(fileName + "_" + i + ".csv");
                        i++;
                    }
                }  // do nothing and continue
            }

            System.out.println(outputFile);

            try {
                fileWriter = new FileWriter(outputFile, false);

                for (List<String> row : resultArray) {
                    for (String cell : row) {
                        fileWriter.write(cell + ", ");

                    }
                    fileWriter.write("\n");
                }

                fileWriter.close();
                infoMessage("Report Generated!");

            } catch (IOException e) {
                System.out.println("In catch block.");
                errorMessage("File Unavailable", "Unable to open file. Please check if it is open.");
            }
        }
    }

// end of class
}





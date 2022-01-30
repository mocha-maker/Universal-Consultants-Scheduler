package application.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The Reports Controller
 * Manages the Reports View, loads reports to GUI and generates reports in CSV
 */
public class Reports extends Base {

    /*  ======================
        REPORT VARIABLES
        ======================*/

    private String selectedReport;
    @FXML
    private TextArea reportView;

    /*  ======================
        FXML ELEMENTS
        ======================*/
    @FXML
    ChoiceBox<String> reportOptions;
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
            infoMessage("Previewing your " + selectedReport + " soon!");
            previewReport(
                    getReport(
                            getReportQuery(selectedReport)));
        });

        generateButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            infoMessage("Generating your " + selectedReport + " soon!");
            generateReport(
                    getReport(
                            getReportQuery(selectedReport)));
        });

    }

    /**
     * Sets the reporting options in the ChoiceBox
     */
    private void setReportOptions() {
        ObservableList<String> reports = FXCollections.observableArrayList();
        reports.addAll("Appointments Summary (Type) Report",
                "Appointments Summary (Country) Report",
                "Contacts Schedules",
                "Customers List By Country",
                "Login Activity Report");
        reportOptions.setItems(reports);
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
        switch (selectedReport) {
            case "Appointments Summary (Type) Report" :
                return apptSummaryByTypeQuery();

            case "Appointments Summary (Country) Report":
                return apptSummaryByCountryQuery();


            case "Contacts Schedules" :
                return reportScheduleQuery();

            case "Customers List By Country":
                return customersByCountryQuery();

            default:
                return "";
        }
    }

    /**
     * Appointments Summary by Type: the total number of customer appointments by type
     * @return the query for an appointments summary report
     */
    public String apptSummaryByTypeQuery() {
        System.out.println("Returning Appointment Summary Query...");

        return "SELECT Type, COUNT(*) AS Total_Appointments FROM appointments " +
                "group by Type";
    }

    /**
     * Schedule: a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
     * @return the query for the schedules of each contact
     */
    public String reportScheduleQuery() {
        System.out.println("Returning Contact Schedule Query...");

        return "SELECT Contact_Name, Appointment_ID, Start, End, Title, Location, Type " +
                "FROM appointments " +
                "JOIN contacts USING (Contact_ID) order by Contact_ID";
    }

    /**
     * Customer List by Country
     * @return the query for a list of customers and their info by country
     */
    public String customersByCountryQuery() {
        System.out.println("Returning Customers By Country Query...");

        return "SELECT Country, Customer_Name, Address, Phone \n" +
                "FROM customers \n" +
                "JOIN first_level_divisions USING (Division_ID) \n" +
                "JOIN countries USING (Country_ID)\n" +
                "ORDER BY Country_ID;";
    }

    /**
     * Appointments Summary by Country: the total number of customer appointments by type and month
     * @return the query for an appointments summary report
     */
    public String apptSummaryByCountryQuery() {
        System.out.println("Returning Appointment Summary Query...");

        return "SELECT Country, COUNT(*) AS Total_Appointments FROM appointments " +
                "JOIN customers USING (Customer_ID) " +
                "JOIN first_level_divisions USING (Division_ID)" +
                "JOIN countries USING (Country_ID)" +
                "group by Country";
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
            while(rs.next()) {
                row = FXCollections.observableArrayList();
                for (int i = 1; i <= numCols; i++) {
                        row.add(rs.getString(i));
                }
                System.out.println(row);
                resultSetArray.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(resultSetArray.get(0).toString());
        return resultSetArray;
    }


    // Event Handling

    /**
     * Build strings to show in TextArea
     * @param resultArray - the report results
     */
    private void previewReport(ObservableList<List<String>> resultArray) {
        System.out.println("Previewing report");
        StringBuilder preview = new StringBuilder();

        for (List<String> strings : resultArray) {
            for (int j = 0; j < strings.size(); j++) {
                preview.append(strings.get(j));
                if (j != strings.size() - 1) {
                    preview.append(" | ");
                }
            }
            preview.append("\n");
        }

        reportView.setText(preview.toString());

    }


    /**
     * Prints report results to a csv file.
     * @param resultArray - the report results
     */
    public void generateReport(ObservableList<List<String>> resultArray) {
        System.out.println("Generating csv file.");
        String fileName = selectedReport.replace(" ","_");
        File outputFile = new File(fileName+".csv");

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
                errorMessage("File Unavailable","Unable to open file. Please check if it is open.");
            }

    }



// end of class
}





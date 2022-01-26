package application.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static application.util.Alerts.infoMessage;
import static application.util.Loc.dateToString;

public class Reports extends Base {

    // Set FXML Elements
    @FXML
    ChoiceBox<String> reportOptions;
    String selectedReport;
    @FXML
    Button previewButton, generateButton, printButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setReportOptions();
        setButtons();
    }

    private void setButtons() {
        previewButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            infoMessage("Previewing your " + selectedReport + " soon!");
            getReportQuery(selectedReport);
            previewReport();
        });

        generateButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            infoMessage("Generating your " + selectedReport + " soon!");
            getReportQuery(selectedReport);
        });

        printButton.setOnAction(EventHandler -> {
            selectedReport = reportOptions.getValue();
            infoMessage("Printing your " + selectedReport + " soon!");
        });
    }


    private void getReportQuery(String selectedReport) {
        System.out.println("Running Query based on selected report.");
        switch (selectedReport) {
            case "Appointments Summary Report" :
                apptSummaryQuery();
            case "Contacts Schedules" :
                reportScheduleQuery();
            case "Customers List by Country":
                customersByCountryQuery();
            case "Report #4":
                reportFour();
            default:
                break;
        }
    }


    // Form Structure
    private void setReportOptions() {
        ObservableList reports = FXCollections.observableArrayList();
        reports.addAll("Appointments Summary Report","Contacts Schedules", "Customers List By Country","Report #4","Login Activity Report");
        reportOptions.setItems(reports);

    }



    private List<String> resultSetArray = FXCollections.observableArrayList();

    // Appointments Summary: the total number of customer appointments by type and month
    public void apptSummaryQuery() {
        getReport("SELECT Type, COUNT(*) AS  Total_Appointments FROM appointments group by Type");

        getReport("SELECT Country, COUNT(*) AS Total_Appointments FROM appointments " +
                "JOIN customers USING (Customer_ID) " +
                "JOIN first_level_divisions USING (Division_ID)" +
                "JOIN countries USING (Country_ID)" +
                "group by Country;");
    }


    // Schedule: a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
    public String reportScheduleQuery() {
        return "";

    }

    // Customer List by Country
    public String customersByCountryQuery() {
        return "";

    }

    // Customer List by Country
    public String reportFour() {
        return "";

    }

    // Reporting

    public void getReport(String query) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int numCols = rs.getMetaData().getColumnCount();

            // Build Header
            StringBuilder header = new StringBuilder();

            for (int i = 1; i <= numCols; i++) {
                if (i < numCols) {
                    header.append(rs.getMetaData().getColumnLabel(i)).append(", ");
                } else {
                    header.append(rs.getMetaData().getColumnLabel(i));
                }
            }
            resultSetArray.add(header.toString());
            System.out.println(resultSetArray);

            // Add rows
            while(rs.next()) {
                StringBuilder row = new StringBuilder();

                for (int i = 1; i <= numCols; i++) {
                    if (i < numCols) {
                        row.append(rs.getString(i)).append(", ");
                    } else {
                        row.append(rs.getString(i)).append("\n");
                    }
                }
                resultSetArray.add(row.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        generateReport(resultSetArray);
    }


    // Login Activity Report
    public static void loginActivity(ZonedDateTime zonedDateTime, String user, Boolean result) {
        FileWriter logFile;
        String status = (result ? "Succeeded" : "Failed");
        String timestamp = dateToString(zonedDateTime, "yyyy-MM-dd hh:mm:ss a zzz");
        System.out.println(timestamp);
        try {
            logFile = new FileWriter("login_activity.txt", true);
            logFile.write("[" + timestamp + "] Login attempt by user: " + user + " - Attempt " + status + "\n");
            logFile.close();
            System.out.println("Login Activity Recorded.");
        } catch (IOException e) {
            System.out.println("In catch block.");
            e.printStackTrace();
        }
    }

    // Event Handling

    private void previewReport() {
        // TODO: send to node
    }

    // Print to PDF
    public static void generateReport(List<String> resultArray) {
        System.out.println("Generating csv file.");
        String fileName = "newReport.csv";
        File outputFile = new File(fileName);
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(outputFile,false);
            for(String mapping : resultArray) {
                fileWriter.write(mapping + "\n");
            }

            fileWriter.close();

        } catch (IOException e) {
            System.out.println("In catch block.");
            e.printStackTrace();
        }
    }

    private void printReport() {
        previewReport(); // need to send query to node before printing

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        //if(printerJob.showPrintDialog(stage.getOwner()) && printerJob.printPage(yourNode))
        printerJob.endJob();
    }



// end of class
}





package application.controller;

import application.model.Appointment;
import application.model.Contact;
import application.model.Customer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static application.controller.CustTable.getAllCustomers;
import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.infoMessage;
import static application.util.Loc.timeStampToLocal;
import static application.util.Loc.dateToString;

public final class ApptTable extends TableBase<Appointment> implements Initializable {
    
    /*  ======================
        APPOINTMENT TABLE ELEMENTS
        ======================*/



    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/

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

        final TableColumn<Appointment, String> custIdCol =new TableColumn<>("CustomerId");
        custIdCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getCustomer().getId())));

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

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateTable() {
        tableView.getItems().clear();
        tableView.setItems(getAllAppointments());
    }

    /**
     *
     * @return
     */
    public static ObservableList<Appointment> getAllAppointments() {
       allAppointments.clear();

        try {
            System.out.println("Querying Appointment Database.");
            prepQuery("SELECT * FROM appointments JOIN contacts USING (Contact_ID) JOIN customers USING (Customer_ID) JOIN users USING (User_ID) ORDER BY Appointment_ID ASC");
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


    /*  ======================
        Event Handling
        ======================*/


    /**
     *
     * @return SQL statement to delete appointment record
     */
    protected String getDeleteStatement() {
        return "DELETE FROM appointments WHERE Appointment_ID = ?";
    }

    public void deleteApptRecord(ActionEvent e) {
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

    /**
     * empty method
     * @return empty string
     */
    protected String getDeleteDependencies() { return "";}



    // end of class
}

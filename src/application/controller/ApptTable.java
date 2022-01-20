package application.controller;

import application.model.Appointment;
import application.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static application.util.Alerts.confirmMessage;
import static application.util.Alerts.infoMessage;
import static application.util.Loc.dateToString;

public final class ApptTable extends TableBase<Appointment> implements Initializable {
    
    /*  ======================
        APPOINTMENT TABLE ELEMENTS
        ======================*/

    public static ObservableList<Contact> allContacts;
    public static ObservableList<Appointment> allAppointments;

    // TODO TableColumn Factory

    public TableView<Appointment> allAppointmentsTable;
    public TableColumn<?,?> apptID;
    public TableColumn<?,?> apptTitle;
    public TableColumn<?,?> apptDesc;
    public TableColumn<?,?> apptLoc;
    public TableColumn<?,?> apptType;
    public TableColumn<Appointment, Timestamp> apptStart;
    public TableColumn<Appointment, Timestamp> apptEnd;
    public TableColumn<?,?> apptContact;
    public TableColumn<?,?> apptCustID;
    public TableColumn<?,?> apptUserID;


    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/
    public void setColumns() {

        System.out.println("Setting Appointment Table Columns.");
        apptID.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
apptStart.setCellFactory(column -> {
    TableCell<Appointment, Timestamp> cell = new TableCell<Appointment, Timestamp>() {

        @Override
        protected void updateItem(Timestamp item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                this.setText(dateToString(item.toLocalDateTime(),"yyyy-MM-dd hh:mm a"));
            }
        }
    };
    return cell;
});
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptEnd.setCellFactory(column -> {
            TableCell<Appointment, Timestamp> cell = new TableCell<Appointment, Timestamp>() {

                @Override
                protected void updateItem(Timestamp item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                    } else {
                        this.setText(dateToString(item.toLocalDateTime(),"yyyy-MM-dd hh:mm a"));
                    }
                }
            };
            return cell;
        });
        apptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptCustID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateTable() {
         allAppointmentsTable.setItems(getAllAppointments());
    }


    /**
     *
     * @return
     */
    public static ObservableList<Appointment> getAllAppointments() {
        allAppointments = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Appointment Database.");
            prepQuery("SELECT * FROM appointments JOIN contacts USING (Contact_ID) JOIN customers USING (Customer_ID) JOIN users USING (User_ID)");
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

                Timestamp apptStart = rs.getTimestamp("Start");
                Timestamp apptEnd = rs.getTimestamp("End");

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

    public static ObservableList<Contact> getAllContacts() {
        allContacts = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Contacts Database.");
            prepQuery("SELECT * FROM contacts");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Contact Variables.");
                int contact_id = rs.getInt("Contact_ID");
                String contact_name = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                // construct Contact object using result
                System.out.println("Constructing Contact Object.");
                Contact contactResult = new Contact(contact_id,
                        contact_name,
                        email);

                // add Contact object to Observable List
                allContacts.add(contactResult);
                i++;
                System.out.println("Contact added to Observable List. (" + i + ")");

            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allContacts;
    }


    /*  ======================
        Event Handling
        ======================*/
    /**
     * Open Appointment Record
     * TODO: Update record title depending on which button is clicked
     */
    @FXML
    public void toApptRecord(ActionEvent e) throws IOException {
       // Declare Local Variables
        // record which button was clicked
        String button = ((Button)e.getSource()).getText();
        System.out.println(button);
        String action = "add";
        if ( button.contains("New") ) {
            action = "add";
        } else if ( button.contains("Update") ) {
            action = "edit";
        }
        System.out.println("Action needed = " + action);

        // capture selected appointment from table
        Appointment appointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

        if (appointment !=  null || action == "add") {
            // TODO: Transfer parameters to Controller
            //System.out.println(getSceneLoader("ApptRecord").getController().toString());
            FXMLLoader loader = setLoader("ApptRecord");
            Parent root = loader.load();

            ApptRecord controller = loader.getController();
            controller.getParams(action, appointment);

            popupScene(root, "Appointment Record");

             // send button action and table row item
        } else if ( action == "edit" && appointment == null){
            infoMessage("Please select a record for modification.");
        }
        updateTable();
    }


    /**
     *
     * @return SQL statement to delete appointment record
     */
    protected String getDeleteStatement() {
        return "DELETE FROM appointments WHERE Appointment_ID = ?";
    }

    public void deleteApptRecord(ActionEvent e) {
        Appointment appointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

        if (appointment != null) {
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

package application.controller;

import application.model.Appointment;
import application.model.Customer;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.util.Alerts.*;
import static application.util.DAOimpl.*;

public class ApptTable extends Base {
    
    /*  ======================
        APPOINTMENT TABLE ELEMENTS
        ======================*/

    public TableView<Appointment> allAppointmentsTable;
    public TableColumn<?,?> apptID;
    public TableColumn<?,?> apptTitle;
    public TableColumn<?,?> apptDesc;
    public TableColumn<?,?> apptLoc;
    public TableColumn<?,?> apptType;
    public TableColumn<?,?> apptStart;
    public TableColumn<?,?> apptEnd;
    public TableColumn<?,?> apptContact;
    public TableColumn<?,?> apptCustID;
    public TableColumn<?,?> apptUserID;

    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setColumns();
        updateAppointmentsTable();

    }
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
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptCustID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    /**
     * Updates Customer Table
     * used to populate tableview
     */
    public void updateAppointmentsTable() {
         allAppointmentsTable.setItems(getAllAppointments());
    }


    /*  ======================
        Event Handling
        ======================*/
    /**
     * Open Appointment Record
     * TODO: Update record title depending on which button is clicked
     */
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

        if (appointment != null || action == "add") {
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

    }
    public void deleteApptRecord(ActionEvent e) {
        Appointment appointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

        if (appointment != null) {
            // prompt for confirmation
            boolean confirm = confirmMessage("Delete Appointment", "Are you sure you want to delete Appointment ID " + appointment.getId() + " at " + appointment.getStart() + "?");

            if (confirm) {
                cancelAppt(appointment);
                updateAppointmentsTable();
            }

        } else {
            infoMessage("Please select a record for deletion.");
        }
    }

    // end of class
}

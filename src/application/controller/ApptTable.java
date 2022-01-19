package application.controller;

import application.model.Appointment;
import application.model.Contact;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import static application.util.Alerts.*;
import static application.util.DAOimpl.*;

public final class ApptTable extends TableBase<Appointment> implements Initializable {
    
    /*  ======================
        APPOINTMENT TABLE ELEMENTS
        ======================*/

    // To access Contact Name
    private final HashMap<Integer, Contact> contactMap = new HashMap<>();

    // TODO TableColumn Factory

    public TableView<Appointment> allAppointmentsTable;
    public TableColumn<?,?> apptID;
    public TableColumn<?,?> apptTitle;
    public TableColumn<?,?> apptDesc;
    public TableColumn<?,?> apptLoc;
    public TableColumn<?,?> apptType;
    public TableColumn<?,?> apptStart;
    public TableColumn<?,?> apptEnd;
    public TableColumn<Contact,String> apptContact;
    public TableColumn<?,?> apptCustID;
    public TableColumn<?,?> apptUserID;


    /*  ======================
        TABLEVIEW MANAGEMENT
        ======================*/
    public void setColumns() {
        makeContactMap();
        System.out.println("Setting Appointment Table Columns.");
        apptID.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
/*        apptContact.setCellValueFactory(param -> {
            final Optional<Contact> contact = Optional.ofNullable(contactMap.get(param.getValue().getId()));
            return new SimpleStringProperty(contact.map(Contact::getName).orElse(""));
        });*/
        apptContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
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



    protected String getDeleteStatement() {
        return "DELETE FROM appointments WHERE Appointment_ID = ?";
    }



    public void deleteApptRecord(ActionEvent e) {
        Appointment appointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

        if (appointment != null) {
            // prompt for confirmation
            boolean confirm = confirmMessage("Delete Appointment", "Are you sure you want to delete Appointment ID " + appointment.getId() + " at " + appointment.getStart() + "?");

            if (confirm) {
                deleteRecord(appointment);
                updateTable();
            }

        } else {
            infoMessage("Please select a record for deletion.");
        }
    }

    // Contact Map Maker
    private void makeContactMap() {
        try {
            prepQuery("SELECT * FROM contacts");
            ResultSet rs = getResult();
            while (rs.next()) {
                final Contact contact = new Contact(rs.getInt(1), rs.getString(2), rs.getString((3)));
                contactMap.put(contact.getId(), contact);
            }
        } catch (SQLException exception) {
            printSQLException(exception);
        }
    }

    // end of class
}

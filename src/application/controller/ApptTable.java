package application.controller;

import application.model.Appointment;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static application.util.DAOimpl.getAllAppointments;

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

}

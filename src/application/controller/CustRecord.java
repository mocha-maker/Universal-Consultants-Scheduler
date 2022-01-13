package application.controller;

import application.model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static application.util.DAOimpl.*;

public class CustRecord extends Base {

    Customer formCustomer;
    Boolean formTypeNew = true;

    // Set FXML Variables
    @FXML
    Text custRecordTitle;
    @FXML
    TextField custID;
    @FXML
    TextField custName;
    @FXML
    ComboBox<String> country;
    @FXML
    TextField address;
    @FXML
    ComboBox<String> division;
    @FXML
    TextField code;
    @FXML
    TextField phone;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();

        // TODO: Add data entry validation for phone and postal/zip code (depends on country)
    }

    public void getParams(String action, Customer customer) {
        System.out.println("Transferring parameters to new controller.");
        System.out.println("Setting Selected Appointment.");
        formCustomer = customer;

        System.out.println("Updating Title String...");
        switch (action) {
            case "add":
                custRecordTitle.setText("Add New Appointment");
                formTypeNew = true;
                break;
            case "edit" :
                custRecordTitle.setText("Edit Existing Appointment");
                formTypeNew = false;
                break;
            default:
                break;
        }
        populateForm();
    }

    private void populateForm() {

        if (!formTypeNew) {
            // populate from formCustomer
            custID.setText(String.valueOf(formCustomer.getId()));
            custName.setText(formCustomer.getCustomerName());
            country.setValue(formCustomer.getCountry());
            address.setText(formCustomer.getAddress());
            division.setValue(formCustomer.getDivision());
            code.setText(formCustomer.getPostalCode());
            phone.setText(formCustomer.getPhone());
        } else {
            division.setDisable(true);
            // generate id
        }

    }

    private void setComboBoxes() {
        setCountriesCB();
    }

    private void setCountriesCB() {
        System.out.println("Setting Countries Combo Box...");
        country.setPromptText("Select a country.");

        try {
            country.getItems().addAll(getAllCountries());
        } catch (NullPointerException ex) {
            System.out.println("No Countries Found");
        }

        country.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            if(newVal != oldVal){
                setRegionsCB();
            }
        });
    }

    @FXML
    private void setRegionsCB() {
        System.out.println("Setting Regions Combo Box...");
        division.setDisable(false);
        division.getItems().clear(); // clear list before re-populating based on change
        division.setPromptText("Select a Region.");

        try {
            division.getItems().addAll(getAllRegions(country.getValue()));
        } catch (NullPointerException ex) {
            System.out.println("No Regions Found");
        }

    }

    // TODO: Set Customer-related Appointments tableview (see apptTable)

}

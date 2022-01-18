package application.controller;

import application.model.Customer;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.util.DAOimpl.*;

public class CustRecord extends Base {

    Customer formCustomer;
    Boolean formTypeNew = true;
    @FXML
    Button saveButton;

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

    // Class Level Validation Booleans for Bindings
    private boolean newNameValid = false;
    private boolean newAddressValid = false;
    private boolean newCodeValid = false;
    private boolean newPhoneValid = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up form structures
        setComboBoxes();
        addListeners();


        // TODO: Add data entry validation for phone and postal/zip code (depends on country)
    }

    // FORM VALIDATION AND MANAGEMENT

    private void bindSaveButton() {
        BooleanBinding isAllValid = new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return (custName.getText().isEmpty()
                        || country.getValue().isEmpty()
                        || address.getText().isEmpty()
                        || division.getValue().isEmpty()
                        || code.getText().isEmpty()
                        || phone.getText().isEmpty());
            }
        };
        saveButton.disableProperty().bind(isAllValid);
    }

    public void addListeners() {

        custName.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking name input...");
            if(!custName.getText().isEmpty() && oldValue != newValue) {
                newNameValid = validation(custName, "[a-zA-Z0-9 ]*", "Please only use alphanumeric characters and spaces.");
            }
        });

        address.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking address input...");
            if(!address.getText().isEmpty() && oldValue != newValue) {
                newAddressValid = validation(address, "[a-zA-Z0-9 ]*", "Please only use alphanumeric characters and spaces.");
            }
        });

        code.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking address code input...");
            if(!code.getText().isEmpty() && oldValue != newValue) {
                switch (country.getValue()) {
                    case "U.S.":
                        newCodeValid = validation(code, "^[0-9]{5}(?:-[0-9]{4})?$", "Please enter a valid US zipcode with 5 digits.");
                        break;
                    case "UK":
                        newCodeValid = validation(code, "^[A-Z]{1,2}[0-9R][0-9A-Z]?●[0-9][ABD-HJLNP-UW-Z]{2}$", "Please enter a valid UK post code in format AA1 1AA or AA1A 1AA.");
                        break;
                    case "Canada":
                        newCodeValid = validation(code, "/^[ABCEGHJ-NPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ -]?\\d[ABCEGHJ-NPRSTV-Z]\\d$/i", "Please enter a valid Canadian postal code in format A1A 1A1.");
                        break;
                    default :
                        break;
                }
            }
        });

        phone.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking name input...");
            if(!phone.getText().isEmpty() && oldValue != newValue) {
                newPhoneValid = validation(phone, "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", "Please enter a valid phone number with 10-digits");
            }
        });

    }

    // FORM SETUP

    /**
     * Transfer parameters from other controllers to this one
     * @param action
     * @param customer
     */
    public void getParams(String action, Customer customer) {
        System.out.println("Transferring parameters to new controller.");
        System.out.println("Setting Selected Customer.");
        formCustomer = customer;

        System.out.println("Updating Title String...");
        switch (action) {
            case "add":
                custRecordTitle.setText("Add New Customer");
                formTypeNew = true;
                break;
            case "edit" :
                custRecordTitle.setText("Edit Existing Customer");
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
            generateId();
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

        if(division.isDisabled()) {
            division.setDisable(false);
        }
        division.getItems().clear(); // clear list before re-populating based on change
        division.setPromptText("Select a Region.");

        try {
            division.getItems().addAll(getAllRegions(country.getValue()));
        } catch (NullPointerException ex) {
            System.out.println("No Regions Found");
        }

    }

    // TODO: Generate ID method
    private void generateId() {

        custID.setText(String.valueOf(getAvailableID("customers")));
    }

    // TODO: Set Customer-related Appointments tableview (see apptTable)

    // TODO: SAVE BUTTON
    @FXML
    private void saveCustomer(ActionEvent actionEvent) {
        // Collect textfield values

        Customer newCust = new Customer(Integer.parseInt(custID.getText()),
                                        custName.getText(),
                                        phone.getText(),
                                        address.getText(),
                                        code.getText(),
                                        division.getValue(),
                                        country.getValue());

        // check form type
        if (formTypeNew) {
            addCust(newCust);
            exitButton(actionEvent);
        } else {
            updateCust(newCust);
            exitButton(actionEvent);
        }
    }


}

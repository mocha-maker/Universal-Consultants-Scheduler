package application.controller;

import application.model.Customer;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static application.util.Alerts.infoMessage;

import static application.util.Loc.getCurrentTimestamp;


public class CustRecord extends RecordBase<Customer> {

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



        custName.getTextFormatter();
        custName.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking name input...");
            if(!custName.getText().isEmpty() && oldValue != newValue ) {
                newNameValid = validation(custName, "[a-zA-Z0-9 ]*", "Please only use alphanumeric characters and spaces.");
            }
        });

        address.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking address input...");
            if(!address.getText().isEmpty() && oldValue != newValue ) {
                newAddressValid = validation(address, "[a-zA-Z0-9 ]*", "Please only use alphanumeric characters and spaces.");
            }
        });

        code.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Checking postal code input...");
            if(!code.getText().isEmpty() && oldValue != newValue ) {
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
            System.out.println("Checking phone input...");
            if(!phone.getText().isEmpty() && oldValue != newValue ) {
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
            custID.setText(genId("customer"));
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

        List<Object> params;


        // check form type
        if (formTypeNew) {
            params = toList(
                    newCust.getId(),
                    newCust.getCustomerName(),
                    newCust.getPhone(),
                    newCust.getAddress(),
                    newCust.getPostalCode(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    getDivisionID(newCust.getDivision())
            );
            addRecord(newCust,params);
            exitButton(actionEvent);
        } else {
            params = toList(newCust.getCustomerName(),
                    newCust.getPhone(),
                    newCust.getAddress(),
                    newCust.getPostalCode(),
                    getCurrentTimestamp(),
                    getActiveUser().getUserName(),
                    getDivisionID(newCust.getDivision()),
                    newCust.getId());
            boolean updated = updateRecord(newCust,params);
            exitButton(actionEvent);
            if (updated) { infoMessage("Customer ID: " + newCust.getId() + "\nSuccessfully Updated");}
        }
    }

    public String getInsertStatement() {
        return "INSERT INTO customers VALUES (?,?,?,?,?,?,?,?,?,?)";
    }

    public String getUpdateStatement() {
        return "UPDATE customers SET " +
                "Customer_Name = ?," +
                "Phone = ?, " +
                "Address = ?, " +
                "Postal_Code = ?, " +
                "Last_Update = ?, " +
                "Last_Updated_By = ?, " +
                "Division_ID = ? " +
                "WHERE Customer_ID = ?";
    }

    public static int getDivisionID(String divisionName) {
        System.out.println("Retrieving Division ID for Division " + divisionName);
        int divId = 0;
        try {
            prepQuery("SELECT * FROM first_level_divisions WHERE Division = '" + divisionName +"'");
            ResultSet rs = getResult();
            while (rs.next()) {
                divId = rs.getInt("Division_ID");
                System.out.println("Division ID Retrieved = " + divId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return divId;
    }

    public static ObservableList<String> getAllCountries() {
        ObservableList<String> allCountries = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Countries Database for Unique Items.");
            prepQuery("SELECT * FROM countries ORDER BY Country ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Countries.");
                String countryResult = rs.getString("Country");
                // add Type String to Observable List
                allCountries.add(countryResult);
                i++;
                System.out.println(countryResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allCountries;
    }

    public static ObservableList<String> getAllRegions(String country) {
        ObservableList<String> allRegions = FXCollections.observableArrayList();

        try {
            System.out.println("Querying Regions Database for Unique Items.");
            prepQuery("SELECT * FROM first_level_divisions JOIN countries USING (Country_ID) WHERE Country = '" + country +"' ORDER BY Division ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Regions.");
                String regionResult = rs.getString("Division");
                // add Type String to Observable List
                allRegions.add(regionResult);
                i++;
                System.out.println(regionResult + " Type added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allRegions;
    }

}

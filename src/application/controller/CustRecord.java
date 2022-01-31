package application.controller;

import application.model.Customer;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static application.util.Loc.getCurrentTimestamp;

/**
 * Customer Record Controller
 * Manages the Customer Record Form for Adding and Editing Customers
 */
public class CustRecord extends RecordBase<Customer> {


    // Set FXML Variables
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

    // Validation Variables
    Boolean nameValid;
    Boolean addressValid;
    Boolean countryValid;
    Boolean divisionValid;
    Boolean phoneValid;
    Boolean codeValid;


    // TODO: Class Level Validation Booleans for Bindings

    /**
     * Pass parameters from Customer Table
     * @param action - the button action that was taken
     * @param customer - the customer to load on the form
     */
    @Override
    protected void getParams(String action, Customer customer) {
        System.out.println("Transferring parameters to new controller.");
        System.out.println("Setting Selected Customer.");
        record = customer;

        System.out.println("Updating Title String...");
        switch (action) {
            case "New":
                formTitle.setText("Add New Customer");
                formTypeNew = true;

                division.setDisable(true);
                // generate id
                custID.setText(genId("customer"));

                break;
            case "Edit" :
                formTitle.setText("Edit Existing Customer");
                formTypeNew = false;

                // populate from formCustomer
                custID.setText(String.valueOf(record.getId()));
                custName.setText(record.getCustomerName());
                country.setValue(record.getCountry());
                address.setText(record.getAddress());
                division.setValue(record.getDivision());
                code.setText(record.getAddressCode());
                phone.setText(record.getPhone());

                break;
            default:
                break;
        }
        bindSaveButton();
    }

    // FORM VALIDATION AND MANAGEMENT

    /**
     * Binds saveButton to the other string fields to ensure all fields are completed
     */
    private void bindSaveButton() {
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
                () ->   // how to calculate value
                        custName.getText().isEmpty() ||
                        address.getText().isEmpty() ||
                        code.getText().isEmpty() ||
                        phone.getText().isEmpty() ||
                        country.getSelectionModel().isEmpty() ||
                        division.getSelectionModel().isEmpty(),

                        // elements to look at
                        custName.textProperty(),
                        address.textProperty(),
                        code.textProperty(),
                        phone.textProperty(),
                        country.selectionModelProperty(),
                        division.selectionModelProperty()
        ));
    }

    /**
     * Add data validation listeners for all Text Fields
     */
    @FXML
    public void addListeners() {

        custName.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                nameValid = validateField(custName, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
            }
        });

        address.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                addressValid = validateField(address, newVal, "^[a-zA-Z0-9\\s.,'-]{1,100}$");
            }
        });

        phone.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                phoneValid = validateField(phone, newVal, "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*{1,12}$");
            }
        });

        code.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                code.setText(code.getText().toUpperCase());
                try {
                    switch (country.getValue()) {
                        case "U.S":
                            System.out.println("Checking US Postal Code...");
                            codeValid = validateField(code, newVal, "(^\\d{5}$)|(^\\d{9}$)|(^\\d{5}-\\d{4}$)");
                            break;
                        case "Canada":
                            System.out.println("Checking Canadian Postal Code...");
                            codeValid = validateField(code, newVal, "^(?![DFIOQUWZ])[A-Z]{1}[0-9]{1}(?![DFIOQU])[A-Z]{1}[ ]{1}[0-9]{1}(?![DFIOQU])[A-Z]{1}[0-9]{1}$");
                            break;
                        case "UK":
                            System.out.println("Checking UK Postal Code...");
                            codeValid = validateField(code, newVal, "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$");
                            break;
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    errorMessage("Data Validation", "No Country Selected. Unable to validate postal code.");
                }

            }
        });

        country.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            if(!Objects.equals(newVal, oldVal)){
                setRegionsCB();
            }
        });

    }


    /**
     * Initiate Validation check on all fields
     * @return validation status
     */
    private Boolean isAllFieldsValid() {

        countryValid = !country.getValue().isEmpty();
        divisionValid = !division.getValue().isEmpty();

        System.out.println("Country empty = " + countryValid);
        System.out.println("Division empty = " + divisionValid);


        return nameValid && addressValid && countryValid && divisionValid && codeValid && phoneValid;
    }


    // FORM SETUP

    /**
     * Set Combo Box values
     */
    @Override
    protected void setComboBoxes() {
        setCountriesCB();
    }

    /**
     * Set Countries ComboBox
     */
    private void setCountriesCB() {
        System.out.println("Setting Countries Combo Box...");
        country.setPromptText("Select a country.");

        try {
            country.getItems().addAll(getAllCountries());
        } catch (NullPointerException ex) {
            System.out.println("No Countries Found");
        }
    }

    /**
     * Set Regions Combo Box dynamically based on the country selected (see listeners)
     */
    @FXML
    private void setRegionsCB() {
        System.out.println("Setting Regions Combo Box...");

        if(division.isDisabled()) {
            division.setDisable(false);
        }
        division.getItems().clear(); // clear list before re-populating based on change
        division.setPromptText("Select a Region.");

        try {
            division.getItems().addAll(getAllDivisions(country.getValue()));
        } catch (NullPointerException ex) {
            System.out.println("No Regions Found");
        }
    }

    /**
     * Initiate save record process
     * @param actionEvent the button press event
     */
    @FXML
    private void saveRecord(ActionEvent actionEvent) {
        // Collect textfield values if all fields are valid

        if (isAllFieldsValid()) {
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
                        newCust.getAddress(),
                        newCust.getAddressCode(),
                        newCust.getPhone(),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        getDivisionID(newCust.getDivision())
                );
                addRecord(newCust, params);
                exitButton(actionEvent);
            } else {
                params = toList(newCust.getCustomerName(),
                        newCust.getAddress(),
                        newCust.getAddressCode(),
                        newCust.getPhone(),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        getDivisionID(newCust.getDivision()),
                        newCust.getId()); // Needed for WHERE param
                boolean updated = updateRecord(newCust, params);
                exitButton(actionEvent);
                if (updated) {
                    infoMessage("\nSuccessfully Updated" + "\nID: " + newCust.getId() + "\nName: " + newCust.getCustomerName());
                }
            }
        } else {
            errorMessage("Data Validation", "Input is incomplete or invalid.");
        }
    }

    /**
     * Get Insert SQL Statement
     * @return Customer insert SQL statement
     */
    public String getInsertStatement() {
        return "INSERT INTO customers VALUES (?,?,?,?,?,?,?,?,?,?)";
    }

    /**
     * Get Update SQL Statement
     * @return Customer update SQL statement
     */
    public String getUpdateStatement() {
        return "UPDATE customers SET " +
                "Customer_Name = ?," +
                "Address = ?, " +
                "Postal_Code = ?, " +
                "Phone = ?, " +
                "Last_Update = ?, " +
                "Last_Updated_By = ?, " +
                "Division_ID = ? " +
                "WHERE Customer_ID = ?";
    }

    /**
     * Gets Division ID from Division Name for SQL Customer Record Updates
     * @param divisionName - name of the division
     * @return division id from database
     */
    private static int getDivisionID(String divisionName) {
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
            printSQLException(ex);
        }
        return divId;
    }

    /**
     * Get list of all countries
     * Used to populate country combo box
     * @return list of all countries
     */
    static ObservableList<String> getAllCountries() {
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
                // add country String to Observable List
                allCountries.add(countryResult);
                i++;
                System.out.println(countryResult + " Country added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allCountries;
    }

    /**
     * Get list of all divisions
     * @param country the country name to filter by
     * @return list of all divisions to populate state/province combo box
     */
    public static ObservableList<String> getAllDivisions(String country) {
        ObservableList<String> allDivisions= FXCollections.observableArrayList();

        try {
            System.out.println("Querying Regions Database for Unique Items.");
            prepQuery("SELECT * FROM first_level_divisions JOIN countries USING (Country_ID) WHERE Country = '" + country +"' ORDER BY Division ASC");
            ResultSet rs = getResult();
            System.out.println("Retrieved Results.");
            int i = 0;
            while (rs.next()) {

                // set result to variables
                System.out.println("Setting Results to Divisions.");
                String division = rs.getString("Division");
                // add division String to Observable List
                allDivisions.add(division);
                i++;
                System.out.println(division + " Division added to Observable List. (" + i + ")");
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
        System.out.println("Retrieving Observable List.");
        return allDivisions;
    }

    // end of class
}

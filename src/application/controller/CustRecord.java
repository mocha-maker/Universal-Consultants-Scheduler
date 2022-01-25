package application.controller;

import application.model.Appointment;
import application.model.Customer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static application.util.Alerts.errorMessage;
import static application.util.Alerts.infoMessage;

import static application.util.Loc.getCurrentTimestamp;


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
                System.out.println(formTypeNew);

                division.setDisable(true);
                // generate id
                custID.setText(genId("customer"));

                break;
            case "Edit" :
                formTitle.setText("Edit Existing Customer");
                formTypeNew = false;
                System.out.println(formTypeNew);

                // populate from formCustomer
                custID.setText(String.valueOf(record.getId()));
                custName.setText(record.getCustomerName());
                country.setValue(record.getCountry());
                address.setText(record.getAddress());
                division.setValue(record.getDivision());
                code.setText(record.getPostalCode());
                phone.setText(record.getPhone());
                break;
            default:
                break;
        }

        bindSaveButton();

    }

    // FORM VALIDATION AND MANAGEMENT

    private void bindSaveButton() {
       BooleanBinding isAllValid = new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return (custName.getText().isEmpty()
                        || country.getValue().isBlank()
                        || address.getText().isEmpty()
                        || division.getValue().isBlank()
                        || code.getText().isEmpty()
                        || phone.getText().isEmpty());
            }
        };
        saveButton.disableProperty().bind(isAllValid);
    }

    @FXML
    public void addListeners() {

        custName.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (newVal != oldVal) {
                    nameValid = validateField(custName, newVal, "^[a-zA-Z0-9\\s.,'-]{1,50}$");
                }
            }
        });

        address.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (newVal != oldVal) {
                    addressValid = validateField(address, newVal, "^[a-zA-Z0-9\\s.,'-]{1,100}$");
                }
            }
        });

        phone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (newVal != oldVal) {
                    phoneValid = validateField(phone, newVal, "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*{1,12}$");
                }
            }
        });

        code.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (newVal != oldVal) {
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
                    }catch (Exception ex) {
                        errorMessage("Data Validation", "No Country Selected. Unable to validate postal code.");

                    }

                }
            }
        });


    }



    private Boolean isAllFieldsValid() {

        countryValid = !country.getValue().isEmpty();
        divisionValid = !division.getValue().isEmpty();

        System.out.println("Country empty = " + countryValid);
        System.out.println("Division empty = " + divisionValid);


        if(nameValid && addressValid && countryValid && divisionValid && codeValid && phoneValid) {
             return true;
        } else
        return false;
    }


    // FORM SETUP

    @Override
    protected void setComboBoxes() {
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
    private void saveRecord(ActionEvent actionEvent) {
        // Collect textfield values

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
                        newCust.getPhone(),
                        newCust.getAddress(),
                        newCust.getPostalCode(),
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
                        newCust.getPhone(),
                        newCust.getAddress(),
                        newCust.getPostalCode(),
                        getCurrentTimestamp(),
                        getActiveUser().getUserName(),
                        getDivisionID(newCust.getDivision()),
                        newCust.getId());
                boolean updated = updateRecord(newCust, params);
                exitButton(actionEvent);
                if (updated) {
                    infoMessage("Customer ID: " + newCust.getId() + "\nSuccessfully Updated");
                }
            }
        } else {
            errorMessage("Data Validation", "Input is incomplete or invalid.");
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

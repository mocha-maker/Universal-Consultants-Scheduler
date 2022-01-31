package application.model;

/**
 * Customers model class to create an object with similar attributes to database for easier manipulation
 */
public class Customer extends Record {

    /*  ======================
        CUSTOMER PARAMETERS
        ======================*/

    /**
     * the customer's name
     */
    private String customerName;
    /**
     * the customer's address
     */
    private String address;
    /**
     * the customer's zip or postal code
     */
    private String addressCode;
    /**
     * the customer's phone number
     */
    private String phone;
    /**
     * the customer's division i.e. province, state, or region
     */
    private String division;
    /**
     * the customer's country
     */
    private String country;

    /**
     * Customer object Constructor
     * @param id the customer's id
     * @param customerName the customer's name
     * @param phone the customer's phone
     * @param address the customer's address
     * @param addressCode the customer's postal or zip code
     * @param division the customer's division i.e. province, state, or region
     * @param country the customer's country
     */
    public Customer(int id, String customerName, String phone, String address, String addressCode, String division, String country) {
        super(id);
        setCustomerName(customerName);
        setPhone(phone);
        setAddress(address);
        setAddressCode(addressCode);
        setDivision(division);
        setCountry(country);
    }

    /*  ======================
        SETTERS
        ======================*/

    /**
     * Set customer name
     * @param customerName the customer's name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Set customer address
     * @param address the customer's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Set customer postal or zip code
     * @param addressCode the customer's postal or zip code
     */
    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    /**
     * Set customer phone number
     * @param phone the customer's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Set customer division
     * @param division customer's region division
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Set customer country
     * @param country the customer's country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /*  ======================
        GETTERS
        ======================*/

    /**
     * Get customer name
     * @return the customer's name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Get customer address
     * @return the customer's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get customer postal or zip code
     * @return the customer's postal or zip code
     */
    public String getAddressCode() {
        return addressCode;
    }

    /**
     * Get customer phone number
     * @return the customer's phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Get customer division
     * @return the customer's division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Get customer country
     * @return the customer's country
     */
    public String getCountry() {
        return country;
    }


    /**
     * Override toString() method for use in combo box in appointment form
     * @return formatted string - CustomerName (Phone#)
     */
    @Override
    public String toString() {
        return (customerName + " (" + phone + ")");
    }
}

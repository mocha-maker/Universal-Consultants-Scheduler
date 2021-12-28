package application.model;

public class Customer extends Record {
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String division;
    private String country;

    public Customer(int id, String customerName, String address, String postalCode, String phone, String division, String country) {
        super(id);
        setCustomerName(customerName);
        setAddress(address);
        setPostalCode(postalCode);
        setPhone(phone);
        setDivision(division);
        setCountry(country);
    }
    /*  ======================
        SETTERS
        ======================*/

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    /*  ======================
        GETTERS
        ======================*/

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getDivision() {
        return division;
    }

    public String getCountry() {
        return country;
    }
}

package application.model;

public class Customer extends Record{
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String division;
    private String country;

    public Customer(long custID, String customerName, String address, String postalCode, String phone, String division, String country) {
        super(custID);
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
        this.country = country;
    }
}

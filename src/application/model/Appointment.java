package application.model;

import java.time.LocalDateTime;

public class Appointment extends Record {

    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private Contact contact;
    private Customer customer;
    private int userId;

    public Appointment(int id, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, Contact contact, Customer customer, int userID) {
        super(id);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(start);
        setEnd(end);
        setContact(contact);
        setCustomer(customer);
        setUserId(userID);
    }



    /*  ======================
        SETTERS
        ======================*/

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) { this.type = type; }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /*  ======================
        GETTERS
        ======================*/

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() { return type;}

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Contact getContact() {
        return contact;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getUserId() {
        return userId;
    }

// end of class
}

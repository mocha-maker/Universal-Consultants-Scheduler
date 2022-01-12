package application.model;

import java.time.LocalDateTime;

public class Appointment extends Record {

    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private String contact;
    private int customerId;
    private int userId;

    public Appointment(int id, String title, String description, String location, String type, String start, String end, String contact, int customerID, int userID) {
        super(id);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(start);
        setEnd(end);
        setContact(contact);
        setCustomerId(customerID);
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

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getContact() {
        return contact;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getUserId() {
        return userId;
    }
}

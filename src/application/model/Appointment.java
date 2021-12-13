package application.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Appointment extends Record {

    private String title;
    private String description;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
    private long contactID;
    private long customerID;
    private long userID;

    public Appointment(long apptID, String title, String description, String location, LocalDateTime start, LocalDateTime end, long contactID, long customerID, long userID) {
        super(apptID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setStart(start);
        setEnd(end);
        setContactID(contactID);
        setCustomerID(customerID);
        setUserID(userID);
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

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setContactID(long contactID) {
        this.contactID = contactID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
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

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public long getContactID() {
        return contactID;
    }

    public long getCustomerID() {
        return customerID;
    }

    public long getUserID() {
        return userID;
    }
}

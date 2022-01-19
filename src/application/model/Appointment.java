package application.model;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Appointment extends Record {

    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private int contactId;
    private int customerId;
    private int userId;

    public Appointment(int id, String title, String description, String location, String type, Timestamp start, Timestamp end, int contactId, int customerID, int userID) {
        super(id);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(start);
        setEnd(end);
        setContactId(contactId);
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

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
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

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public int getContactId() {
        return contactId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
       return (start.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
    }

// end of class
}

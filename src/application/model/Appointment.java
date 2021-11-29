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
        this.title = title;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.contactID = contactID;
        this.customerID = customerID;
        this.userID = userID;
    }
}

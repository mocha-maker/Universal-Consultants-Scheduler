package application.model;

import java.time.LocalDateTime;

/**
 * Appointments model class to create an object with similar attributes to database for easier manipulation
 */
public class Appointment extends Record {

    /*  ======================
        APPOINTMENT PARAMETERS
        ======================*/

    /**
     * appointment title
     */
    private String title;
    /**
     * appointment description
     */
    private String description;
    /**
     * appointment location
     */
    private String location;
    /**
     * appointment type
     */
    private String type;
    /**
     * appointment start LocalDateTime
     */
    private LocalDateTime start;
    /**
     * appointment end LocalDateTime
     */
    private LocalDateTime end;
    /**
     * appointment contact
     */
    private Contact contact;
    /**
     * appointment customer
     */
    private Customer customer;
    /**
     * appointment userId
     */
    private int userId;

    /**
     * Appointment object constructor
     * @param id appointment id
     * @param title appointment title
     * @param description appointment description
     * @param location appointment location
     * @param type appointment type
     * @param start appointment start date time
     * @param end appointment end date time
     * @param contact appointment contact
     * @param customer appointment customer
     * @param userID appointment user id
     */
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

    /**
     * Set appointment title
     * @param title appointment title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set appointment description
     * @param description appointment description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set appointment location
     * @param location appointment location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set appointment type
     * @param type appointment type
     */
    public void setType(String type) { this.type = type; }

    /**
     * Set appointment start date time
     * @param start appointment start
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Set appointment end date time
     * @param end appointment end
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Set appointment contact
     * @param contact appointment contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Set appointment customer
     * @param customer appointment customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Set appointment user id
     * @param userId appointment user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /*  ======================
        GETTERS
        ======================*/

    /**
     * Get appointment title
     * @return appointment title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get appointment description
     * @return appointment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get appointment location
     * @return appointment location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get appointment type
     * @return appointment type
     */
    public String getType() { return type;}

    /**
     * Get appointment start date time
     * @return appointment start date time
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Get appointment end date time
     * @return appointment end date time
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Get appointment contact
     * @return appointment contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Get appointment customer
     * @return appointment customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Get appointment user id
     * @return appointment user id
     */
    public int getUserId() {
        return userId;
    }

// end of class
}

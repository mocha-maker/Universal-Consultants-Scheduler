package application.model;

/**
 * Contacts model class to create an object with similar attributes to database for easier manipulation
 */
public class Contact extends Record {

    /*  ======================
        CONTACT PARAMETERS
        ======================*/

    /**
     * the contact's name
     */
    private String name;
    /**
     * the contact's email
     */
    private String email;

    /**
     * Contact object constructor
     * @param id the contact's id
     * @param name the contact's name
     * @param email the contact's email
     */
    public Contact(int id, String name, String email) {
        super(id);
        setName(name);
        setEmail(email);
    }

    /*  ======================
        SETTERS
        ======================*/

    /**
     * Set contact name
     * @param name the contact's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set contact email
     * @param email the contact's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*  ======================
        GETTERS
        ======================*/

    /**
     * Get contact name
     * @return the contact's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get contact email
     * @return the contact's email
     */
    public String getEmail() {
        return email;
    }


    /**
     * Override toString() method for use in viewing in tableView and appointment form
     * @return formatted string - "ContactName (Contact@email.com)
     */
    @Override
    public String toString() {
        return name + " (" + email + ")";
    }

// end of class
}

package application.model;

/**
 * Appointments model class to create an object with similar attributes to database for easier manipulation, password is not saved in the application for security
 */
public class User extends Record {

    /* ======================
        VARIABLES
       ======================*/
    /**
     * The User's username for use in login functions
     */
    private String userName;

    /**
     * User Constructor
     * @param userID the user id
     * @param userName the user's username
     */
    public User(int userID, String userName) {
        super(userID);
        setUserName(userName);
    }

    /**
     * Set user's username
     * @param userName the user's username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get user's username
     * @return the user's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Modify toString method for use in user sidebar label
     * @return formatted string - "Username (ID#)"
     */
    @Override
    public String toString() {
        return userName + " (ID#" + id + ")";
    }

}

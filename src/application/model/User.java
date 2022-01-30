package application.model;

public class User extends Record {

    /* ======================
        VARIABLES
       ======================*/
    private String userName;

    public User(int userID, String userName) {
        super(userID);
        setUserName(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return userName + " (ID#" + id + ")";
    }

}

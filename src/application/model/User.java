package application.model;

public class User extends Record {

    /* ======================
        VARIABLES
       ======================*/
    private String userName;

    public User(long userID, String userName) {
        super(userID);
        setUserName(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }


}

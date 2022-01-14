package application.model;

public class Contact extends Record {
    private String name;
    private String email;

    public Contact(int id, String name, String email) {
        super(id);
        setName(name);
        setEmail(email);
    }

    /*  ======================
        SETTERS
        ======================*/

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*  ======================
        GETTERS
        ======================*/

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    // Override toString
    @Override
    public String toString() {
        return ( name );
    }

    public String toReportString() {
        return ( name + " (" + email + ")" );
    }
}

package application.model;


import static application.util.Alerts.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;


public abstract class Record {

    // variables
    protected int id;

    public Record(int id) {
        setID(id);
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }


    //end of class
}

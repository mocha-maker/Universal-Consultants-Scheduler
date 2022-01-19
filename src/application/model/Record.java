package application.model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static application.util.Alerts.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;


public abstract class Record<T> {

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

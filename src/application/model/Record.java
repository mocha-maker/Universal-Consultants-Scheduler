package application.model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class Record {

    // variables
    public static ResourceBundle bundle;
    public static Locale locale;
    protected long id;

    public Record(long id) {
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public void setID() {
        this.id = id;
    }

public void validateFields() {
        for (final Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object value = field.get(this);
                if (value instanceof String) {
                    if (((String) value).length() == 0) {
                        System.out.println(field.getName() + "Error. Empty String.");
                    }
                } else if (value instanceof Long) {
                    if ((Long) value == 0) {
                        System.out.println("Empty Long");
                    }
                } else if (value instanceof LocalDateTime) {
                } else {
                    System.out.println("Unreachable.");
                }
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }
    }


    public class ValidationError extends Exception {
        public ValidationError(String message) {
            super(message);
        }
    }
    //end of class
}

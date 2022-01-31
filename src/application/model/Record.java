package application.model;

/**
 * Abstract record class for a standard structure for ids of the other objects
 */
public abstract class Record {

    /**
     * The object's id
     */
    protected int id;

    /**
     * Record constructor
     * @param id - the object's id
     */
    public Record(int id) {
        setID(id);
    }

    /**
     * Get object's id
     * @return the object's id
     */
    public int getId() {
        return id;
    }

    /**
     * Set object's id
     * @param id - the object's id
     */
    public void setID(int id) {
        this.id = id;
    }
    //end of class
}

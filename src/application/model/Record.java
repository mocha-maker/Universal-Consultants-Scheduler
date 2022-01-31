package application.model;


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

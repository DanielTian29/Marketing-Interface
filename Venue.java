import java.util.ArrayList;
import java.util.List;

public class Venue {
    private int venueID;
    private boolean booked;
    private int capacity;
    private String name;
    public Venue(int venueID, String name,Boolean booked, int capacity){
        this.venueID = venueID;
        this.name = name;
        this.booked = booked;
        this.capacity = capacity;
    }

    public int getVenueID() { return venueID; }
    public String getName() { return name; }
    public boolean getBooked() { return booked; }
    public int getCapacity() { return  capacity; }
}

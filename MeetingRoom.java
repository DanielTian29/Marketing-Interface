import java.util.ArrayList;
import java.util.List;

public class MeetingRoom {
    private int meetingRoomID;
    private boolean booked;
    private int capacity;
    private String name;
    
    public MeetingRoom(int meetingRoomID, String name, Boolean booked, int capacity){
        this.meetingRoomID = meetingRoomID;
        this.name = name;
        this.booked = booked;
        this.capacity = capacity;
    }

    public int getmeetingRoomID() { return meetingRoomID; }
    public String getName() { return name; }
    public boolean getBooked() { return booked; }
    public int getCapacity() { return  capacity; }
}

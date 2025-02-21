import java.time.LocalDateTime;

public class Timeslot {
    private int timeslotId;
    private int venueId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked;


    public Timeslot(int timeslotId, int venueId, LocalDateTime startTime, LocalDateTime endTime, boolean isBooked) {
        this.timeslotId = timeslotId;
        this.venueId = venueId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    public int getTimeslotId() { return timeslotId; }
    public int getVenueId() { return venueId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isBooked() { return isBooked; }

}

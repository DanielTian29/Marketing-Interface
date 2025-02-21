import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseConnectionInterface {
    //connect to the database
    Connection connectToDatabase();

    //get all the timeslots where you can book events
    List<Timeslot> getAvailableTimeslots(String name) throws SQLException;

    //book the time slots
    boolean bookTimeslot(int timeslotId, String eventName) throws SQLException;

    //get all the booked seats for finances
    List<Seat> getBookedSeats(int eventId) throws SQLException;

    //get the cost of the event
    double getEventCost(int eventId) throws SQLException;

    //log the costs of the event
    boolean logEventCost(int eventId, double cost) throws SQLException;

    //get the amount the venue was booked for
    double getVenuePrice(int venueId) throws SQLException;

    //Need this to make the daily planner
    void getTodayEventsWithAvailableSeating() throws SQLException;

    //get all the free meeting rooms
    List<MeetingRoom> getAvailableMeetingRooms() throws SQLException;

    //reserve the meeting room
    boolean reserveMeetingRoom(int meetingRoomId);
}

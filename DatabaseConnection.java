import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    //connect to the database
    public Connection connectToDatabase() {
        String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t08";
        String user = "";
        String password = "";
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
    //get all the timeslots where you can book events
    public List<Timeslot> getAvailableTimeslots(String name) throws SQLException {
        List<Timeslot> availableTimeslots = new ArrayList<>();
        String sql = "SELECT timeslotId, venueId, startTime, endTime, isBooked " +
                "FROM Timeslot " +
                "JOIN Venues ON Venues.venue_id = Timeslot.venueId " +
                "WHERE name = ? AND isBooked = false " +
                "ORDER BY startTime ASC;";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Timeslot timeslot = new Timeslot(
                            rs.getInt("timeslotId"),
                            rs.getInt("venueId"),
                            rs.getTimestamp("startTime").toLocalDateTime(),
                            rs.getTimestamp("endTime").toLocalDateTime(),
                            rs.getBoolean("isBooked")
                    );
                    availableTimeslots.add(timeslot);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available timeslots: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return availableTimeslots;
    }
    //book the time slots
    public boolean bookTimeslot(int timeslotId, String eventName) throws SQLException {
        String updateTimeslot = "UPDATE Timeslot SET isBooked = true WHERE timeslotId = ? AND isBooked = false;";
        String insertEvent = "INSERT INTO Events (name, timeslotId) VALUES (?, ?, ?);";
        try {Connection conn = connectToDatabase();
            conn.setAutoCommit(false);
            try (PreparedStatement p = conn.prepareStatement(updateTimeslot)) {
                p.setInt(1, timeslotId);
                int affectedRows = p.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            try (PreparedStatement p = conn.prepareStatement(insertEvent)) {
                p.setString(1, eventName);
                p.setInt(2, timeslotId);
                p.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error booking timeslot: " + e.getMessage());
            return false;
        }
    }
    //get all the booked seats for finances
    public List<Seat> getBookedSeats(int eventId) throws SQLException {
        List<Seat> availableSeats = new ArrayList<>();
        String sql = "SELECT s.* FROM Seats s JOIN Events e ON s.venue_id = e.venue_id " +
                "WHERE e.event_id = ?, s.booked = TRUE ORDER BY s.row, s.seat_number ASC";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, eventId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat(
                            rs.getInt("seat_id"),
                            rs.getInt("venue_id"),
                            rs.getString("seat_number"),
                            rs.getString("row"),
                            rs.getBoolean("booked"),
                            rs.getBoolean("disabledSeating"),
                            rs.getInt("price"),
                            rs.getBoolean("restricted")
                    );
                    availableSeats.add(seat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in fetching available seats: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return availableSeats;
    }
    //get the cost of the event
    public double getEventCost(int eventId) throws SQLException {
        String sql = "SELECT cost FROM Events WHERE event_id = ?";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, eventId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("cost");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching event cost: " + e.getMessage());
            throw e;
        }
    }
    //log the costs of the event
    public boolean logEventCost(int eventId, double cost) throws SQLException {
        String sql = "INSERT INTO EventCosts (eventId, cost) VALUES (?, ?);";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, eventId);
            p.setDouble(2, cost);
            int affectedRows = p.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error logging event cost: " + e.getMessage());
            return false;
        }
    }
    //get the amount the venue was booked for
    public double getVenuePrice(int venueId) throws SQLException {
        String sql = "SELECT price FROM Venue WHERE venue_id = ?";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, venueId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching venue price: " + e.getMessage());
            throw e;
        }
    }
    //Need this to make the daily planner
    public void getTodayEventsWithAvailableSeating() throws SQLException {
        String eventsSql = "SELECT id, name, start_time, end_time FROM Venues WHERE start_time >= CURRENT_DATE AND start_time < CURRENT_DATE + INTERVAL '1' DAY";
        try (Connection conn = connectToDatabase();
             PreparedStatement eventsStmt = conn.prepareStatement(eventsSql);
             ResultSet eventsRs = eventsStmt.executeQuery()) {
            if (!eventsRs.isBeforeFirst()) {
                System.out.println("No events found for today.");
            } else {
                while (eventsRs.next()) {
                    int eventId = eventsRs.getInt("id");
                    String name = eventsRs.getString("name");
                    Time startTime = eventsRs.getTime("start_time");
                    Time endTime = eventsRs.getTime("end_time");
                    System.out.printf("\nEvent:" + name + "\nStart Time:" + startTime + "\nEnd Time: " + endTime);
                    String seatsSql = "SELECT seat_number, row, price FROM Seats WHERE venue_id = ? AND booked = false";
                    try (PreparedStatement seatsStmt = conn.prepareStatement(seatsSql)) {
                        seatsStmt.setInt(1, eventId);
                        try (ResultSet seatsRs = seatsStmt.executeQuery()) {
                            if (!seatsRs.isBeforeFirst()) {
                                System.out.println("No available seats for this event.");
                            } else {
                                System.out.println("Available Seats:");
                                while (seatsRs.next()) {
                                    String seatNumber = seatsRs.getString("seat_number");
                                    String row = seatsRs.getString("row");
                                    double price = seatsRs.getDouble("price");
                                    System.out.printf("Seat Number: " + seatNumber + ", Row: " + row + ", Price: £" + price);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database access error:");
            e.printStackTrace();
        }
    }
    //get all the free meeting rooms
    public List<MeetingRoom> getAvailableMeetingRooms() throws SQLException {
        List<MeetingRoom> meetingRoomList = new ArrayList<>();
        String sql = "SELECT * FROM MeetingRooms WHERE booked = FALSE AND start_time >= CURRENT_DATE AND start_time < CURRENT_DATE + INTERVAL '1' DAY";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    MeetingRoom meetingRoom = new MeetingRoom(
                            rs.getInt("meetingRoomID"),
                            rs.getString("name"),
                            rs.getBoolean("booked"),
                            rs.getInt("capacity")
                    );
                    meetingRoomList.add(meetingRoom);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available meeting room info: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return meetingRoomList;
    }
    //reserve the meeting room
    public boolean reserveMeetingRoom(int meetingRoomId) {
        String sql = "UPDATE MeetingRooms SET booked = TRUE WHERE meetingRoomID = ? AND booked = FALSE;";
        try (Connection conn = connectToDatabase();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, meetingRoomId);
            int affectedRows = p.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error reserving meeting room: " + e.getMessage());
            return false;
        }
    }
}
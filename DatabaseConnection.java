import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    //way to connect to the database
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

    //access the database to get all info on venues
    public List<Timeslot> getAvailableTimeslots(String name) throws SQLException {
        List<Timeslot> availableTimeslots = new ArrayList<>();
        String sql = "SELECT timeslotId, venueId, startTime, endTime, isBooked " +
                "FROM Timeslot " +
                "JOIN Venues ON Venues.venue_id = Timeslot.venueId " +
                "WHERE name = ? AND isBooked = false " +
                "ORDER BY startTime ASC;";

        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            throw e; // Rethrowing exception to handle it further up if necessary
        }

        return availableTimeslots;
    }

    public boolean bookTimeslot(int timeslotId, String eventName) {
        String updateTimeslot = "UPDATE Timeslot SET isBooked = true WHERE timeslotId = ? AND isBooked = false;";
        String insertEvent = "INSERT INTO Events (name, timeslotId) VALUES (?, ?, ?);";

        try {Connection conn = connectToDatabase();
            // Start transaction
            conn.setAutoCommit(false);

            // Update timeslot
            try (PreparedStatement pstmt = conn.prepareStatement(updateTimeslot)) {
                pstmt.setInt(1, timeslotId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback(); // Rollback if no rows are updated (timeslot already booked or does not exist)
                    return false;
                }
            }

            // Insert event
            try (PreparedStatement pstmt = conn.prepareStatement(insertEvent)) {
                pstmt.setString(1, eventName);
                pstmt.setInt(2, timeslotId);
                pstmt.executeUpdate();
            }

            // Commit transaction
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error booking timeslot: " + e.getMessage());
            return false;
        }
    }

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
            throw e;  // Rethrowing exception to handle it further up if necessary
        }
        return availableSeats;
    }

    public double getEventCost(int eventId) throws SQLException {
        String sql = "SELECT cost FROM Events WHERE event_id = ?";

        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
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

    public boolean logEventCost(int eventId, double cost) {
        String sql = "INSERT INTO EventCosts (eventId, cost) VALUES (?, ?);";
        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setDouble(2, cost);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error logging event cost: " + e.getMessage());
            return false;
        }
    }
}


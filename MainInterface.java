import java.sql.SQLException;

public interface MainInterface {
    void main() throws SQLException;

    void findFreeTimeSlots() throws SQLException;

    void bookFreeTimeSlots() throws SQLException;

    void getEventFianceData() throws SQLException;

    void logCosts() throws SQLException;

    void getDailyReport() throws SQLException;

    void getFreeMeetingRooms() throws SQLException;

    void bookMeetingRoom() throws SQLException;

    String fetchOnlineAPIReviews() throws SQLException;
}

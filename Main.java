import java.lang.constant.Constable;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
    }

    public void findFreeTimeSlots() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<Timeslot> timeslots = connection.getAvailableTimeslots("Main Hall");
        for (Timeslot timeslot : timeslots) {
            LocalDate date = timeslot.getStartTime().toLocalDate();
            System.out.println("Timeslot on " + date + " from " + timeslot.getStartTime().toLocalTime() +
                    " to " + timeslot.getEndTime().toLocalTime() + " is available.");
        }
    }

    public void bookFreeTimeSlots() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ID of the timeslot you'd like to book: \n");
        int timeslotid = scanner.nextInt();
        System.out.println("Please enter the name of the Event: \n");
        String eventName = scanner.next();
        connection.bookTimeslot(timeslotid, eventName);
    }

    public void getEventFianceData(int eventID) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<Seat> seats =  connection.getBookedSeats(eventID);
        double venuePrice = connection.getVenuePrice(eventID);
        double cost = connection.getEventCost(eventID);
        double revenue = 0;
        for (Seat seat : seats) {
            revenue += seat.getPrice();
        }
        System.out.println("Price venue has been booked for: " + venuePrice);
        System.out.println("\nTotal seats sold for this event is: " + seats.size());
        System.out.println("\nTotal revenue from seats: " + revenue);
        if (cost > 0) {
            System.out.println("\nTotal cost: " + cost);
            System.out.println("\nProfit: " + ((revenue + venuePrice) -cost));
        }else {
            System.out.println("\nTotal revenue: " + (revenue+venuePrice));
        }
    }

    public void logCosts(int eventID) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter cost of the event: \n");
        double cost = scanner.nextDouble();
        connection.logEventCost(eventID, cost);
    }

    public void getDailyReport() throws SQLException {
        System.out.println("Here's the Daily report: ");
        DatabaseConnection connection = new DatabaseConnection();
        connection.getTodayEventsWithAvailableSeating();
        List<MeetingRoom> meetingRoomList = connection.getAvailableMeetingRooms();
        System.out.println("The following meeting rooms are free: ");
        for (MeetingRoom meetingRoom : meetingRoomList) {
            System.out.println("Meeting room name: " + meetingRoom.getName()
                    + "\nMeeting room capcity: " + meetingRoom.getCapacity() +
                    "\nMeeting room ID: " + meetingRoom.getmeetingRoomID());
        }
    }

    public void bookMeetingRoom() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ID of the meeting room you'd like to book: \n");
        int meetingRoomID = scanner.nextInt();
        connection.reserveMeetingRoom(meetingRoomID);
    }

    public String fetchOnlineAPIReviews() throws SQLException {
        return null;
    }

}

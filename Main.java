import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;

public class Main implements MainInterface {
    @Override
    public void main() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select what you would like from the following " +
                "options:\n1.Get available time slots. \n2.Book time slots.\n" +
                "3.Get an events financial data. \n4.Log event cost.\n" +
                "5.Get daily report. \n6.Get free meeting rooms." +
                "\n7.Reserve a meeting room \n8.Get online reviews");
        String response = scanner.nextLine();
        while (!response.equals("stop")) {

            if (response.equals("1")) {
                findFreeTimeSlots();
            }
            if (response.equals("2")) {
                bookFreeTimeSlots();
            }
            if (response.equals("3")) {
                getEventFianceData();
            }
            if (response.equals("4")) {
                logCosts();
            }
            if (response.equals("5")) {
               getDailyReport();
            }
            if (response.equals("6")) {
                getFreeMeetingRooms();
            }
            if (response.equals("7")) {
                bookMeetingRoom();
            }
            if (response.equals("8")) {
                fetchOnlineAPIReviews();
            }
            System.out.println("Please select what you would like from the following " +
                    "options:\n1.Get available time slots. \n2.Book time slots.\n" +
                    "3.Get an events financial data. \n4.Log event cost.\n" +
                    "5.Get daily report. \n6.Get free meeting rooms." +
                    "\n7.Reserve a meeting room \n8.Get online reviews");
            response = scanner.nextLine();
        }
    }

    @Override
    public void findFreeTimeSlots() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<Timeslot> timeslots = connection.getAvailableTimeslots("Main Hall");
        for (Timeslot timeslot : timeslots) {
            LocalDate date = timeslot.getStartTime().toLocalDate();
            System.out.println("Timeslot on " + date + " from " + timeslot.getStartTime().toLocalTime() +
                    " to " + timeslot.getEndTime().toLocalTime() + " is available.");
        }
    }

    @Override
    public void bookFreeTimeSlots() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ID of the timeslot you'd like to book: \n");
        int timeslotid = scanner.nextInt();
        System.out.println("Please enter the name of the Event: \n");
        String eventName = scanner.next();
        connection.bookTimeslot(timeslotid, eventName);
    }

    @Override
    public void getEventFianceData() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        DatabaseConnection connection = new DatabaseConnection();
        System.out.println("Please enter the ID of the event you'd like to look at: \n");
        int eventID = scanner.nextInt();
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

    @Override
    public void logCosts() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter cost of the event: \n");
        double cost = scanner.nextDouble();
        System.out.println("Please enter the ID of the event you'd like to look at: \n");
        int eventID = scanner.nextInt();
        connection.logEventCost(eventID, cost);
    }

    @Override
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

    @Override
    public void getFreeMeetingRooms() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<MeetingRoom> meetingRoomList = connection.getAvailableMeetingRooms();
        System.out.println("These meeting rooms are free: ");
        for (MeetingRoom meetingRoom : meetingRoomList) {
            System.out.println("This meeting room with the id: " + meetingRoom.getmeetingRoomID() +
                    "named: " + meetingRoom.getName() + "with a capacity of: " + meetingRoom.getCapacity());
        }
    }

    @Override
    public void bookMeetingRoom() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ID of the meeting room you'd like to book: \n");
        int meetingRoomID = scanner.nextInt();
        connection.reserveMeetingRoom(meetingRoomID);
    }

    @Override
    public String fetchOnlineAPIReviews() throws SQLException {
        return null;
    }

}

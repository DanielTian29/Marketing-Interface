public class Seat {
    private int seatId;
    private int venueId;
    private int guestID;
    private String seatNumber;
    private String row;
    private Boolean booked;
    private boolean disabledSeating;
    private double price;
    private Boolean restricted;

    public Seat(int seatId, int venueId, String seatNumber, String row, Boolean booked, boolean disabledSeating, double price, boolean restricted){
        this.seatId = seatId;
        this.venueId = venueId;
        this.seatNumber = seatNumber;
        this.row = row;
        this.booked = booked;
        this.disabledSeating = disabledSeating;
        this.price = price;
        this.restricted = restricted;
    }

    public int getSeatId() { return seatId; }
    public int getVenueId() { return venueId; }
    public int getGuestID() { return guestID; }
    public String getSeatNumber() { return seatNumber; }
    public String  getRow() { return row; }
    public Boolean getBooked() { return booked; }
    public boolean getIfDisabled() { return disabledSeating; }
    public double getPrice() { return price; }
    public Boolean getRestricted() { return restricted; }
    public void setPrice(double price) { this.price = price; }
}

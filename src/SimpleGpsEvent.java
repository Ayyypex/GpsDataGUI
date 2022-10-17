/** Represents a single set of GpsCoordinates, except for the altitude. */
public class SimpleGpsEvent {

  public String name;         // The name of the GPS Tracker
  public double latitude;     // The Latitude of the GPS event as a value from -90.0 to +90.0
  public double longitude;    // The Longitude of the GPS event as a value from -180.0 to +180.0

  /** Creates a SimpleGpsEvent from a GpsEvent. */
  public SimpleGpsEvent(GpsEvent ev){
    this.name = ev.name;
    this.latitude = ev.latitude;
    this.longitude = ev.longitude;
  }
} 

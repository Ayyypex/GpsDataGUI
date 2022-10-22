/** Represents a single set of GpsCoordinates, except for the altitude. */
public class SimpleGpsEvent {

  public String name;         // The name of the GPS Tracker
  public double latitude;     // The Latitude of the GPS event as a value from -90.0 to +90.0
  public double longitude;    // The Longitude of the GPS event as a value from -180.0 to +180.0

  private boolean notSet = true;    // used to indicate that the fields have not been set

  /** Creates a SimpleGpsEvent from a GpsEvent. */
  public SimpleGpsEvent(GpsEvent ev) {
    notSet = false;
    this.name = ev.name;
    this.latitude = ev.latitude;
    this.longitude = ev.longitude;
  }

  /** Constructor does nothing so it can be used for a cell to hold. */
  public SimpleGpsEvent() {}

  /** Getter for the tracker number. */
  public String getTrackerNumber() {
    if (notSet) { return ""; }
    return String.valueOf(name.charAt(7));
  }

  /** Getter for the latitude. */
  public String getLatitude() {
    if (notSet) { return ""; }
    return String.valueOf(latitude);
  }

  /** Getter for the longitude. */
  public String getLongitude() {
    if (notSet) { return ""; }
    return String.valueOf(longitude);
  }
} 

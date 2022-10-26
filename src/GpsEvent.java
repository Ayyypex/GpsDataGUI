/** Represents a single set of GpsCoordinates. */
public class GpsEvent {

  public String name;         // The name of the GPS Tracker
  public double latitude;     // The Latitude of the GPS event as a value from -90.0 to +90.0
  public double longitude;    // The Longitude of the GPS event as a value from -180.0 to +180.0
  public double altitude;     // The Altitude of the GPS event in feet
  public Double timeAdded;

  /** Creates a GpsEvent. */
  public GpsEvent(String name, double latitude, double longitude, double altitude){
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  /** Getter for the tracker number. */
  public String getTrackerNumber() {
    return String.valueOf(name.charAt(7));
  }

  /**
   * Sets the time field for the event.
   * 
   * @param time  The time the event was added to the cell list in DistanceTravelledComponent.
   */
  public void setTime(Double time){
    this.timeAdded = time;
  }

  /** 
   * Returns a String object representing this GpsEvent's value.
   * @return a string representation of the value of this object.
   */
  public String toString(){
    return this.getTrackerNumber()+","+this.latitude+","+this.longitude+","+this.altitude;
  }

} 

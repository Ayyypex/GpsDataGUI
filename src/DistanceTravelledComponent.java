import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
import java.util.*;

/** Displays the distance travelled over the last 5 minutes for each tracker
 *  within a latitude and longitude range set by a user-configurable control panel.
 */
public class DistanceTravelledComponent extends JPanel {
  // just using this to try stuff out
  public static void main(String[] args) {
    GpsService serv = new GpsService();
    Stream<GpsEvent>[] streams = serv.getEventStreams();

    JFrame frame = new JFrame();
    frame.add( new DistanceTravelledComponent(streams) );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);        // place gui window at center of screen
    frame.setSize( new Dimension(700, 400) );
    frame.setVisible(true);
  }

  /**
   * Calculates the distance between two positions by first calculating their haversine
   * distance, before using Pythagoras to account for the altitude. The hypotenuse of 
   * the triangle will be the final distance, while the 'horizontal' side will be the 
   * haversine distance, and the 'vertical' side is the altitude difference. The positions
   * are assumed to be in signed degrees format.
   * 
   *        /|
   *       / |
   *   d  /  |
   *     /   |  altitude Difference
   *    /    |
   *   /_____|
   *  haversine
   * 
   * 
   * @param lat1  The latitude of the first position.
   * @param lon1  The longitude of the first position.
   * @param alt1  The altitude of the first position.
   * @param lat2  The latitude of the second position.
   * @param lon2  The longitude of the second position.
   * @param alt2  The altitude of the second position.
   * @return      The distance between the two positions.
   */
  public static Double calcHaversine(Double lat1, Double lon1, Double alt1,
    Double lat2, Double lon2, Double alt2 ) 
  {
    // convert degrees to radians
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);
    lon1 = Math.toRadians(lon1);
    lon2 = Math.toRadians(lon2);

    // apply formulae to get value within the square root
    Double a = Math.pow( Math.sin( (lat2-lat1)/2 ), 2 ) +
               Math.pow( Math.sin( (lon2-lon1)/2 ), 2 ) *
               Math.cos(lat1) *
               Math.cos(lat2);
      
    // apply formuale to get value within external-most bracket
    Double b = Math.asin(Math.sqrt(a));

    // radius of the sphere (Earth in this case)
    Double r = 6371.0 * 1000 ; // meters

    // final haversine distance
    Double hDist = 2*r*b;

    // convert altitude from feet to meters
    Double feetPerMeter = 3.281;
    alt1 = alt1 / feetPerMeter;
    alt2 = alt2 / feetPerMeter;

    // calculate final distance with altitude via pythagoras
    Double sqrSum = Math.pow(hDist, 2) + Math.pow(alt2-alt1, 2);
    return Math.sqrt(sqrSum);
  }

  /**
   * Calculates the distance between two positions by converting the geodetic data
   * to ECEF coordinates, which is a cartesian spatial reference system for Earth. 
   * The positions are assumed to be in signed degrees format.
   * 
   * @param lat1  The latitude of the first position.
   * @param lon1  The longitude of the first position.
   * @param alt1  The altitude of the first position in feet.
   * @param lat2  The latitude of the second position.
   * @param lon2  The longitude of the second position.
   * @param alt2  The altitude of the second position in feet.
   * @return      The distance between the two positions.
   */
  public static Double calcEuclidean(Double lat1, Double lon1, Double alt1,
    Double lat2, Double lon2, Double alt2 ) 
  {
    // convert to X, Y, Z format
    Double[] pt1 = convertToECEF(lat1, lon1, alt1);
    Double[] pt2 = convertToECEF(lat2, lon2, alt2);

    // calculate differences between points
    Double diffX = pt2[0] - pt1[0];
    Double diffY = pt2[1] - pt1[1];
    Double diffZ = pt2[2] - pt1[2];

    // calculate distance between points
    Double sqrSum = Math.pow(diffX, 2) + Math.pow(diffY, 2) + Math.pow(diffZ, 2);
    return Math.sqrt(sqrSum);
  }

  //https://en.wikipedia.org/wiki/Geographic_coordinate_conversion#From_geodetic_to_ECEF_coordinates
  /**
   * 
   * @param lat  The latitude of the position in signed degrees format. 
   * @param lon  The longitude of the position in signed degrees format. 
   * @param alt  The altitude of the position in feet.
   * @return
   */
  public static Double[] convertToECEF(Double lat, Double lon, Double alt) {
    // convert degrees to radians, and altitude to meters
    lat = Math.toRadians(lat);
    lon = Math.toRadians(lon);
    Double h = alt / 3.281;

    // define Earth's equatorial radius (semi-major axis), and polar radius (semi-minor axis)
    // source: https://en.wikipedia.org/wiki/Earth_radius
    Double a = 6378.1370 * 1000; // meters
    Double b = 6356.7523 * 1000;

    // define e squared, the ' square of the first numerical eccentricity of the ellipsoid'
    Double eSqrd = 1 - (Math.pow(b, 2) / Math.pow(a, 2));

    // define N, the prime vertical radius of curvature
    Double N = a / (Math.sqrt( 1 - eSqrd * Math.pow(Math.sin(lat), 2) ));

    // convert to ECEF's X, Y, Z  format
    Double X = (N + h) * Math.cos(lat) * Math.cos(lon);
    Double Y = (N + h) * Math.cos(lat) * Math.sin(lon);
    Double Z = ((Math.pow(b, 2) / Math.pow(a, 2)) * N + h) * Math.sin(lat);

    return new Double[] { X, Y, Z };
  }

  /**
   * Calculates the distance travelled in meters, of a list of known positions. 
   * 
   * @param events  A list of GpsEvents.
   * @return        The distance travelled by moving to each position sequentially.
   */
  public static int calcDistance(ArrayList<GpsEvent> events) {
    // need at least two positions to calculate the distance between
    if ( events.size() < 2 ) {
      return 0;
    }

    Double travelledDist = 0.0;

    // calculate distance travelled between events
    for ( int i=0; i<events.size()-1; i++ ) {
      GpsEvent ev1 = events.get(i);
      GpsEvent ev2 = events.get(i+1);

      // sum up distance
      travelledDist += calcEuclidean(ev1.latitude, ev1.longitude, ev1.altitude, 
        ev2.latitude, ev2.longitude, ev2.altitude);
    }
    
    // round UP to nearest integer meter 
    return (int) Math.ceil(travelledDist);
  }

  /** 
   * Constructs the fourth required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   */
  public DistanceTravelledComponent(Stream<GpsEvent>[] streams) {
    // set up Sodium FRP timer system and cell to hold the current time
    SecondsTimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;
    
    Stream<GpsEvent> sGpsEvent = streams[1];

    // accumulate events
    Cell<ArrayList<GpsEvent>> allEvents = sGpsEvent.accum(
      new ArrayList<GpsEvent>(), (GpsEvent ev, ArrayList<GpsEvent> list) -> {
        ev.name+=String.valueOf(cTime.sample());
        ev.setTime( cTime.sample() );
        list.add(ev);
        return list;
      }
    )
    // remove events older than 10 seconds (5 MINUTES LATER ON)
    .map( (ArrayList<GpsEvent> list) -> {
      ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
      for ( GpsEvent ev : list ) {
        if ( (cTime.sample() - ev.timeAdded) < 10 ) {
          newList.add(ev);
        }
      }
      return newList;
    });

    

    // loop() in peridioc can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every second
      Stream<Double> sTimer = myGUI.periodic(sys, 1.0);

      // create sliding window that contains the events from the last 10 seconds (5 MINUTES LATER ON)
      Stream<ArrayList<GpsEvent>> sSlidingWindow = sTimer.snapshot(
        allEvents, (Double t, ArrayList<GpsEvent>list) -> {
          ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
          for ( GpsEvent ev : list ) {
            if ( (cTime.sample() - ev.timeAdded) < 10 ) {
              newList.add(ev);
            }
          }
          return newList;
        }
      );

      // cell to show the distance travelled of the events currently in sliding window
      Cell<String> distanceTravelled = sSlidingWindow.map( (ArrayList<GpsEvent> list) -> {
        int travelledDist = calcDistance(list);
        return String.valueOf(travelledDist) + " m";
      }).hold("");

      // add to display
      SLabel label = new SLabel(distanceTravelled);
      this.add(label);
    });    
  }
}
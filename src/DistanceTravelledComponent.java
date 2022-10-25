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
   * Calculates the distance between two positions by converting the geodetic data
   * to ECEF coordinates, which is a cartesian spatial reference system for Earth. 
   * The latitude and longitude positions should be in signed degrees format.
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
    // convert to ECEF coordinates
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

  /**
   * Converts geodetic coordinates (latitude, longitude, altitude) to Earth-centered Earth-fixed coordinates.
   * 
   * Source for formulae: https://en.wikipedia.org/wiki/Geographic_coordinate_conversion#From_geodetic_to_ECEF_coordinates
   * Source for Earth's radius: https://en.wikipedia.org/wiki/Earth_radius
   * 
   * @param lat  The latitude of the position in signed degrees format. 
   * @param lon  The longitude of the position in signed degrees format. 
   * @param alt  The altitude of the position in feet.
   * @return     An array containing the converted ECEF coordinates.
   */
  public static Double[] convertToECEF(Double lat, Double lon, Double alt) {
    // convert degrees to radians, and altitude to meters
    lat = Math.toRadians(lat);
    lon = Math.toRadians(lon);
    Double h = alt / 3.281;

    // define Earth's equatorial radius (semi-major axis), and polar radius (semi-minor axis)
    Double a = 6378.1370 * 1000; // meters
    Double b = 6356.7523 * 1000;

    // define e squared, the 'square of the first numerical eccentricity of the ellipsoid'
    Double eSqrd = 1 - (Math.pow(b, 2) / Math.pow(a, 2));

    // define N, the prime vertical radius of curvature
    Double N = a / Math.sqrt( 1 - eSqrd * Math.pow(Math.sin(lat), 2) );

    // convert to ECEF coordinates
    Double X = (N + h) * Math.cos(lat) * Math.cos(lon);
    Double Y = (N + h) * Math.cos(lat) * Math.sin(lon);
    Double Z = ((1 - eSqrd) * N + h) * Math.sin(lat);

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
   * @param  streams   An array of GpsEvent streams that will be used to calculate
   *                   the distance travelled in the last 5 minutes.
   */
  public DistanceTravelledComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridBagLayout());
    this.setLayout(new GridLayout(1,2));

    // set up Sodium FRP timer system and cell to hold the current time
    SecondsTimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;

    // add control panel to main panel
    this.add(new ControlPanelComponent(null));
    

    JPanel allTrackersPanel = new JPanel(new GridLayout(5, 2, 10, 10));
    
    // create and add each tracker display
    for ( int i=0; i<streams.length; i++ ) {
      Stream<GpsEvent> sGpsEvents = streams[i];

      // create and configure display panel
      JPanel trackerPanel = new JPanel(new GridLayout(2, 2, 0, 5));
      trackerPanel.setBackground(Color.white);
      trackerPanel.setBorder(BorderFactory.createEtchedBorder());

      // accumulate events
      Cell<ArrayList<GpsEvent>> allEvents = sGpsEvents.accum(
        new ArrayList<GpsEvent>(), (GpsEvent ev, ArrayList<GpsEvent> list) -> {
          ev.setTime(cTime.sample());
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

        // cell to hold tracker name
        Cell<String> cTrackerNumber = sGpsEvents.map( (GpsEvent ev) -> ev.getTrackerNumber() ).hold("");

        // cell to show the distance travelled of the events currently in sliding window
        Cell<String> distanceTravelled = sSlidingWindow.map( (ArrayList<GpsEvent> list) -> {
          int travelledDist = calcDistance(list);
          return String.valueOf(travelledDist) + " m";
        }).hold("");

        // create SLabels
        SLabel trackerNumLabel = new SLabel(cTrackerNumber);
        SLabel distanceLabel = new SLabel(distanceTravelled);

        // configure SLabels
        Font value = new Font("Courier", Font.PLAIN, 14);
        trackerNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackerNumLabel.setVerticalAlignment(SwingConstants.TOP);
        trackerNumLabel.setFont(value);
        distanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        distanceLabel.setVerticalAlignment(SwingConstants.TOP);
        distanceLabel.setFont(value);

        // create header labels
        JLabel trackerNumHeader = new JLabel("Tracker Number:");
        JLabel distanceHeader = new JLabel("Distance Travelled:");

        // configure header labels
        Font header = new Font("Courier", Font.BOLD, 15);
        trackerNumHeader.setHorizontalAlignment(SwingConstants.CENTER);
        trackerNumHeader.setVerticalAlignment(SwingConstants.BOTTOM);
        trackerNumHeader.setFont(header);
        distanceHeader.setHorizontalAlignment(SwingConstants.CENTER);
        distanceHeader.setVerticalAlignment(SwingConstants.BOTTOM);
        distanceHeader.setFont(header);

        // add labels to panel
        trackerPanel.add(trackerNumHeader);
        trackerPanel.add(distanceHeader);
        trackerPanel.add(trackerNumLabel);
        trackerPanel.add(distanceLabel);
      }); 
      
      // add to allTrackersPanel
      allTrackersPanel.add(trackerPanel);
    }

    // add to main panel
    this.add(allTrackersPanel);
  }
}
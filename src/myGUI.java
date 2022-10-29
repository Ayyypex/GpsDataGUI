import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** 
 * My GUI which displays transformed tracker data retrieved from a stream
 * using Sodium FRP operations. 
 */
public class myGUI extends JFrame {

  /** Creates an instance of myGUI and then shows it. */
  public static void main(String[] args) {
    myGUI GUI = new myGUI();
    GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GUI.setLocationRelativeTo(null);
    GUI.setVisible(true);
  }

  /** Constructs my GUI. */
  public myGUI() {
    // mandatory configuration
    this.setTitle("a1765159's GUI");
    this.setBackground(Color.gray);
    this.setSize(1300, 940);

    // create main panel
    JPanel mainPanel = new JPanel(new GridBagLayout());

    // create panel for the trackers
    JPanel trackersTablePanel = new JPanel(new GridBagLayout());
    trackersTablePanel.setBorder(BorderFactory.createEtchedBorder());

    // create header labels
    JLabel trackerNumHeader = new JLabel("Tracker Number", SwingConstants.CENTER);
    JLabel trackerLatHeader = new JLabel("Latitude", SwingConstants.CENTER);
    JLabel trackerLonHeader = new JLabel("Longitude", SwingConstants.CENTER);
    JLabel trackerDistDeader = new JLabel("Distance Travelled within Range (meters)", SwingConstants.CENTER);

    // define insets and font to be used throughout GUI
    Insets columnInsets = new Insets(10, 50, 10, 50);
    Insets minInsets = new Insets(5, 5, 5, 5);
    Font valueFont = new Font("Courier", Font.PLAIN, 14);

    // add table headers
    addComponent(trackersTablePanel, trackerNumHeader, 0, 0, 1, 1, columnInsets);
    addComponent(trackersTablePanel, trackerLatHeader, 0, 1, 1, 1, columnInsets);
    addComponent(trackersTablePanel, trackerLonHeader, 0, 2, 1, 1, columnInsets);
    addComponent(trackersTablePanel, trackerDistDeader, 0, 4, 1, 1, columnInsets);

    // add table header separator
    JSeparator headerSep = new JSeparator(SwingConstants.HORIZONTAL);
    headerSep.setForeground(Color.black);
    headerSep.setBackground(Color.black);
    addComponent(trackersTablePanel, headerSep, 1, 0, 5, 1, minInsets);

    // add vertical separator to indicate difference between the distance column
    JSeparator distanceSep = new JSeparator(SwingConstants.VERTICAL);
    distanceSep.setForeground(Color.black);
    distanceSep.setBackground(Color.black);
    addComponent(trackersTablePanel, distanceSep, 0, 3, 1, 24, minInsets);

    // create Sodium FRP timer system and control panel
    SecondsTimerSystem sys = new SecondsTimerSystem();
    ControlPanel controlPanel = new ControlPanel(null);
    
    // get the event streams
    GpsService serv = new GpsService();
    Stream<GpsEvent>[] streams = serv.getEventStreams();    

    int rowIndex = 2;   // row to put the tracker info in, because separator takes a row

    // fill row for each stream/tracker    
    for ( int i=0; i<streams.length; i++ ) {
      // get tracker's stream
      Stream<GpsEvent> sGpsEvents = streams[i];

      // get row values
      Cell<String>[] simplifiedGpsCells =  getSimplifiedGpsCells(sGpsEvents);
      Cell<String> cDistTravelled = getDistTravelledCell(sys, controlPanel, sGpsEvents);

      // create SLabels
      SLabel trackerNumLabel = new SLabel(simplifiedGpsCells[0]);
      SLabel trackerLatLabel = new SLabel(simplifiedGpsCells[1]);
      SLabel trackerLonLabel = new SLabel(simplifiedGpsCells[2]);
      SLabel trackerDistLabel = new SLabel(cDistTravelled);
      
      // configure SLabels
      trackerNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerDistLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerNumLabel.setFont(valueFont);
      trackerLatLabel.setFont(valueFont);
      trackerLonLabel.setFont(valueFont);
      trackerDistLabel.setFont(valueFont);

      // add SLabels to table row
      addComponent( trackersTablePanel, trackerNumLabel, rowIndex, 0, 1, 1, columnInsets );
      addComponent( trackersTablePanel, trackerLatLabel, rowIndex, 1, 1, 1, columnInsets );
      addComponent( trackersTablePanel, trackerLonLabel, rowIndex, 2, 1, 1, columnInsets );
      addComponent( trackersTablePanel, trackerDistLabel, rowIndex, 4, 1, 1, columnInsets );

      // add separator below each row
      JSeparator rowSep = new JSeparator(SwingConstants.HORIZONTAL);
      rowSep.setForeground(Color.lightGray);
      rowSep.setBackground(Color.lightGray);
      addComponent( trackersTablePanel, rowSep, rowIndex+1, 0, 5, 1, minInsets );
      rowIndex += 2;     
    }

    // add seperator at bottom of table
    JSeparator rowSep = new JSeparator(SwingConstants.HORIZONTAL);
    rowSep.setForeground(Color.black);
    rowSep.setBackground(Color.black);
    addComponent( trackersTablePanel, rowSep, rowIndex+1, 0, 5, 1, minInsets );

    // create panel to hold the event displays
    JPanel eventsPanel = new JPanel(new GridLayout(2, 1, 10, 10));

    // create panel to display each event as it is passed to the GUI 
    JPanel allEventsPanel = new JPanel(new GridBagLayout());
    allEventsPanel.setBorder(BorderFactory.createEtchedBorder());
    Cell<String> cAllEvents = getAllEventsCell(sys, streams);

    // create and configure labels
    JLabel recentEventHeader = new JLabel("Most Recent Event:", SwingConstants.CENTER);
    SLabel allEventsLabel = new SLabel(cAllEvents);
    allEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    allEventsLabel.setFont(valueFont);

    // add labels to allEventsPanel
    addComponent( allEventsPanel, recentEventHeader, 0, 0, 1, 1, minInsets );
    addComponent( allEventsPanel, allEventsLabel, 1, 0, 1, 1, minInsets );

    // create panel to display each filtered event as it is passed to the GUI
    JPanel filteredEventsPanel = new JPanel(new GridBagLayout());
    filteredEventsPanel.setBorder(BorderFactory.createEtchedBorder());
    Cell<String> cFilteredEvents = getFilteredEventsCell(sys, controlPanel, streams);

    // create and configure labels
    JLabel filteredEventsHeader = new JLabel("Most Recent Filtered Event:", SwingConstants.CENTER);
    SLabel filteredEventsLabel = new SLabel(cFilteredEvents);
    filteredEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    filteredEventsLabel.setFont(valueFont);

    // add labels to filteredEventsPanel
    addComponent( filteredEventsPanel, filteredEventsHeader, 0, 0, 1, 1, minInsets );
    addComponent( filteredEventsPanel, filteredEventsLabel, 1, 0, 1, 1, minInsets );

    // add event subpanels to eventsPanel    
    addComponent(eventsPanel, allEventsPanel, 0, 1, 1, 1, minInsets);
    addComponent(eventsPanel, filteredEventsPanel, 1, 1, 1, 1, minInsets);

    // add all subpanels to main panel
    addComponent(mainPanel, trackersTablePanel, 0, 0, 2, 1, minInsets);
    addComponent(mainPanel, controlPanel, 1, 0, 1, 2, minInsets);
    addComponent(mainPanel, eventsPanel, 2, 1, 1, 2, minInsets);
    
    // add main panel to frame
    this.add(mainPanel);
  }

  /**
   * Adds a component to a JPanel with the specified GridBag contraints.
   * 
   * @param panel       The JPanel the component is being added to.
   * @param component   The component to be added.
   * @param row         The row to be added to.
   * @param col         The column to be added to.
   * @param width       The number of columns the component will occupy. 
   * @param height      The number of rows the component will occupy.
   * @param insets      The spacing of the component's edges.
   */
  public static void addComponent(JPanel panel, Component component,
    int row, int col, int width, int height, Insets insets) 
  {
    GridBagConstraints c = new GridBagConstraints();
    c.insets = insets;
    c.fill = GridBagConstraints.BOTH;
    c.gridx=col;  
    c.gridy=row;
    c.gridwidth=width;
    c.gridheight=height;
    panel.add(component, c);
  }

  /**
   * Uses Sodium FRP operations on a GpsEvent stream to get simplified tracking data. 
   * 
   * @param sGpsEvents  A stream of GpsEvents.
   * @return            A string cell array containing cells that hold the tracker number, the
   *                    tracker latitude, and the tracker longitude.
   */
  public static Cell<String>[] getSimplifiedGpsCells(Stream<GpsEvent> sGpsEvents) {
    // create cell to hold the stripped stream's SimpleGpsEvent
    Cell<SimpleGpsEvent> cEvent = stripAltitude(sGpsEvents)
      .hold( new SimpleGpsEvent() );

    // create cells to hold each field
    Cell<String> cTrackerNumber = cEvent.map( (SimpleGpsEvent ev) -> ev.getTrackerNumber() );
    Cell<String> cTrackerLatitude = cEvent.map( (SimpleGpsEvent ev) -> ev.getLatitude() );
    Cell<String> cTrackerLongitude = cEvent.map( (SimpleGpsEvent ev) -> ev.getLongitude() );

    // create linked list, so we can convert it to array
    LinkedList<Cell<String>> cells = new LinkedList<Cell<String>>();
    cells.add(cTrackerNumber);
    cells.add(cTrackerLatitude);
    cells.add(cTrackerLongitude);

    // return the linked list in array form
    return (Cell<String>[])cells.toArray(new Cell[0]);
  }

  /**
   * Uses Sodium FRP operations on an array of GpsEvent streams to get a cell that holds
   * all stream events.
   * 
   * @param sys      A Sodium FRP SecondsTimerSystem used to create a periodic timer. 
   * @param streams  An array of GpsEvent streams.
   * @return         A cell that holds each event that is passed to the streams in String form.
   */
  public static Cell<String> getAllEventsCell(SecondsTimerSystem sys, Stream<GpsEvent>[] streams) {
    // create cell to return
    CellLoop<String> cAllEvents = Transaction.run(() -> {

      // create cell to hold the timer system's current time
      Cell<Double> cTime = sys.time;

      // merge streams
      Stream<GpsEvent> sAllGpsEvents = myGUI.mergeStreams(streams);

      // record system time of last event occurrence
      Cell<Double> cLastEventTime = sAllGpsEvents.map( (GpsEvent ev) -> cTime.sample() )
        .hold(0.0);

      // create stream that will fire an event every 0.1 seconds
      Stream<Double> sTimer = myGUI.periodic(sys, 0.1);

      // create CellLoop for eventString so we can check if its empty
      CellLoop<String> cEventString = new CellLoop<>();

      // create stream that fires "" after 3 seconds since last event if cell is not ""
      Stream<String> sClear = sTimer.filter( (Double t) -> 
        ((cTime.sample() - cLastEventTime.sample()) > 3) && (cEventString.sample() != "") )
          .map((Double t) -> "");

      // set up cell to hold the event as a string
      cEventString.loop(
        sAllGpsEvents.map( (GpsEvent ev) -> ev.toString() )
          .orElse(sClear)
            .hold("") );

      return cEventString;
    });

    return cAllEvents;
  }

  /**
   * Uses Sodium FRP operations on an array of GpsEvent streams to get a cell that holds
   * all stream events that are within the control panel's current latitude and longitude
   * restrictions. 
   * 
   * @param sys      A Sodium FRP SecondsTimerSystem used to create a periodic timer.
   * @param ctrlPnl  The ControlPanel that sets and contains the current restrictions.
   * @param streams  An array of GpsEvent streams.
   * @return         A cell that holds each event that is passed to the streams in String form.
   */
  public static Cell<String> getFilteredEventsCell(SecondsTimerSystem sys, ControlPanel ctrlPnl,
    Stream<GpsEvent>[] streams)
  {
    // create cell to return
    CellLoop<String> cFilteredEvents = Transaction.run(() -> {

      // create cell to hold the timer system's current time
      Cell<Double> cTime = sys.time;

      // merge streams and create cells to hold the current latitude and longitude restrictions
      Stream<GpsEvent> sAllGpsEvents = myGUI.mergeStreams(streams);
      Cell<Double> cLatMin = ctrlPnl.cLatMin.map( (String latMin) -> Double.parseDouble(latMin) );
      Cell<Double> cLatMax = ctrlPnl.cLatMax.map( (String latMax) -> Double.parseDouble(latMax) );
      Cell<Double> cLonMin = ctrlPnl.cLonMin.map( (String lonMin) -> Double.parseDouble(lonMin) );
      Cell<Double> cLonMax = ctrlPnl.cLonMax.map( (String lonMax) -> Double.parseDouble(lonMax) ); 
    
      // create stream that will fire an event every 0.1 seconds
      Stream<Double> sTimer = myGUI.periodic(sys, 0.1);

      // create CellLoop for eventString so we can check if its empty
      CellLoop<String> cEventString = new CellLoop<>();

      // filter the merged stream such that it outputs events within restrictions
      Stream<GpsEvent> sFilteredEvents = sAllGpsEvents
        .filter( (GpsEvent ev) -> ev.latitude >= cLatMin.sample() )
        .filter( (GpsEvent ev) -> ev.latitude <= cLatMax.sample() )  
        .filter( (GpsEvent ev) -> ev.longitude >= cLonMin.sample() ) 
        .filter( (GpsEvent ev) -> ev.longitude <= cLonMax.sample() );

      // record system time of last filtered event occurrence
      Cell<Double> cLastEventTime = sFilteredEvents.map( (GpsEvent ev) -> cTime.sample() )
        .hold(0.0);

      // create stream that fires "" after 3 seconds since last event if cell is not ""
      Stream<String> sClear = sTimer.filter( (Double t) -> 
        ((cTime.sample() - cLastEventTime.sample()) > 3) && (cEventString.sample() != "") )
          .map((Double t) -> "");

      // set up cell to hold the event as a string
      cEventString.loop(
        sFilteredEvents.map( (GpsEvent ev) -> ev.toString() )
          .orElse(sClear)
            .hold("") );

      return cEventString;
    });

    return cFilteredEvents;
  }

  /**
   * Uses Sodium FRP operations on a GpsEvent stream to get a cell that holds the distance
   * travelled between events that are within the control panel's current latitude and
   * longitude restrictions, and within the last 5 minutes.
   * 
   * @param sys         A Sodium FRP SecondsTimerSystem used to create a periodic timer.
   * @param ctrlPnl     The ControlPanel that sets and contains the current restrictions.
   * @param sGpsEvents  A stream of GpsEvents.
   * @return            A cell that holds the distance travelled between the GpsEvents
   *                    that are both within the last 5 minutes and the control panel's
   *                    latitude and longitude restrictions.
   */
  public static Cell<String> getDistTravelledCell(SecondsTimerSystem sys, ControlPanel ctrlPnl,
    Stream<GpsEvent> sGpsEvents)
  {
    // get sliding window
    Stream<ArrayList<GpsEvent>> sSlidingWindow = getSlidingWindow(sys, ctrlPnl, sGpsEvents, 5*60);

    // create cell to hold the distance travelled between sliding window's events
    Cell<String> cDistTravelled = sSlidingWindow.map( (ArrayList<GpsEvent> list) -> {
      int distTravelled = calcDistance(list);
      return String.valueOf(distTravelled) + " m";
    }).hold("");

    return cDistTravelled;
  }


//////////////////////////////////-- HELPER FUNCTIONS --\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  /**
   * Strips a GpsEvent stream of its altitude by mapping the GpsEvents to
   * SimpleGpsEvents.
   * 
   * @param stream  A stream of GpsEvents.
   * @return        A stream of SimpleGpsEvents.
   */
  public static Stream<SimpleGpsEvent> stripAltitude(Stream<GpsEvent> stream) {
    return stream.map( (GpsEvent ev) -> new SimpleGpsEvent(ev) );
  }

  /**
   * Returns a stream that fires an event at specified intervals.
   * 
   * @param sys     A Sodium FRP SecondsTimerSystem.
   * @param period  The specified interval that the stream will fire.
   * @return        A timer in the form of a stream that will repeatedly fire periodically.
   */
  public static Stream<Double> periodic(SecondsTimerSystem sys, Double period) {
    Cell<Double> time = sys.time;
    CellLoop<Optional<Double>> oAlarm = new CellLoop<>();
    Stream<Double> sAlarm = sys.at(oAlarm);
    oAlarm.loop(
      sAlarm.map( (Double t) -> Optional.of(t + period) )
        .hold(Optional.<Double>of(time.sample() + period)));
    return sAlarm;
  }

  /**
   * Merges an array of GpsEvent streams.
   * 
   * @param streams An array of GpsEvent streams.
   * @return        A merged stream of all the GpsEvent streams.
  */
  public static Stream<GpsEvent> mergeStreams(Stream<GpsEvent>[] streams) {    
    return Stream.orElse(Arrays.asList(streams));
  }

 
  /**
   * Uses Sodium FRP operations to create a GpsEvent stream containing events within the
   * specified window size, and within the control panel's restrictions.
   * 
   * @param sys         A Sodium FRP SecondsTimerSystem used to create a periodic timer.
   * @param ctrlPnl     The ControlPanel that sets and contains the current restrictions.
   * @param sGpsEvents  A stream of GpsEvents.
   * @param windowSize  The number of seconds to check events for. 
   * @return            A Stream of ArrayList<GpsEvent> events containing only GpsEvents
   *                    that are both within the last 5 minutes and the control panel's
   *                    latitude and longitude restrictions.
   */
  public static Stream<ArrayList<GpsEvent>> getSlidingWindow(SecondsTimerSystem sys,
    ControlPanel ctrlPnl, Stream<GpsEvent> sGpsEvents, int windowSize)
  {
    // create stream to return
    Stream<ArrayList<GpsEvent>> sSlidingWindow = Transaction.run(() -> {

      // create cell to hold the timer system's current time
      Cell<Double> cTime = sys.time;

      // get the control panel's current latitude and longitude restrictions 
      Cell<Double> cLatMin = ctrlPnl.cLatMin.map( (String latMin) -> Double.parseDouble(latMin) );
      Cell<Double> cLatMax = ctrlPnl.cLatMax.map( (String latMax) -> Double.parseDouble(latMax) );
      Cell<Double> cLonMin = ctrlPnl.cLonMin.map( (String lonMin) -> Double.parseDouble(lonMin) );
      Cell<Double> cLonMax = ctrlPnl.cLonMax.map( (String lonMax) -> Double.parseDouble(lonMax) ); 

      // create cell to accumulate GpsEvents in a list
      Cell<ArrayList<GpsEvent>> cEventList = sGpsEvents.accum(
        new ArrayList<GpsEvent>(), (GpsEvent ev, ArrayList<GpsEvent> list) -> {
          ev.setTime(cTime.sample());
          list.add(ev);
          return list;
        }
      )
      // remove events that are older than the specified windowSize 
      .map( (ArrayList<GpsEvent> list) -> {
        ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
        for ( GpsEvent ev : list ) {
          // event won't be ever considered again
          if ( (cTime.sample() - ev.timeAdded) > windowSize ) {
            continue;
          }
          newList.add(ev);          
        }
        return newList;
      });

      // create stream that will fire an event every second.
      // used to remove events older than the specified windowSize
      Stream<Double> sTimer = myGUI.periodic(sys, 1.0);

      // create sliding window that contains the events within the windowSize
      Stream<ArrayList<GpsEvent>> sFilteredEvents = sTimer.snapshot(
        cEventList, (Double t, ArrayList<GpsEvent>list) -> {
          ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
          
          // check event is within sliding window and restrictions
          for ( GpsEvent ev : list ) {
            boolean notWithinWindow = (cTime.sample() - ev.timeAdded) > windowSize;
            boolean notWithinLatRange = ev.latitude < cLatMin.sample() || ev.latitude > cLatMax.sample();
            boolean notWithinLonRange = ev.longitude < cLonMin.sample() || ev.longitude > cLonMax.sample();

            // a condition wasn't met, check next event
            if ( notWithinWindow || notWithinLatRange || notWithinLonRange ) {
              continue;
            }
            newList.add(ev);
          }

          return newList;
        }
      );

      return sFilteredEvents;
    });

    return sSlidingWindow;
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

    Double distTravelled = 0.0;

    // calculate distance travelled between events
    for ( int i=0; i<events.size()-1; i++ ) {
      GpsEvent ev1 = events.get(i);
      GpsEvent ev2 = events.get(i+1);

      // sum up distance
      distTravelled += calcEuclidean(ev1.latitude, ev1.longitude, ev1.altitude, 
        ev2.latitude, ev2.longitude, ev2.altitude);
    }    
    
    // round UP to nearest integer meter 
    return (int) Math.ceil(distTravelled);
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
}
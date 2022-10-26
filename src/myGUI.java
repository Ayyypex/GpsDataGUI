import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
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
    this.setSize(1140, 840);

    // construct main main panel with 2 rows and 1 column
    JPanel mainPanel = new JPanel(new GridBagLayout());

    // construct table panel for the trackers, it will occupy the first row, and 2 columns
    JPanel trackersTablePanel = new JPanel(new GridBagLayout());
    trackersTablePanel.setBorder(BorderFactory.createEtchedBorder());

    // create header labels
    JLabel trackerNumHeader = new JLabel("Tracker Number");
    JLabel trackerLatHeader = new JLabel("Latitude");
    JLabel trackerLonHeader = new JLabel("Longitude");
    JLabel trackerDistDeader = new JLabel("Distance Travelled (meters)");
    
    // configure header labels
    trackerNumHeader.setHorizontalAlignment(SwingConstants.CENTER);
    trackerLatHeader.setHorizontalAlignment(SwingConstants.CENTER);
    trackerLonHeader.setHorizontalAlignment(SwingConstants.CENTER);
    trackerDistDeader.setHorizontalAlignment(SwingConstants.CENTER);

    // define insets to be used throughout table
    Insets tableRowInsets = new Insets(10, 50, 10, 50);
    Insets minInsets = new Insets(2, 2, 2, 2);

    // add table headers
    addComponent(trackersTablePanel, trackerNumHeader, 0, 0, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerLatHeader, 0, 1, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerLonHeader, 0, 2, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerDistDeader, 0, 4, 1, 1, tableRowInsets);

    // add table header separator
    JSeparator headerSep = new JSeparator(SwingConstants.HORIZONTAL);
    headerSep.setForeground(Color.black);
    headerSep.setBackground(Color.black);
    addComponent(trackersTablePanel, headerSep, 1, 0, 5, 1, minInsets);

    // add vertical separator to indicate difference between the distance column
    JSeparator distanceSep = new JSeparator(SwingConstants.VERTICAL);
    distanceSep.setForeground(Color.black);
    distanceSep.setBackground(Color.black);
    addComponent(trackersTablePanel, distanceSep, 0, 3, 1, 22, minInsets);

    // set up Sodium FRP timer system and cell to hold the current time
    SecondsTimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;
    
    // get the tracker's event streams
    GpsService serv = new GpsService();
    Stream<GpsEvent>[] streams = serv.getEventStreams();

    // construct control panel
    ControlPanelComponent controlPanel = new ControlPanelComponent(null);

    int rowIndex = 2;   // row to put the tracker info in, because separator takes a row

    // fill row for each stream/tracker    
    for ( int i=0; i<streams.length; i++ ) {
      // get stream of GpsEvents for the tracker
      Stream<GpsEvent> sGpsEvents = streams[i];

      // get simplified tracker info
      Cell<String>[] simplifiedGpsCells =  getSimplifiedGpsCells(sGpsEvents);

      // get distance travelled in last 5 minutes within control panel's restrictions
      //Cell<String> cDistTravelled = getDistTravelledCell(sys, cTime, controlPanel, sGpsEvents);

      // create SLabels
      SLabel trackerNumLabel = new SLabel(simplifiedGpsCells[0]);
      SLabel trackerLatLabel = new SLabel(simplifiedGpsCells[1]);
      SLabel trackerLonLabel = new SLabel(simplifiedGpsCells[2]);
      JLabel trackerDistLabel = new JLabel("10"); // SLabel trackerDistLabel = new SLabel(cDistanceTravelled);
      
      // configure SLabels
      trackerNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerDistLabel.setHorizontalAlignment(SwingConstants.CENTER);
      Font tableValueFont = new Font("Courier", Font.PLAIN, 14);
      trackerNumLabel.setFont(tableValueFont);
      trackerLatLabel.setFont(tableValueFont);
      trackerLonLabel.setFont(tableValueFont);
      trackerDistLabel.setFont(tableValueFont);

      // add SLabels to table
      addComponent( trackersTablePanel, trackerNumLabel, rowIndex, 0, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, trackerLatLabel, rowIndex, 1, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, trackerLonLabel, rowIndex, 2, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, trackerDistLabel, rowIndex, 4, 1, 1, tableRowInsets );

      // add separator for each row
      JSeparator rowSep = new JSeparator(SwingConstants.HORIZONTAL);
      addComponent( trackersTablePanel, rowSep, rowIndex-1, 0, 5, 1, minInsets );
      rowIndex+=2;

      // add two seperators at bottom of table
      if ( i == streams.length-1 ) {
        rowSep = new JSeparator(SwingConstants.HORIZONTAL);
        addComponent( trackersTablePanel, rowSep, rowIndex, 0, 5, 1, minInsets );
        rowSep = new JSeparator(SwingConstants.HORIZONTAL);
        rowSep.setForeground(Color.black);
        rowSep.setBackground(Color.black);
        addComponent( trackersTablePanel, rowSep, rowIndex+1, 0, 5, 1, minInsets );
      }
    }

    // construct panel for the allEvents and filteredEvents components
    JPanel eventsPanel = new JPanel(new GridLayout(2,1));

    // set up allEvents Panel 
    JPanel allEventsPanel = new JPanel(new GridBagLayout());
    allEventsPanel.setBorder(BorderFactory.createEtchedBorder());
    Cell<String> cAllEvents = getAllEventsCell(sys, cTime, streams);

    // create and configure header label
    JLabel recentEventHeader = new JLabel("Most Recent Event:");
    recentEventHeader.setHorizontalAlignment(SwingConstants.CENTER);

    // create and configure SLabel
    SLabel allEventsLabel = new SLabel(cAllEvents);
    allEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    allEventsLabel.setFont(new Font("Courier", Font.PLAIN, 14));

    // add labels to allEventsPanel
    addComponent( allEventsPanel, recentEventHeader, 0, 0, 1, 1, minInsets );
    addComponent( allEventsPanel, allEventsLabel, 1, 0, 1, 1, minInsets );

    // set up filteredEvents Panel
    JPanel filteredEventsPanel = new JPanel(new GridBagLayout());
    filteredEventsPanel.setBorder(BorderFactory.createEtchedBorder());
    Cell<String> cFilteredEvents = getFilteredEventsCell(sys, cTime, controlPanel, streams);

    // create and configure header label
    JLabel filteredEventsHeader = new JLabel("Most Recent Filtered Event:");
    filteredEventsHeader.setHorizontalAlignment(SwingConstants.CENTER);

    // create and configure SLabel
    SLabel filteredEventsLabel = new SLabel(cFilteredEvents);
    filteredEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    filteredEventsLabel.setFont(new Font("Courier", Font.PLAIN, 14));

    addComponent( filteredEventsPanel, filteredEventsHeader, 0, 0, 1, 1, minInsets );
    addComponent( filteredEventsPanel, filteredEventsLabel, 1, 0, 1, 1, minInsets );

    // add to events panel    
    addComponent(eventsPanel, allEventsPanel, 0, 1, 1, 1, minInsets);
    addComponent(eventsPanel, filteredEventsPanel, 1, 1, 1, 1, minInsets);

    // add subpanels to main panel
    addComponent(mainPanel, trackersTablePanel, 0, 0, 2, 1, minInsets);
    addComponent(mainPanel, controlPanel, 1, 0, 1, 2, minInsets);
    addComponent(mainPanel, eventsPanel, 2, 1, 1, 2, minInsets);
    
    // add main panel to frame    
    this.add(mainPanel);
  }

  /**
   * Adds a component to a JPanel with the specified GridBag contraints.
   * @param panel       The JPanel the component is being added to.
   * @param component   The component to be added.
   * @param row         The row to be added to.
   * @param col         The column to be added to.
   * @param width       The number of columns the component will occupy. 
   * @param height      The number of rows the component will occupy. 
   */
  private void addComponent(JPanel panel, Component component,
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
   * Strips a GpsEvent stream of its altitude. 
   * 
   * @param   stream    A stream of GpsEvents
   * @return  A stream of SimpleGpsEvents
   */
  public static Stream<SimpleGpsEvent> stripAltitude(Stream<GpsEvent> stream) {
    return stream.map( (GpsEvent ev) -> new SimpleGpsEvent(ev) );
  }

  /**
   * 
   * @param sGpsEvents
   * @return
   */
  public static Cell<String>[] getSimplifiedGpsCells(Stream<GpsEvent> sGpsEvents) {
    // set up cell to hold the stripped stream's SimpleGpsEvent
    Cell<SimpleGpsEvent> cEvent = stripAltitude(sGpsEvents)
      .hold( new SimpleGpsEvent() );

    // set up cells to hold each field
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
   * 
   * @param sys
   * @param cTime
   * @param streams
   * @return
   */
  public static Cell<String> getAllEventsCell(SecondsTimerSystem sys, Cell<Double> cTime, Stream<GpsEvent>[] streams) {
    // merge streams
    Stream<GpsEvent> sAllGpsEvents = myGUI.mergeStreams(streams);

    // record system time of last event occurrence
    Cell<Double> cLastEventTime = sAllGpsEvents.map( (GpsEvent ev) -> cTime.sample() )
      .hold(0.0);

    // create cell to return
    CellLoop<String> cAllEvents = Transaction.run(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Double> sTimer = myGUI.periodic(sys, 0.1);

      // set up CellLoop for eventString so we can forward reference it
      CellLoop<String> cEventString = new CellLoop<>();

      // create stream that fires empty string after 3 seconds since last event, & cell is not ""
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
   * 
   * @param sys
   * @param cTime
   * @param ctrlPnl
   * @param streams
   * @return
   */
  public static Cell<String> getFilteredEventsCell(SecondsTimerSystem sys, Cell<Double> cTime,
    ControlPanelComponent ctrlPnl, Stream<GpsEvent>[] streams)
  {
    Stream<GpsEvent> sAllGpsEvents = myGUI.mergeStreams(streams);
    Cell<Double> cLatMin = ctrlPnl.cLatMin.map( (String latMin) -> Double.parseDouble(latMin) );
    Cell<Double> cLatMax = ctrlPnl.cLatMax.map( (String latMax) -> Double.parseDouble(latMax) );
    Cell<Double> cLonMin = ctrlPnl.cLonMin.map( (String lonMin) -> Double.parseDouble(lonMin) );
    Cell<Double> cLonMax = ctrlPnl.cLonMax.map( (String lonMax) -> Double.parseDouble(lonMax) ); 

    // create cell to return
    CellLoop<String> cFilteredEvents = Transaction.run(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Double> sTimer = myGUI.periodic(sys, 0.1);

      // set up CellLoop for eventString so we can forward reference it
      CellLoop<String> cEventString = new CellLoop<>();

      // filter the merged stream such that it outputs events within specified range
      Stream<GpsEvent> sEventsInRange = sAllGpsEvents.filter( (GpsEvent ev) -> ev.latitude >= cLatMin.sample() )
        .filter( (GpsEvent ev) -> ev.latitude <= cLatMax.sample() )  
          .filter(  (GpsEvent ev) -> ev.longitude >= cLonMin.sample() ) 
            .filter( (GpsEvent ev) -> ev.longitude <= cLonMax.sample() );

      // record system time of last filtered event occurrence
      Cell<Double> cLastEventTime = sEventsInRange.map( (GpsEvent ev) -> cTime.sample() )
        .hold(0.0);

      // create stream that fires empty string after 3 seconds since last event, & cell is not ""
      Stream<String> sClear = sTimer.filter( (Double t) -> 
        ((cTime.sample() - cLastEventTime.sample()) > 3) && (cEventString.sample() != "") )
          .map((Double t) -> "");

      // set up cell to hold the event as a string
      cEventString.loop(
        sEventsInRange.map( (GpsEvent ev) -> ev.toString() )
          .orElse(sClear)
            .hold("") );

      return cEventString;
    });

    return cFilteredEvents;
  }

  /**
   * Returns a stream that fires an event at specified intervals.
   * 
   * @param sys     A Sodium FRP timer system.
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
   * Merges an array of GpsEvent streams 
   * 
   * @param   streams    An array of GpsEvent streams
   * @return  A merged stream of all the GpsEvent streams
  */
  public static Stream<GpsEvent> mergeStreams(Stream<GpsEvent>[] streams) {    
    return Stream.orElse(Arrays.asList(streams));
  }
}
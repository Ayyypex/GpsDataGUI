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
    this.setSize(1920, 1080);
    
    // get the tracker's event streams
    GpsService serv = new GpsService();
    Stream<GpsEvent>[] streams = serv.getEventStreams();

    // construct table header
    JPanel trackersTablePanel = new JPanel(new GridBagLayout());

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
    Insets tableRowInsets = new Insets(10, 70, 10, 70);
    Insets seperatorInsets = new Insets(2, 0, 2, 0);

    // add table headers
    addComponent(trackersTablePanel, trackerNumHeader, 0, 0, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerLatHeader, 0, 1, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerLonHeader, 0, 2, 1, 1, tableRowInsets);
    addComponent(trackersTablePanel, trackerDistDeader, 0, 4, 1, 1, tableRowInsets);

    // add table header separator
    JSeparator headerSep = new JSeparator(SwingConstants.HORIZONTAL);
    headerSep.setForeground(Color.black);
    headerSep.setBackground(Color.black);
    addComponent(trackersTablePanel, headerSep, 1, 0, 5, 1, seperatorInsets);

    // add vertical separator to indicate difference between the distance column
    JSeparator distanceSep = new JSeparator(SwingConstants.VERTICAL);
    distanceSep.setForeground(Color.black);
    distanceSep.setBackground(Color.black);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.VERTICAL;
    c.gridx=3;  
    c.gridy=0;
    c.gridwidth=1;
    c.gridheight=22;
    trackersTablePanel.add(distanceSep, c);

    int rowIndex = 2;   // row to put the tracker info in, because separator takes a row

    // fill row for each stream/tracker    
    for ( int i=0; i<streams.length; i++ ) {
      // get stream of GpsEvents for the tracker
      Stream<GpsEvent> sGpsEvents = streams[i];

      // get simplified tracker info
      Cell<String>[] simplifiedGpsCells =  getSimplifiedGpsCells(sGpsEvents);

      // create SLabels
      SLabel trackerNumLabel = new SLabel(simplifiedGpsCells[0]);
      SLabel trackerLatLabel = new SLabel(simplifiedGpsCells[1]);
      SLabel trackerLonLabel = new SLabel(simplifiedGpsCells[2]);
      
      // configure SLabels
      trackerNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
      Font tableValueFont = new Font("Courier", Font.PLAIN, 14);
      trackerNumLabel.setFont(tableValueFont);
      trackerLatLabel.setFont(tableValueFont);
      trackerLonLabel.setFont(tableValueFont);

      // add SLabels to table
      addComponent( trackersTablePanel, trackerNumLabel, rowIndex, 0, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, trackerLatLabel, rowIndex, 1, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, trackerLonLabel, rowIndex, 2, 1, 1, tableRowInsets );
      addComponent( trackersTablePanel, new JLabel("temporary4"), rowIndex, 4, 1, 1, tableRowInsets );

      // add separator for each row
      JSeparator rowSep = new JSeparator(SwingConstants.HORIZONTAL);
      addComponent( trackersTablePanel, rowSep, rowIndex-1, 0, 5, 1, seperatorInsets );
      rowIndex+=2;

      // add two seperators at bottom of table
      if ( i == streams.length-1 ) {
        rowSep = new JSeparator(SwingConstants.HORIZONTAL);
        addComponent( trackersTablePanel, rowSep, rowIndex, 0, 5, 1, seperatorInsets );
        rowSep = new JSeparator(SwingConstants.HORIZONTAL);
        rowSep.setForeground(Color.black);
        rowSep.setBackground(Color.black);
        addComponent( trackersTablePanel, rowSep, rowIndex+1, 0, 5, 1, seperatorInsets );
      }
    }

    // add to frame
    this.add(trackersTablePanel);
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
    c.fill = GridBagConstraints.HORIZONTAL;
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
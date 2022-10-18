import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;
import java.util.ArrayList;

/** My GUI which displays transformed tracker data retrieved from a stream using Sodium FRP operations. */
public class myGUI extends JFrame {

  boolean Testing = true;

  /** Creates an instance of myGUI and then shows it. */
  public static void main(String[] args) {
    myGUI GUI = new myGUI();
    GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GUI.setLocationRelativeTo(null);        // place gui window at center of screen
    GUI.setVisible(true);
  }

  /** Constructs my GUI. */
  public myGUI() {
    // mandatory configuration
    this.setTitle("a1765159's GUI");
    Border blackline = BorderFactory.createLineBorder(Color.black, 5);
    this.getRootPane().setBorder(blackline);
    this.setBackground(Color.gray);
    this.setSize(1200, 700);
    
    // get the tracker's event streams
    GpsService serv = new GpsService();
    Stream<GpsEvent>[] streams = serv.getEventStreams();
    
    // add components to gui
    JTabbedPane tabbedPane = new JTabbedPane();
    this.add(tabbedPane);
    tabbedPane.addTab( "Simplified Trackers", new simplifiedTrackersComponent(streams) );
    tabbedPane.addTab( "All Events", new allEventsComponent() );
    tabbedPane.addTab( "Events within Range", new eventsWithinRangeComponent() );
    tabbedPane.addTab( "Distance travelled within Range", new distanceTravelledComponent() );
  }

  /** Displays ten simplified tracker displays, stripping the altitude from a GpsEvent. */
  public class simplifiedTrackersComponent extends JPanel {

    public ArrayList<ArrayList<Cell<String>>> allCells;

    /**
     * Strips a GpsEvent stream of its altitude. 
     * 
     * @param   stream    A stream of GpsEvents
     * @return  A stream of SimpleGpsEvents
    */
    public Stream<SimpleGpsEvent> stripAltitude(Stream<GpsEvent> stream) {
      return stream.map( (GpsEvent ev) -> new SimpleGpsEvent(ev) );
    }

    /** Constructs the first required display. */
    public simplifiedTrackersComponent(Stream<GpsEvent>[] streams) {
      // configure main panel
      this.setLayout(new GridLayout(5, 2, 10, 10));

      // initialize arrayList
      if (Testing) {
        allCells = new ArrayList<ArrayList<Cell<String>>>();
        for ( int i=0; i<streams.length; i++ ) {
          allCells.add( new ArrayList<Cell<String>>() );
        }
      }

      // create and add each tracker display
      for ( int i=0; i<streams.length; i++ ) {
        Stream<GpsEvent> s = streams[i];

        // create and configure display panel
        JPanel trackerDisplay = new JPanel(new GridLayout(2, 3, 0, 5));
        trackerDisplay.setBackground(Color.white);
        trackerDisplay.setBorder(BorderFactory.createEtchedBorder());

        // create stream of simplified Gps Events
        Stream<SimpleGpsEvent> simplifiedGpsStream = stripAltitude(s);

        // set up cells to hold each field
        Cell<String> trackerNumber = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> String.valueOf(ev.name.charAt(7)) ).hold("N/A");
        Cell<String> trackerLatitude = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> String.valueOf(ev.latitude) ).hold("N/A");
        Cell<String> trackerLongitude = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> String.valueOf(ev.longitude) ).hold("N/A");

        // add cells to arraylist
        if (Testing) {
          allCells.get(i).add(trackerNumber);
          allCells.get(i).add(trackerLatitude);
          allCells.get(i).add(trackerLongitude);
        }

        // create SLabels
        SLabel trackerNumberLabel = new SLabel(trackerNumber);
        SLabel trackerLatLabel = new SLabel(trackerLatitude);
        SLabel trackerLongLabel = new SLabel(trackerLongitude);

        // configure SLabels
        trackerNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackerNumberLabel.setVerticalAlignment(SwingConstants.TOP);
        trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackerLatLabel.setVerticalAlignment(SwingConstants.TOP);
        trackerLongLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackerLongLabel.setVerticalAlignment(SwingConstants.TOP);

        // create header labels
        JLabel tNumberLbl = new JLabel("Tracker Number:");
        JLabel tLatLbl = new JLabel("Latitude:");
        JLabel tLongLbl = new JLabel("Longitude:");

        // configure header labels
        Font header = new Font("Courier", Font.BOLD, 15);
        tNumberLbl.setHorizontalAlignment(SwingConstants.CENTER);
        tNumberLbl.setVerticalAlignment(SwingConstants.BOTTOM);
        tNumberLbl.setFont(header);
        tLatLbl.setHorizontalAlignment(SwingConstants.CENTER);
        tLatLbl.setVerticalAlignment(SwingConstants.BOTTOM);
        tLatLbl.setFont(header);
        tLongLbl.setHorizontalAlignment(SwingConstants.CENTER);
        tLongLbl.setVerticalAlignment(SwingConstants.BOTTOM);
        tLongLbl.setFont(header);

        // add labels to display
        trackerDisplay.add(tNumberLbl);
        trackerDisplay.add(tLatLbl);
        trackerDisplay.add(tLongLbl);
        trackerDisplay.add(trackerNumberLabel);
        trackerDisplay.add(trackerLatLabel);
        trackerDisplay.add(trackerLongLabel);

        // add to main panel
        this.add(trackerDisplay);
      }
    }
  }

  /** Displays all GpsEvents passed to the GUI at the time they occur. */
  public class allEventsComponent extends JPanel {
    //
  }

  /** Displays all GpsEvents within a latitude and longitude range set by a user-configurable control panel. */
  public class eventsWithinRangeComponent extends JPanel {
    //
  }

  /** Displays the distance travelled over the last 5 minutes for each tracker within the latitude/longitude range. */
  public class distanceTravelledComponent extends JPanel {
    //
  }
}
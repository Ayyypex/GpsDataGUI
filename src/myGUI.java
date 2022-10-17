import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;

/** My GUI which displays transformed tracker data retrieved from a stream using Sodium FRP operations. */
public class myGUI extends JFrame {

  // need these while creating the components
  public Stream<GpsEvent>[] streams;

  /** Creates an instance of myGUI and then shows it. */
  public static void main(String[] args) {
    myGUI GUI = new myGUI();
    GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GUI.setLocationRelativeTo(null);        // place gui window at center of screen
    GUI.setVisible(true);
  }

  /** Constructs my GUI. */
  public myGUI() {
    // configuration
    this.setTitle("a1765159's GUI");
    //this.setResizable(false);
    Border blackline = BorderFactory.createLineBorder(Color.black, 5);
    this.getRootPane().setBorder(blackline);
    this.setBackground(Color.gray);
    this.setSize(1800, 900);
    
    // get the tracker's event streams
    GpsService serv = new GpsService();
    streams = serv.getEventStreams();
    
    // add components to gui
    this.add( new simplifiedTrackersComponent() );
    //this.add( new allEventsComponent() );
    //this.add( new eventsWithinRangeComponent() );
    //this.add( new distanceTravelledComponent() );
  }

  /** Displays ten simplified tracker displays, stripping the altitude from a GpsEvent. */
  public class simplifiedTrackersComponent extends JPanel {

    /** Constructor. */
    public simplifiedTrackersComponent() {
      // configure main panel
      this.setLayout(new GridLayout(5, 2, 15, 15));
      this.setSize(new Dimension(1720, 865));

      // add each tracker display
      for ( Stream<GpsEvent> s : streams ) {
        // create and configure display panel
        JPanel trackerDisplay = new JPanel(new GridLayout(2, 3, 50, 10));
        trackerDisplay.setBackground(Color.white);

        // create stream of simplified Gps Events
        Stream<SimpleGpsEvent> simplifiedGpsStream = s.map( (GpsEvent ev) -> new SimpleGpsEvent(ev) );

        // set up cells to hold each field
        Cell<String> trackerNumber = simplifiedGpsStream.map((SimpleGpsEvent ev) -> String.valueOf(ev.name.charAt(7))).hold("");
        Cell<String> trackerLatitude = simplifiedGpsStream.map((SimpleGpsEvent ev) -> String.valueOf(ev.latitude)).hold("");
        Cell<String> trackerLongitude = simplifiedGpsStream.map((SimpleGpsEvent ev) -> String.valueOf(ev.longitude)).hold("");

        // set up SLabels
        SLabel trackerNumberLabel = new SLabel(trackerNumber);
        SLabel trackerLatLabel = new SLabel(trackerLatitude);
        SLabel trackerLongLabel = new SLabel(trackerLongitude);

        // set up normal labels
        JLabel tNumberLbl = new JLabel("Tracker Number");
        JLabel tLatLbl = new JLabel("Latitude");
        JLabel tLongLbl = new JLabel("Longitude");

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
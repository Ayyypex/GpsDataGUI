import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import nz.sodium.*;

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
    tabbedPane.addTab( "Simplified Trackers", new SimplifiedTrackersComponent(streams) );
    tabbedPane.addTab( "All Events", new AllEventsComponent(streams) );
    tabbedPane.addTab( "Events within Range", new EventsWithinRangeComponent() );
    tabbedPane.addTab( "Distance travelled within Range", new DistanceTravelledComponent() );
  }

  /** Displays all GpsEvents within a latitude and longitude range set by a user-configurable control panel. */
  public class EventsWithinRangeComponent extends JPanel {
    //
  }

  /** Displays the distance travelled over the last 5 minutes for each tracker within the latitude/longitude range. */
  public class DistanceTravelledComponent extends JPanel {
    //
  }
}
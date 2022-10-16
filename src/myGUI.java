import javax.swing.*;
import nz.sodium.*;
import swidgets.*;

/** My GUI which displays transformed tracker data retrieved from a stream using Sodium FRP operations. */
public class myGUI extends JFrame {

  // need these while creating the components
  public JFrame gui;                           // maybe not
  public Stream<GpsEvent>[] streams;

  // arrays for simplifiedTrackersComponent (stc)
  public Cell<GpsEvent>[] stcCELLS;
  public SLabel[] stcLABELS;

  /** Creates an instance of myGUI and then shows it. */
  public static void main(String[] args) {
    myGUI gui = new myGUI();
    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gui.setLocationRelativeTo(null);  // place gui window at center of screen
    gui.setVisible(true);
  }

  /** Constructs my GUI. */
  public myGUI() {
    this.setTitle("a1765159's GUI");
    this.setResizable(false);
    this.setSize(1600, 900);

    // get all event streams
    GpsService serv = new GpsService();
    streams = serv.getEventStreams();
    
    // add components to gui
    this.add( new simplifiedTrackersComponent() );
    this.add( new allEventsComponent() );
    this.add( new eventsWithinRangeComponent() );
    this.add( new distanceTravelledComponent() );
  }

  /** Displays ten simplified tracker displays, stripping the altitude from a GpsEvent. */
  public class simplifiedTrackersComponent extends JPanel {
    //
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
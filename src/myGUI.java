import javax.swing.*;
import nz.sodium.*;
import swidgets.*;

/** My GUI which displays transformed tracker data retrieved from a stream using Sodium FRP operations. */
public class myGUI {

  // need these while creating the components
  public JFrame gui;                   // maybe not
  public Stream<GpsEvent>[] streams;

  /** Creates an instance of myGUI */
  public static void main(String[] args) {
    new myGUI();
  }

  /** Constructs and then shows my GUI. */
  public myGUI() {
    
    // create and configure gui
    gui = new JFrame();
    gui.setTitle("a1765159's GUI");
    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gui.setResizable(false);
    gui.setSize(1600, 900);

    // get all event streams
    GpsService serv = new GpsService();
    streams = serv.getEventStreams();
    
    // add components to gui
    gui.add( new simplifiedTrackersComponent() );
    gui.add( new allEventsComponent() );
    gui.add( new eventsWithinRangeComponent() );
    gui.add( new distanceTravelledComponent() );

    // show gui
    gui.setLocationRelativeTo(null);  // place gui window at center of screen
    gui.setVisible(true);
  }

  /** Displays ten simplified tracker displays, stripping the altitude from a GpsEvent. */
  private class simplifiedTrackersComponent extends JPanel {
    //
  }

  /** Displays all GpsEvents passed to the GUI at the time they occur. */
  private class allEventsComponent extends JPanel {
    //
  }

  /** Displays all GpsEvents within a latitude and longitude range set by a user-configurable control panel. */
  private class eventsWithinRangeComponent extends JPanel {
    //
  }

  /** Displays the distance travelled over the last 5 minutes for each tracker within the latitude/longitude range. */
  private class distanceTravelledComponent extends JPanel {
    //
  }
}
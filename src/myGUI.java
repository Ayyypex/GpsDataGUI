import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import java.util.*;

/** 
 * My GUI which displays transformed tracker data retrieved from a stream
 * using Sodium FRP operations. 
 */
public class myGUI extends JFrame {

  boolean Testing = true;

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
    tabbedPane.addTab( "Events within Range", new EventsWithinRangeComponent(streams, null) );
    tabbedPane.addTab( "Distance travelled within Range", new DistanceTravelledComponent(streams) );
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
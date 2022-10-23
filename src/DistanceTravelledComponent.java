import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;

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
   * Constructs the fourth required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   */
  public DistanceTravelledComponent(Stream<GpsEvent>[] streams) {
    Stream<GpsEvent> sGpsEvent = streams[1];

    Cell<String> allEvents = sGpsEvent.accum("init", (ev, state)-> state+ev.toString() );

    SLabel label = new SLabel(allEvents);
    this.add(label);
  }
}
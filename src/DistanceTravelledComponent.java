import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
import java.util.*;

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
    // set up Sodium FRP timer system and cell to hold the current time
    TimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;
    
    Stream<GpsEvent> sGpsEvent = streams[1];

    // accumulate events
    Cell<ArrayList<GpsEvent>> allEvents = sGpsEvent.accum(new ArrayList<GpsEvent>(), (ev, list) -> {
      ev.name+=String.valueOf(cTime.sample());
      ev.setTime(cTime.sample());
      list.add(ev);
      return list;
    })
    // remove events older than 5 seconds
    .map( (list) -> {
      ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
      for ( GpsEvent ev : list ) {
        if ( cTime.sample() - ev.timeAdded < 5 ) {
          newList.add(ev);
        }
      }
      return newList;
    });

    // cell to show the names of events currently in list
    Cell<String> names = allEvents.map( (list) -> {
      String nameList = "";
      for (GpsEvent l: list) {
        nameList += l.name + " ";
      }
      return nameList;
    });

    // add to display
    SLabel label = new SLabel(names);
    this.add(label);
  }
}
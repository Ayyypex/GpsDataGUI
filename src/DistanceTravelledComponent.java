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
    SecondsTimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;
    
    Stream<GpsEvent> sGpsEvent = streams[1];

    // accumulate events
    Cell<ArrayList<GpsEvent>> allEvents = sGpsEvent.accum(
      new ArrayList<GpsEvent>(), (GpsEvent ev, ArrayList<GpsEvent> list) -> {
        ev.name+=String.valueOf(cTime.sample());
        ev.setTime( cTime.sample() );
        list.add(ev);
        return list;
      }
    )
    // remove events older than 10 seconds (5 MINUTES LATER ON)
    .map( (ArrayList<GpsEvent> list) -> {
      ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
      for ( GpsEvent ev : list ) {
        if ( (cTime.sample() - ev.timeAdded) < 10 ) {
          newList.add(ev);
        }
      }
      return newList;
    });

    

    // loop() in peridioc can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every second
      Stream<Double> sTimer = myGUI.periodic(sys, 1.0);

      // create sliding window that contains the events from the last 10 seconds (5 MINUTES LATER ON)
      Stream<ArrayList<GpsEvent>> sSlidingWindow = sTimer.snapshot(
        allEvents, (Double t, ArrayList<GpsEvent>list) -> {
          ArrayList<GpsEvent> newList = new ArrayList<GpsEvent>();
          for ( GpsEvent ev : list ) {
            if ( (cTime.sample() - ev.timeAdded) < 10 ) {
              newList.add(ev);
            }
          }
          return newList;
        }
      );

      // cell to show the summed up latitude of events currently in sliding window
      Cell<String> latitudes = sSlidingWindow.map( (ArrayList<GpsEvent> list) -> {
        // no events in sliding window
        if ( list.size() == 0 ) {
          return "0.0";
        }

        Double latSum = 0.0;
        for (GpsEvent l: list) {
          latSum += l.latitude;
        }
        return String.valueOf(latSum);
      }).hold("");

      // add to display
      SLabel label = new SLabel(latitudes);
      this.add(label);
    });    
  }
}
import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
import java.util.*;

/** Displays all GpsEvents passed to the GUI at the time they occur. */
public class AllEventsComponent extends JPanel {

  boolean Testing = true;
  public Cell<String> cell;

  /**
   * Returns a stream that fires an event at specified intervals.
   * 
   * @param sys     A Sodium FRP timer system.
   * @param period  The specified interval that the stream will fire.
   * @return        A timer in the form of a stream that will repeatedly fire periodically.
   */
  static Stream<Long> periodic(TimerSystem sys, long period) {
    Cell<Long> time = sys.time;
    CellLoop<Optional<Long>> oAlarm = new CellLoop<>();
    Stream<Long> sAlarm = sys.at(oAlarm);
    oAlarm.loop(
      sAlarm.map(t -> Optional.of(t + period))
        .hold(Optional.<Long>of(time.sample() + period)));
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

  /** 
   * Constructs the second required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be displayed.
   */
  public AllEventsComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridBagLayout());

    // set up Sodium FRP timer system and cell to hold the current time
    TimerSystem sys = new MillisecondsTimerSystem();
    Cell<Long> time = sys.time;

    // merge streams
    Stream<GpsEvent> allStreams = mergeStreams(streams);

    // record system time of last event occurrence
    Cell<Long> lastEventTime = allStreams.map( (GpsEvent ev) -> time.sample() ).hold(Long.valueOf(0));

    // create and configure event panel that info will be displayed in
    JPanel eventPanel = new JPanel();
    eventPanel.setBackground(Color.white);
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setPreferredSize( new Dimension(500, 50) );
    
    // loop() in peridioc can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Long> timerStream = periodic(sys, 100);

      // create CellLoop for eventString, as the clearStream will check whether it is empty or not, so that it doesn't repeatedly fire an empty string
      CellLoop<String> eventString = new CellLoop<>();

      // at every event, produce empty string if the time between the last event and now has exceeded 3 seconds and the eventString isn't already empty
      Stream<String> clearStream = timerStream.filter( (Long t) -> ((time.sample() - lastEventTime.sample()) > 3000) && (eventString.sample() != "")  ).map((Long t) -> "");

      // set up cells to hold the event and the time at which it occurs as separate strings
      eventString.loop( allStreams.map( (GpsEvent ev) -> ev.toString() ).orElse(clearStream).hold("") );
      Cell<String> timeStampString = allStreams.map( (GpsEvent ev) -> "at: " + String.valueOf(java.time.LocalTime.now()) ).orElse(clearStream).hold("");

      // create SLabels
      SLabel eventStringLabel = new SLabel(eventString);
      SLabel timeStampLabel = new SLabel(timeStampString);

      // add labels to display
      eventPanel.add(eventStringLabel);
      eventPanel.add(timeStampLabel);
    });

    // add to main panel
    this.add(eventPanel);
  }
}
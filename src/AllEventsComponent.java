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

  ///////////
  static Stream<Long> periodic(TimerSystem sys, long period) {
    Cell<Long> time = sys.time;
    CellLoop<Optional<Long>> oAlarm = new CellLoop<>();
    Stream<Long> sAlarm = sys.at(oAlarm);
    oAlarm.loop(
    sAlarm.map(t -> Optional.of(t + period))
    .hold(Optional.<Long>of(time.sample() + period)));
    return sAlarm;
  }
  ////////////

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
    ///////////
    TimerSystem sys = new MillisecondsTimerSystem();
    Cell<Long> time = sys.time;
    ///////////

    // configure main panel
    this.setLayout(new GridBagLayout());

    // merge streams
    Stream<GpsEvent> allStreams = streams[0];//mergeStreams(streams);

    // set up cells to hold the event as a string and the time at which it occurs
    Cell<String> eventString = allStreams.map( (GpsEvent ev) -> ev.toString() ).hold("");
    Cell<String> timeStamp = allStreams.map( (GpsEvent ev) -> "at: " + String.valueOf(java.time.LocalTime.now()) ).hold("");

    /////////
    Stream<Long> timeStamp1 = allStreams.map( (GpsEvent ev) -> time.sample() );
    Cell<Long> lastEventTime = timeStamp1.hold(Long.valueOf(0));
      /////////

    // create SLabels
    SLabel eventStringLabel = new SLabel(eventString);
    SLabel timeStampLabel = new SLabel(timeStamp);

    ///////////////////
    Transaction.runVoid(() -> {
      Stream<String> clearStream = periodic(sys, 100).map((t)->String.valueOf(time.sample()));//.snapshot(lastEventTime).filter( a -> (a - lastEventTime.sample()) < 3000 ).map(t->"");
      Stream<String> clearStream2 = clearStream.snapshot(lastEventTime).filter(t->((time.sample() - lastEventTime.sample()) > 3000)).map(t->"");
      Cell<String> eventString1 = allStreams.map( (GpsEvent ev) -> ev.toString() ).orElse(clearStream2).hold("");
      SLabel eventStringLabel1 = new SLabel(eventString1);
      this.add(eventStringLabel1);
    });
    /////////////////

    // create and configure event panel that info will be displayed in
    JPanel eventPanel = new JPanel();
    eventPanel.setBackground(Color.white);
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setPreferredSize( new Dimension(500, 50) );

    // add labels to display
    eventPanel.add(eventStringLabel);
    eventPanel.add(timeStampLabel);

    // add to main panel
    this.add(eventPanel);
  }
}
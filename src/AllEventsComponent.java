import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;

/** Displays all GpsEvents passed to the GUI at the time they occur. */
public class AllEventsComponent extends JPanel {

  boolean Testing = true;
  public Cell<String> eventCell;

  /** 
   * Constructs the second required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   */
  public AllEventsComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridBagLayout());

    // set up Sodium FRP timer system and cell to hold the current time
    TimerSystem sys = new MillisecondsTimerSystem();
    Cell<Long> time = sys.time;

    // merge streams
    Stream<GpsEvent> allStreams = myGUI.mergeStreams(streams);

    // record system time of last event occurrence
    Cell<Long> lastEventTime = allStreams.map( (GpsEvent ev) -> time.sample() )
      .hold(Long.valueOf(0));

    // create and configure event panel that info will be displayed in
    JPanel eventPanel = new JPanel();
    eventPanel.setBackground(Color.white);
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setPreferredSize( new Dimension(500, 50) );
    
    // loop() in peridioc can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Long> timerStream = myGUI.periodic(sys, 100);

      // create CellLoop for eventString to prevent repeatedly firing an empty string
      CellLoop<String> eventString = new CellLoop<>();

      // fire empty string if > 3 seconds since last event and eventString isn't empty
      Stream<String> clearStream = timerStream.filter( (Long t) -> 
        ((time.sample() - lastEventTime.sample()) > 3000) && (eventString.sample() != "") )
          .map((Long t) -> "");

      // set up cells to hold the event and the time at which it occurs as separate strings
      eventString.loop( 
        allStreams.map( (GpsEvent ev) -> ev.toString() )
          .orElse(clearStream)
            .hold("") );
      Cell<String> timeStampString = allStreams.map( (GpsEvent ev) -> 
        "at: " + String.valueOf(java.time.LocalTime.now()) )
          .orElse(clearStream)
            .hold("");

      // set testing cell
      if (Testing) {
        eventCell = eventString;
      }

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
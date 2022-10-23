import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;

/** Displays all GpsEvents passed to the GUI at the time they occur. */
public class AllEventsComponent extends JPanel {

  public CellLoop<String> cEventString;

  /** 
   * Constructs the second required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   */
  public AllEventsComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridBagLayout());

    // set up Sodium FRP timer system and cell to hold the current time
    MillisecondsTimerSystem sys = new MillisecondsTimerSystem();
    Cell<Long> cTime = sys.time;

    // merge streams
    Stream<GpsEvent> sAll = myGUI.mergeStreams(streams);

    // record system time of last event occurrence
    Cell<Long> cLastEventTime = sAll.map( (GpsEvent ev) -> cTime.sample() )
      .hold(Long.valueOf(0));

    // create and configure event panel that info will be displayed in
    JPanel eventPanel = new JPanel();
    eventPanel.setBackground(Color.white);
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setPreferredSize( new Dimension(500, 30) );
    
    // loop() in peridioc can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Long> sTimer = myGUI.periodic(sys, 100);

      // set CellLoop for eventString to prevent repeatedly firing an empty string
      cEventString = new CellLoop<>();

      // fire empty string if > 3 seconds since last event and eventString isn't empty
      Stream<String> sClear = sTimer.filter( (Long t) -> 
        ((cTime.sample() - cLastEventTime.sample()) > 3000) && (cEventString.sample() != "") )
          .map((Long t) -> "");

      // set up cell to hold the event as a string
      cEventString.loop(
        sAll.map( (GpsEvent ev) -> ev.toString() )
          .orElse(sClear)
            .hold("") );

      // create SLabel and add to panel
      SLabel eventStringLabel = new SLabel(cEventString);
      eventPanel.add(eventStringLabel);
    });

    // add to main panel
    this.add(eventPanel);
  }
}
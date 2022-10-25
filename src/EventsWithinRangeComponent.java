import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;

/** 
 * Displays all GpsEvents within a latitude and longitude range set by
 * a user-configurable control panel.
 */
public class EventsWithinRangeComponent extends JPanel {
  
  boolean Testing = true;
  public CellLoop<String> cEventString;
  public ControlPanelComponent controlPanel;

  /** 
   * Constructs the third required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   * @param  sTest     A Unit StreamSink used for testing the component.
   */
  public EventsWithinRangeComponent(Stream<GpsEvent>[] streams, ControlPanelComponent ctrlPnl, StreamSink<Unit> sTest) {
    // configure main panel
    this.setLayout(new GridLayout(1, 2));

    // set control panel
    if ( ctrlPnl == null ) {
      controlPanel = new ControlPanelComponent(sTest);
    } else {
      controlPanel = ctrlPnl;
    }

    // create subpanel
    JPanel allEventsPanel = new JPanel(new GridBagLayout());
    
    // set up Sodium FRP timer system and cell to hold the current time
    SecondsTimerSystem sys = new SecondsTimerSystem();
    Cell<Double> cTime = sys.time;

    // merge streams and set up cells to hold the current lat and lon range
    Stream<GpsEvent> sAllGpsEvents = myGUI.mergeStreams(streams);
    Cell<Double> cLatMin = controlPanel.cLatMin.map( (String latMin) -> Double.parseDouble(latMin) );
    Cell<Double> cLatMax = controlPanel.cLatMax.map( (String latMax) -> Double.parseDouble(latMax) );
    Cell<Double> cLonMin = controlPanel.cLonMin.map( (String lonMin) -> Double.parseDouble(lonMin) );
    Cell<Double> cLonMax = controlPanel.cLonMax.map( (String lonMax) -> Double.parseDouble(lonMax) );        

    // create and configure event panel that event info will be displayed in
    JPanel eventPanel = new JPanel();
    eventPanel.setBackground(Color.white);
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setPreferredSize( new Dimension(500, 30) );
    
    // loop() can only run in a Transaction 
    Transaction.runVoid(() -> {
      // create stream that will fire an event every 0.1 seconds
      Stream<Double> sTimer = myGUI.periodic(sys, 0.1);

      // set CellLoop for eventString to prevent repeatedly firing an empty string
      cEventString = new CellLoop<>();

      // filter the merged stream such that it outputs events within specified range
      Stream<GpsEvent> sEventsInRange = sAllGpsEvents.filter( (GpsEvent ev) -> ev.latitude >= cLatMin.sample() )
        .filter( (GpsEvent ev) -> ev.latitude <= cLatMax.sample() )  
          .filter(  (GpsEvent ev) -> ev.longitude >= cLonMin.sample() ) 
            .filter( (GpsEvent ev) -> ev.longitude <= cLonMax.sample() );

      // record system time of last within range event occurrence
      Cell<Double> cLastEventTime = sEventsInRange.map( (GpsEvent ev) -> cTime.sample() )
        .hold(0.0);

      // fire empty string if > 3 seconds since last event and eventString isn't empty
      Stream<String> sClear = sTimer.filter( (Double t) -> 
        ((cTime.sample() - cLastEventTime.sample()) > 3) && (cEventString.sample() != "") )
          .map((Double t) -> "");

      // set up cell to hold the event as a string
      cEventString.loop(
        sEventsInRange.map( (GpsEvent ev) -> ev.toString() )
          .orElse(sClear)
            .hold("") );

      // create SLabel and add to panel
      SLabel eventStringLabel = new SLabel(cEventString);
      eventPanel.add(eventStringLabel);
      allEventsPanel.add(eventPanel);
    });
    
    // add to main panel
    this.add(controlPanel);
    this.add(allEventsPanel);
  }
}
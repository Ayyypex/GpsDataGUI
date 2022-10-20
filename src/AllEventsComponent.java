import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;
import java.util.*;

/** Displays all GpsEvents passed to the GUI at the time they occur. */
public class AllEventsComponent extends JPanel {

  boolean Testing = true;
  public Cell<String> cell;

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

    // merge streams
    Stream<GpsEvent> allStreams = mergeStreams(streams);

    // set up cells to hold the event as a string and the time at which it occurs
    Cell<String> eventString = allStreams.map( (GpsEvent ev) -> ev.toString() ).hold("");
    Cell<String> timeStamp = allStreams.map( (GpsEvent ev) -> "at: " + String.valueOf(java.time.LocalTime.now()) ).hold("");

    // create SLabels
    SLabel eventStringLabel = new SLabel(eventString);
    SLabel timeStampLabel = new SLabel(timeStamp);

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
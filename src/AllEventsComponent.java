import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;
import java.util.*;
import java.lang.*;

/** Displays all GpsEvents passed to the GUI at the time they occur. */
public class AllEventsComponent extends JPanel {

  boolean Testing = true;
  public Cell<String> cell;

  @FunctionalInterface
  interface IMyFunc {
    GpsEvent func(GpsEvent a, GpsEvent b);
  }

  /**
   * Merges an array of GpsEvent streams 
   * 
   * @param   streams    An array of GpsEvent streams
   * @return  A merged stream of all the GpsEvent streams
  */
  public static Stream<GpsEvent> mergeStreams(Stream<GpsEvent>[] streams) {    
    // define combining function to prioritize 'leftmost' event
    //Lambda2<GpsEvent, GpsEvent, GpsEvent> combiningFunction = (GpsEvent l, GpsEvent r) -> l;
    // merge the streams
    //return Stream.merge(Arrays.asList(streams),  combiningFunction);

    return Stream.orElse(Arrays.asList(streams));
  }

  /** 
   * Constructs the second required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be displayed.
   */
  public AllEventsComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    //

    // merge streams
    Stream<GpsEvent> allStreams = mergeStreams(streams);

    // show stream events
    Cell<String> info = allStreams.map( (GpsEvent ev) -> ev.toString() ).hold("N/A");
    SLabel infoLabel = new SLabel(info);
    this.add(infoLabel);

  }
}
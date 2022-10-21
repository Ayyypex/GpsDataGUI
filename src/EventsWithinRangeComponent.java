import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import nz.sodium.time.*;
import swidgets.*;
import java.util.*;

/** 
 * Displays all GpsEvents within a latitude and longitude range set by
 * a user-configurable control panel.
 */
public class EventsWithinRangeComponent extends JPanel {
  
  boolean Testing = true;
  public Cell<String> cell;

  /** 
   * Constructs the third required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be merged and their events displayed.
   */
  public EventsWithinRangeComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridBagLayout());
  }
}
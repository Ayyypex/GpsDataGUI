import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;
import java.util.*;

/** Displays ten simplified tracker displays, stripping the altitude from a GpsEvent. */
public class SimplifiedTrackersComponent extends JPanel {

  boolean Testing = true;
  public ArrayList<ArrayList<Cell<String>>> allCells;

  /**
   * Strips a GpsEvent stream of its altitude. 
   * 
   * @param   stream    A stream of GpsEvents
   * @return  A stream of SimpleGpsEvents
   */
  public static Stream<SimpleGpsEvent> stripAltitude(Stream<GpsEvent> stream) {
    return stream.map( (GpsEvent ev) -> new SimpleGpsEvent(ev) );
  }

  /** 
   * Constructs the first required display. 
   * 
   * @param  streams   An array of GpsEvent streams that will be displayed.
   */
  public SimplifiedTrackersComponent(Stream<GpsEvent>[] streams) {
    // configure main panel
    this.setLayout(new GridLayout(5, 2, 10, 10));

    // initialize arrayList
    if (Testing) {
      allCells = new ArrayList<ArrayList<Cell<String>>>();
      for ( int i=0; i<streams.length; i++ ) {
        allCells.add( new ArrayList<Cell<String>>() );
      }
    }

    // create and add each tracker display
    for ( int i=0; i<streams.length; i++ ) {
      Stream<GpsEvent> s = streams[i];

      // create and configure display panel
      JPanel trackerPanel = new JPanel(new GridLayout(2, 3, 0, 5));
      trackerPanel.setBackground(Color.white);
      trackerPanel.setBorder(BorderFactory.createEtchedBorder());

      // set up cell to hold the stripped stream's SimpleGpsEvent
      Cell<SimpleGpsEvent> cEvent = stripAltitude(s)
        .hold( new SimpleGpsEvent() );

      // set up cells to hold each field
      Cell<String> cTrackerNumber = cEvent.map( (SimpleGpsEvent ev) -> ev.getTrackerNumber() );
      Cell<String> cTrackerLatitude = cEvent.map( (SimpleGpsEvent ev) -> ev.getLatitude() );
      Cell<String> cTrackerLongitude = cEvent.map( (SimpleGpsEvent ev) -> ev.getLongitude() );

      // add cells to arraylist
      if (Testing) {
        allCells.get(i).add(cTrackerNumber);
        allCells.get(i).add(cTrackerLatitude);
        allCells.get(i).add(cTrackerLongitude);
      }

      // create SLabels
      SLabel trackerNumLabel = new SLabel(cTrackerNumber);
      SLabel trackerLatLabel = new SLabel(cTrackerLatitude);
      SLabel trackerLongLabel = new SLabel(cTrackerLongitude);

      // configure SLabels
      trackerNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerNumLabel.setVerticalAlignment(SwingConstants.TOP);
      trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatLabel.setVerticalAlignment(SwingConstants.TOP);
      trackerLongLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLongLabel.setVerticalAlignment(SwingConstants.TOP);

      // create header labels
      JLabel trackerNumHeader = new JLabel("Tracker Number:");
      JLabel trackerLatHeader = new JLabel("Latitude:");
      JLabel trackerLonHeader = new JLabel("Longitude:");

      // configure header labels
      Font header = new Font("Courier", Font.BOLD, 15);
      trackerNumHeader.setHorizontalAlignment(SwingConstants.CENTER);
      trackerNumHeader.setVerticalAlignment(SwingConstants.BOTTOM);
      trackerNumHeader.setFont(header);
      trackerLatHeader.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatHeader.setVerticalAlignment(SwingConstants.BOTTOM);
      trackerLatHeader.setFont(header);
      trackerLonHeader.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLonHeader.setVerticalAlignment(SwingConstants.BOTTOM);
      trackerLonHeader.setFont(header);

      // add labels to display
      trackerPanel.add(trackerNumHeader);
      trackerPanel.add(trackerLatHeader);
      trackerPanel.add(trackerLonHeader);
      trackerPanel.add(trackerNumLabel);
      trackerPanel.add(trackerLatLabel);
      trackerPanel.add(trackerLongLabel);

      // add to main panel
      this.add(trackerPanel);
    }
  }
}
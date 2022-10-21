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

      // create stream of simplified Gps Events
      Stream<SimpleGpsEvent> simplifiedGpsStream = stripAltitude(s);

      // set up cells to hold each field
      Cell<String> trackerNumber = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> 
        String.valueOf(ev.name.charAt(7)) )
          .hold("N/A");
      Cell<String> trackerLatitude = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> 
        String.valueOf(ev.latitude) )
          .hold("N/A");
      Cell<String> trackerLongitude = simplifiedGpsStream.map( (SimpleGpsEvent ev) -> 
        String.valueOf(ev.longitude) )
          .hold("N/A");

      // add cells to arraylist
      if (Testing) {
        allCells.get(i).add(trackerNumber);
        allCells.get(i).add(trackerLatitude);
        allCells.get(i).add(trackerLongitude);
      }

      // create SLabels
      SLabel trackerNumberLabel = new SLabel(trackerNumber);
      SLabel trackerLatLabel = new SLabel(trackerLatitude);
      SLabel trackerLongLabel = new SLabel(trackerLongitude);

      // configure SLabels
      trackerNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerNumberLabel.setVerticalAlignment(SwingConstants.TOP);
      trackerLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLatLabel.setVerticalAlignment(SwingConstants.TOP);
      trackerLongLabel.setHorizontalAlignment(SwingConstants.CENTER);
      trackerLongLabel.setVerticalAlignment(SwingConstants.TOP);

      // create header labels
      JLabel tNumberLbl = new JLabel("Tracker Number:");
      JLabel tLatLbl = new JLabel("Latitude:");
      JLabel tLongLbl = new JLabel("Longitude:");

      // configure header labels
      Font header = new Font("Courier", Font.BOLD, 15);
      tNumberLbl.setHorizontalAlignment(SwingConstants.CENTER);
      tNumberLbl.setVerticalAlignment(SwingConstants.BOTTOM);
      tNumberLbl.setFont(header);
      tLatLbl.setHorizontalAlignment(SwingConstants.CENTER);
      tLatLbl.setVerticalAlignment(SwingConstants.BOTTOM);
      tLatLbl.setFont(header);
      tLongLbl.setHorizontalAlignment(SwingConstants.CENTER);
      tLongLbl.setVerticalAlignment(SwingConstants.BOTTOM);
      tLongLbl.setFont(header);

      // add labels to display
      trackerPanel.add(tNumberLbl);
      trackerPanel.add(tLatLbl);
      trackerPanel.add(tLongLbl);
      trackerPanel.add(trackerNumberLabel);
      trackerPanel.add(trackerLatLabel);
      trackerPanel.add(trackerLongLabel);

      // add to main panel
      this.add(trackerPanel);
    }
  }
}
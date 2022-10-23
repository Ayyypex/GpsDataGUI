import static org.junit.Assert.assertEquals;
import org.junit.*;
import nz.sodium.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the SimplifiedTrackersComponent. */
public class SimplifiedTrackers_Test {
  @Test
  public void stripAltitude_test() {
    // we will send a test event to this stream
    StreamSink<GpsEvent> sGps = new StreamSink<GpsEvent>();
    
    // set up stream that will strip the altitude off the GpsEvent stream
    Stream<SimpleGpsEvent> sSimpleGps = SimplifiedTrackersComponent.stripAltitude(sGps);

    // set up cells to hold each field
    Cell<String> cNname = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.name )
      .hold("N/A");
    Cell<Double> cLatitude = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.latitude )
      .hold(-1.11);
    Cell<Double> cLongitude = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.longitude )
      .hold(-1.11);

    // set up and send a test event
    GpsEvent event = new GpsEvent("Tracker0",  0.00, 1.11, 2.22);
    sGps.send(event);

    // check that the fields were carried over correctly
    assertEquals( event.name, cNname.sample() );
    assertEquals( event.latitude, cLatitude.sample(), 0.00 );
    assertEquals( event.longitude, cLongitude.sample(), 0.00 );
  }

  @Test
  public void correctDisplayOutput_test() {
    // set up linked list of GpsEvent StreamSinks
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGps0 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps1 = new StreamSink<GpsEvent>();
    streams.add(sGps0);
    streams.add(sGps1);

    // convert linked list to GpsEvent stream array
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // create instance of the component with the streamsArray
    SimplifiedTrackersComponent display = new SimplifiedTrackersComponent(streamsArray);

    // create and send events down stream
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    sGps0.send(ev1);
    sGps1.send(ev2);

    // get list of cells we want to check
    ArrayList<Cell<String>> tracker0Cells = display.allCells.get(0);
    ArrayList<Cell<String>> tracker1Cells = display.allCells.get(1);

    // check that the tracker number, latitude, and longitude are as expected
    assertEquals( String.valueOf(ev1.name.charAt(7)), tracker0Cells.get(0).sample() );
    assertEquals( String.valueOf(ev1.latitude), tracker0Cells.get(1).sample() );
    assertEquals( String.valueOf(ev1.longitude), tracker0Cells.get(2).sample() );

    assertEquals( String.valueOf(ev2.name.charAt(7)), tracker1Cells.get(0).sample() );
    assertEquals( String.valueOf(ev2.latitude), tracker1Cells.get(1).sample() );
    assertEquals( String.valueOf(ev2.longitude), tracker1Cells.get(2).sample() );
  }
}
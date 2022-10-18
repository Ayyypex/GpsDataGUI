import static org.junit.Assert.assertEquals;
import org.junit.*;
import nz.sodium.*;
import java.util.*;

/** Tests the simplifiedTrackersComponent. */
public class SimplifiedTrackers_Test {
  @Test
  public void stripAltitude_test() {
    // we will send a test event to this stream
    StreamSink<GpsEvent> GpsStream = new StreamSink<GpsEvent>();
    
    // set up stream that will strip the altitude off the GpsEvent stream
    Stream<SimpleGpsEvent> simpleGpsStream = SimplifiedTrackersComponent.stripAltitude(GpsStream);

    // set up and send a test event
    GpsEvent event = new GpsEvent("Tracker0",  0.00, 1.11, 2.22);
    GpsStream.send(event);

    // check that the fields were carried over correctly 
    simpleGpsStream.listen( (SimpleGpsEvent ev) -> {
      assertEquals(event.name, ev.name);
      assertEquals(event.latitude, ev.latitude, 0.00);
      assertEquals(event.longitude, ev.longitude, 0.00);
    });
  }

  @Test
  public void correctDisplayOutput_test() {
    // set up linked list of GpsEvent StreamSinks
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> GpsStream1 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> GpsStream2 = new StreamSink<GpsEvent>();
    streams.add(GpsStream1);
    streams.add(GpsStream2);

    // convert linked list to GpsEvent stream array
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]); // got this line from GpsService

    // create instance of the component with the streamsArray
    SimplifiedTrackersComponent display = new SimplifiedTrackersComponent(streamsArray);

    // create and send events down stream
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    GpsStream1.send(ev1);
    GpsStream2.send(ev2);

    // get list of cells we want to check
    ArrayList<Cell<String>> tracker0Cells = display.allCells.get(0);
    ArrayList<Cell<String>> tracker1Cells = display.allCells.get(1);

    // check that the tracker number, latitude, and longitude are as expected
    assertEquals(String.valueOf(ev1.name.charAt(7)), tracker0Cells.get(0).sample());
    assertEquals(String.valueOf(ev1.latitude), tracker0Cells.get(1).sample());
    assertEquals(String.valueOf(ev1.longitude), tracker0Cells.get(2).sample());

    assertEquals(String.valueOf(ev2.name.charAt(7)), tracker1Cells.get(0).sample());
    assertEquals(String.valueOf(ev2.latitude), tracker1Cells.get(1).sample());
    assertEquals(String.valueOf(ev2.longitude), tracker1Cells.get(2).sample());
  }
}
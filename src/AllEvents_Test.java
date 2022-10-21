import static org.junit.Assert.*;
import org.junit.*;
import nz.sodium.*;
import java.util.*;

/** Tests the AllEventsComponent. */
public class AllEvents_Test {
  @Test
  public void mergeStreams_test() {
    // set up linked list of GpsEvent StreamSinks
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> GpsStream0 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> GpsStream1 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> GpsStream2 = new StreamSink<GpsEvent>();
    streams.add(GpsStream0);
    streams.add(GpsStream1);
    streams.add(GpsStream2);

    // convert linked list to GpsEvent stream array
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // create instance of the component with the streamsArray
    AllEventsComponent display = new AllEventsComponent(streamsArray);

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    GpsEvent ev3 = new GpsEvent("Tracker2", 7.77, 8.88, 9.99);

    // get cell we want to check
    Cell<String> testCell = display.cell;

    // send events and check that the cell updates appropriately
    GpsStream0.send(ev1);
    assertEquals( ev1.toString(), testCell.sample() );

    GpsStream2.send(ev3);
    assertNotEquals( ev1.toString(), testCell.sample() );
    assertEquals( ev3.toString(), testCell.sample() );

    GpsStream1.send(ev2);
    assertNotEquals( ev3.toString(), testCell.sample() );
    assertEquals( ev2.toString(), testCell.sample() );
  }

  @Test
  public void clearStream_test() {
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> GpsStream0 = new StreamSink<GpsEvent>();
    streams.add(GpsStream0);
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    AllEventsComponent display = new AllEventsComponent(streamsArray);
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    Cell<String> testCell = display.cell;

    GpsStream0.send(ev1);
    assertEquals( ev1.toString(), testCell.sample() );

    // wait 2.5 seconds, shouldn't be cleared yet
    try { Thread.sleep(2500); } catch (InterruptedException e) {}
    assertNotEquals( "", testCell.sample() );

    // should be cleared after 0.6 more seconds
    try { Thread.sleep(600); } catch (InterruptedException e) {}
    assertEquals( "", testCell.sample() );

    GpsStream0.send(ev2);
    assertEquals( ev2.toString(), testCell.sample() );
    try { Thread.sleep(3100); } catch (InterruptedException e) {}
    assertEquals( "", testCell.sample() );
  }
}
import static org.junit.Assert.*;
import org.junit.*;
import nz.sodium.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the AllEventsComponent. */
public class AllEvents_Test {
  @Test
  public void mergeStreams_test() {
    // set up linked list of GpsEvent StreamSinks
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGps0 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps1 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps2 = new StreamSink<GpsEvent>();
    streams.add(sGps0);
    streams.add(sGps1);
    streams.add(sGps2);

    // convert linked list to GpsEvent stream array
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // create instance of the component with the streamsArray
    AllEventsComponent display = new AllEventsComponent(streamsArray);

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    GpsEvent ev3 = new GpsEvent("Tracker2", 7.77, 8.88, 9.99);

    // get cell we want to check
    Cell<String> cTest = display.cEventString;

    // send events and check that the cell updates appropriately
    sGps0.send(ev1);
    assertEquals( ev1.toString(), cTest.sample() );

    sGps1.send(ev3);
    assertNotEquals( ev1.toString(), cTest.sample() );
    assertEquals( ev3.toString(), cTest.sample() );

    sGps2.send(ev2);
    assertNotEquals( ev3.toString(), cTest.sample() );
    assertEquals( ev2.toString(), cTest.sample() );
  }

  @Test
  public void clearStream_test() {
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGps = new StreamSink<GpsEvent>();
    streams.add(sGps);
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    AllEventsComponent display = new AllEventsComponent(streamsArray);
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    Cell<String> cTest = display.cEventString;

    sGps.send(ev1);
    assertEquals( ev1.toString(), cTest.sample() );

    // wait 2.5 seconds, shouldn't be cleared yet
    try { Thread.sleep(2500); } catch (InterruptedException e) {}
    assertNotEquals( "", cTest.sample() );

    // should be cleared after 0.6 more seconds
    try { Thread.sleep(600); } catch (InterruptedException e) {}
    assertEquals( "", cTest.sample() );

    sGps.send(ev2);
    assertEquals( ev2.toString(), cTest.sample() );
    try { Thread.sleep(3100); } catch (InterruptedException e) {}
    assertEquals( "", cTest.sample() );
  }
}
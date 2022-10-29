import org.junit.*;
import static org.junit.Assert.*;
import nz.sodium.*;
import nz.sodium.time.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the functions related to the allEvents display. */
public class allEvents_Test {
  @Test
  public void mergeStreams_test() {
    // set up linked list of GpsEvent StreamSinks and convert it to an array
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGps0 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps1 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps2 = new StreamSink<GpsEvent>();
    streams.add(sGps0);
    streams.add(sGps1);
    streams.add(sGps2);
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // get cell we want to test
    Cell<String> cTest = myGUI.getAllEventsCell(new SecondsTimerSystem(), streamsArray);

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);
    GpsEvent ev3 = new GpsEvent("Tracker2", 7.77, 8.88, 9.99);

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
    // set up linked list of GpsEvent StreamSinks and convert it to an array
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    streams.add(sGpsEvents);
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // get cell we want to test and create events
    Cell<String> cTest = myGUI.getAllEventsCell(new SecondsTimerSystem(), streamsArray);
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);

    // send event
    sGpsEvents.send(ev1);
    assertEquals( ev1.toString(), cTest.sample() );

    // wait 2.5 seconds, shouldn't be cleared yet
    try { 
      Thread.sleep(2500); 
      Transaction.runVoid(() -> {});  // this will let timer system update sys.time before checking
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertNotEquals( "", cTest.sample() );

    // should be cleared after 0.6 more seconds
    try { 
      Thread.sleep(600); 
      Transaction.runVoid(() -> {}); 
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( "", cTest.sample() );

    // send event
    sGpsEvents.send(ev2);
    assertEquals( ev2.toString(), cTest.sample() );

    // should be cleared after 3.1 seconds
    try { 
      Thread.sleep(3100); 
      Transaction.runVoid(() -> {}); 
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( "", cTest.sample() );
  }
}
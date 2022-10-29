import org.junit.*;
import static org.junit.Assert.*;
import nz.sodium.*;
import nz.sodium.time.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the functions related to the filteredEvents display. */
public class filteredEvents_Test {
  @Test
  public void getFilteredEventsCell_test() {
    // set up linked list of GpsEvent StreamSinks and convert it to an array
    LinkedList<Stream<GpsEvent>> streams = new LinkedList<Stream<GpsEvent>>();
    StreamSink<GpsEvent> sGps0 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps1 = new StreamSink<GpsEvent>();
    StreamSink<GpsEvent> sGps2 = new StreamSink<GpsEvent>();
    streams.add(sGps0);
    streams.add(sGps1);
    streams.add(sGps2);
    Stream<GpsEvent>[] streamsArray = (Stream<GpsEvent>[])streams.toArray(new Stream[0]);

    // create stream sink that will simulate the button click
    StreamSink<Unit> sClicked = new StreamSink<Unit>();

    // create control panel and get cell we want to check
    ControlPanel ctrlPnl = new ControlPanel(sClicked); 
    Cell<String> cTest = myGUI.getFilteredEventsCell(new SecondsTimerSystem(), ctrlPnl, streamsArray);

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 0.0000000001, 60.0000000001 , 0.00);
    GpsEvent ev2 = new GpsEvent("Tracker1", 44.9999999999, 119.9999999999, 0.00);
    GpsEvent ev3 = new GpsEvent("Tracker2", -0.0000000001, 90, 0.00);
    GpsEvent ev4 = new GpsEvent("Tracker0", 45.0000000001, 90, 0.00);
    GpsEvent ev5 = new GpsEvent("Tracker1", 22.5, 59.9999999999, 0.00);
    GpsEvent ev6 = new GpsEvent("Tracker2", 22.5, 120.0000000001, 0.00);

    // send events and check that the cell updates appropriately
    sGps0.send(ev1);
    assertEquals( ev1.toString(), cTest.sample() );
    sGps1.send(ev2);
    assertEquals( ev2.toString(), cTest.sample() );
    sGps2.send(ev3);
    assertEquals( ev3.toString(), cTest.sample() );
    sGps0.send(ev4);
    assertEquals( ev4.toString(), cTest.sample() );
    sGps1.send(ev5);
    assertEquals( ev5.toString(), cTest.sample() );
    sGps2.send(ev6);
    assertEquals( ev6.toString(), cTest.sample() );

    // set values of text fields to new range and simulate button click
    ctrlPnl.latMin.setText("0");
    ctrlPnl.latMax.setText("45");
    ctrlPnl.lonMin.setText("60");
    ctrlPnl.lonMax.setText("120");
    // give it time to catch up, otherwise it sometimes won't detect the newly set text
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // send events and check that the cell updates or doesn't update appropriately
    sGps0.send(ev1);
    assertEquals( ev1.toString(), cTest.sample() );      // within range

    sGps1.send(ev3);
    assertNotEquals( ev3.toString(), cTest.sample() );   // not within range lat left boundary
    assertEquals( ev1.toString(), cTest.sample() ); 

    sGps2.send(ev4);
    assertNotEquals( ev4.toString(), cTest.sample() );   // not within range lat right boundary
    assertEquals( ev1.toString(), cTest.sample() ); 

    sGps0.send(ev2);
    assertEquals( ev2.toString(), cTest.sample() );      // within range

    sGps1.send(ev5);
    assertNotEquals( ev5.toString(), cTest.sample() );   // not within range lon left boundary
    assertEquals( ev2.toString(), cTest.sample() ); 

    sGps2.send(ev6);
    assertNotEquals( ev6.toString(), cTest.sample() );   // not within range lon right boundary
    assertEquals( ev2.toString(), cTest.sample() ); 
  }
}
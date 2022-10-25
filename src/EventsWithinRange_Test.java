import static org.junit.Assert.*;
import org.junit.*;
import nz.sodium.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the EventsWithinRangeComponent. */
public class EventsWithinRange_Test {
  @Test
  public void eventInRange_test() {
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

    // stream sink that we will use to simulate the button click
    StreamSink<Unit> sClicked = new StreamSink<Unit>();

    // create instance of the component
    EventsWithinRangeComponent display = new EventsWithinRangeComponent(streamsArray, null, sClicked);
    ControlPanelComponent ctrlPnl = display.controlPanel;

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 0.0000000001, 60.0000000001 , 0.00);
    GpsEvent ev2 = new GpsEvent("Tracker1", 44.9999999999, 119.9999999999, 0.00);
    GpsEvent ev3 = new GpsEvent("Tracker2", -0.0000000001, 90, 0.00);
    GpsEvent ev4 = new GpsEvent("Tracker0", 45.0000000001, 90, 0.00);
    GpsEvent ev5 = new GpsEvent("Tracker1", 22.5, 59.9999999999, 0.00);
    GpsEvent ev6 = new GpsEvent("Tracker2", 22.5, 120.0000000001, 0.00);

    // get cell we want to check
    Cell<String> cTest = display.cEventString;

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

    // set values of text fields to new range
    ctrlPnl.latMin.setText("0");
    ctrlPnl.latMax.setText("45");
    ctrlPnl.lonMin.setText("60");
    ctrlPnl.lonMax.setText("120");

    // simulate click of the button
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
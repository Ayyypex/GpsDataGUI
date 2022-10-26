import static org.junit.Assert.assertEquals;
import org.junit.*;
import nz.sodium.*;

/** Tests the SimplifiedTrackersComponent. */
public class SimplifiedTrackers_Test {
  @Test
  public void stripAltitude_test() {
    // set up stream sink that we will send an event to
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    
    // set up stream that will strip the altitude off the GpsEvent stream
    Stream<SimpleGpsEvent> sSimpleGps = myGUI.stripAltitude(sGpsEvents);

    // set up cells to hold each field
    Cell<String> cName = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.name )
      .hold("N/A");
    Cell<Double> cLatitude = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.latitude )
      .hold(-1.11);
    Cell<Double> cLongitude = sSimpleGps.map( (SimpleGpsEvent ev) -> ev.longitude )
      .hold(-1.11);    

    // create and send event down stream
    GpsEvent ev = new GpsEvent("Tracker0",  0.00, 1.11, 2.22);
    sGpsEvents.send(ev);

    // check that the fields were carried over correctly
    assertEquals( ev.name, cName.sample() );
    assertEquals( ev.latitude, cLatitude.sample(), 0.00 );
    assertEquals( ev.longitude, cLongitude.sample(), 0.00 );
  }

  @Test
  public void getSimplifiedGpsCells_test() {
    // set up stream sink that we will send an event to
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();

    // get the cells containing simplified tracker info
    Cell<String>[] simplifiedGpsCells = myGUI.getSimplifiedGpsCells(sGpsEvents);

    // create events
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);

    // send and check that the tracker number, latitude, and longitude are as expected 
    sGpsEvents.send(ev1);    
    assertEquals( ev1.getTrackerNumber(), simplifiedGpsCells[0].sample() );
    assertEquals( String.valueOf(ev1.latitude), simplifiedGpsCells[1].sample() );
    assertEquals( String.valueOf(ev1.longitude), simplifiedGpsCells[2].sample() );

    // send and check that the cells update correctly 
    sGpsEvents.send(ev2);
    assertEquals( ev2.getTrackerNumber(), simplifiedGpsCells[0].sample() );
    assertEquals( String.valueOf(ev2.latitude), simplifiedGpsCells[1].sample() );
    assertEquals( String.valueOf(ev2.longitude), simplifiedGpsCells[2].sample() );
  }
}
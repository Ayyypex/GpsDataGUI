import org.junit.*;
import static org.junit.Assert.*;
import nz.sodium.*;
import nz.sodium.time.*;
import java.util.*;

/** Tests the functions related to calculating the distance travelled. */
public class distTravelled_Test {
  @Test
  public void slidingWindow_test1() {
    // create stream sink and events we will send down the stream
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    GpsEvent ev1 = new GpsEvent("", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("", 4.44, 5.55, 6.66);

    // set up parameters
    SecondsTimerSystem sys = new SecondsTimerSystem();
    ControlPanel ctrlPnl = new ControlPanel(null);

    // get sliding window of 5 seconds
    Stream<ArrayList<GpsEvent>> sSlidingWindow = myGUI.getSlidingWindow(sys, ctrlPnl, sGpsEvents, 5);
    Cell<ArrayList<GpsEvent>> cTest = sSlidingWindow.hold(new ArrayList<GpsEvent>());

    // send event
    sGpsEvents.send(ev1);

    // wait 1 second for sliding window to catch up (sliding window updates itself every 1 second)
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});  // this will let timer system update sys.time before checking
      Thread.sleep(5); 
    } catch (InterruptedException e) {}

    // check that the cell updates correctly
    assertTrue( cTest.sample().contains(ev1) );

    // wait 1.5 seconds between events
    try { Thread.sleep(1500); }
    catch (InterruptedException e) {}

    // send event and check that the window contains both events
    sGpsEvents.send(ev2);
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertTrue( cTest.sample().contains(ev1) && cTest.sample().contains(ev2) );

    // wait 3 seconds and check that window contains only the second event
    try { 
      Thread.sleep(3000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertTrue( !cTest.sample().contains(ev1) && cTest.sample().contains(ev2) );
  }

  @Test
  public void slidingWindow_test2() {
    // create stream sink and events we will send down the stream
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    GpsEvent ev1 = new GpsEvent("", -90, 0, 1.1);
    GpsEvent ev2 = new GpsEvent("", 5, 0, 2.2);
    GpsEvent ev3 = new GpsEvent("", 0, -180, 3.3);
    GpsEvent ev4 = new GpsEvent("", 0, 5, 4.44);
    GpsEvent ev5 = new GpsEvent("", 5, -180, 5.5);
    GpsEvent ev6 = new GpsEvent("", -90, 5, 6.6);

    // set up parameters
    StreamSink<Unit> sClicked = new StreamSink<Unit>();
    SecondsTimerSystem sys = new SecondsTimerSystem();
    ControlPanel ctrlPnl = new ControlPanel(sClicked);

    // get sliding window
    Stream<ArrayList<GpsEvent>> sSlidingWindow = myGUI.getSlidingWindow(sys, ctrlPnl, sGpsEvents, 5*60);
    Cell<ArrayList<GpsEvent>> cTest = sSlidingWindow.hold(new ArrayList<GpsEvent>());

    // send events
    sGpsEvents.send(ev1);
    sGpsEvents.send(ev2);
    sGpsEvents.send(ev3);
    sGpsEvents.send(ev4);
    sGpsEvents.send(ev5);
    sGpsEvents.send(ev6);

    // sliding window should contain all the events
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( 6, cTest.sample().size() );
    assertTrue( cTest.sample().contains(ev1) && cTest.sample().contains(ev2) );
    assertTrue( cTest.sample().contains(ev3) && cTest.sample().contains(ev4) );
    assertTrue( cTest.sample().contains(ev5) && cTest.sample().contains(ev6) );

    // set values of control panel's text fields and simulate button click
    ctrlPnl.latMin.setText("0");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("-180");
    ctrlPnl.lonMax.setText("180");
    // give it time to catch up, otherwise it sometimes won't detect the newly set text
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // sliding window should contain 4 of the events
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( 4, cTest.sample().size() );
    assertTrue( cTest.sample().contains(ev2) && cTest.sample().contains(ev3) );
    assertTrue( cTest.sample().contains(ev4) && cTest.sample().contains(ev5) );

    // set values of control panel's text fields and simulate button click
    ctrlPnl.latMin.setText("-90");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("0");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // sliding window should contain 4 of the events
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( 4, cTest.sample().size() );
    assertTrue( cTest.sample().contains(ev1) && cTest.sample().contains(ev2) );
    assertTrue( cTest.sample().contains(ev4) && cTest.sample().contains(ev6) );
    
    // set values of control panel's text fields and simulate button click
    ctrlPnl.latMin.setText("0");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("0");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // sliding window should contain 2 of the events
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( 2, cTest.sample().size() );
    assertTrue( cTest.sample().contains(ev2) && cTest.sample().contains(ev4) );

    // set values of control panel's text fields to default and simulate button click
    ctrlPnl.latMin.setText("-90");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("-180");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // sliding window should contain all the events again
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( 6, cTest.sample().size() );
    assertTrue( cTest.sample().contains(ev1) && cTest.sample().contains(ev2) );
    assertTrue( cTest.sample().contains(ev3) && cTest.sample().contains(ev4) );
    assertTrue( cTest.sample().contains(ev5) && cTest.sample().contains(ev6) );
  }

  @Test
  // Online calculator used: https://www.apsalin.com/geodetic-to-cartesian-on-ellipsoid/
  public void convertToECEF_test() {
    Double lat = 39.0123456789;
    Double lon = 116.0123456789;
    Double alt = 40.0123456789;   // in feet

    // convert geodetic coordinates to ECEF coordinates
    Double[] ECEF = myGUI.convertToECEF(lat, lon, alt);

    // check results with error range of 1m
    assertEquals( -2176366.506, ECEF[0], 1 );
    assertEquals( 4459773.401, ECEF[1], 1 );
    assertEquals( 3993389.735, ECEF[2], 1 );

    // another one, with negative values
    lat = -39.0123456789;
    lon = -116.0123456789;
    alt = -40.0123456789;   // in feet
    ECEF = myGUI.convertToECEF(lat, lon, alt);

    assertEquals( -2176358.194, ECEF[0], 1 );
    assertEquals( -4459756.368, ECEF[1], 1 );
    assertEquals( -3993374.38, ECEF[2], 1 );
  }

  @Test
  // Online calculator used: http://cosinekitty.com/compass.html
  public void calcEuclidean_test() {
    // define point coordinates
    Double lat1 = 39.001;
    Double lon1 = 115.001;
    Double alt1 = 40.0;     // in feet

    Double lat2 = 39.002;
    Double lon2 = 115.002;
    Double alt2 = 42.0;     // in feet

    // calculate distance
    Double dist = myGUI.calcEuclidean(lat1, lon1, alt1, lat2, lon2, alt2);
  
    // check result with error range of 1m
    assertEquals( 141, dist, 1 );

    // just change the altitude by 1m, distance betw/ points should only be 1m
    lat2 = 39.001;
    lon2 = 115.001;
    alt2 = 43.2804;
    dist = myGUI.calcEuclidean(lat1, lon1, alt1, lat2, lon2, alt2);
    assertEquals( 1, dist, 0.01 );
  }

  @Test
  public void calcDistance_test1() {
    // empty event list
    ArrayList<GpsEvent> eventList = new ArrayList<GpsEvent>();
    assertEquals( 0, myGUI.calcDistance(eventList) );
    
    // single event in list
    eventList.add(new GpsEvent("", 0, 0, 0));
    assertEquals( 0, myGUI.calcDistance(eventList) );

    // two different-positioned events in list, should get a nonzero result
    eventList.add(new GpsEvent("", 1, 1, 1));
    assertNotEquals( 0, myGUI.calcDistance(eventList) );
  }

  @Test
  public void calcDistance_test2() {
    // two events but same position should return 0
    ArrayList<GpsEvent> eventList = new ArrayList<GpsEvent>();
    eventList.add(new GpsEvent("", 1, 1, 1));
    eventList.add(new GpsEvent("", 1, 1, 1));
    assertEquals( 0, myGUI.calcDistance(eventList) );

    // add different positioned event and track the distance
    eventList.add(new GpsEvent("", 1.001, 1.001, 1));
    Double dist = myGUI.calcEuclidean(1.0, 1.0, 1.0, 1.001, 1.001, 1.0);    
    assertEquals( dist, myGUI.calcDistance(eventList), 1 );

    // move back to original position, same distance travelled twice
    eventList.add(new GpsEvent("", 1, 1, 1));
    dist *= 2;    
    assertEquals( dist, myGUI.calcDistance(eventList), 1 );

    // final check that the function is adding distances correctly
    eventList.add(new GpsEvent("", 0.98, 0.98, 1));
    dist += myGUI.calcEuclidean(1.0, 1.0, 1.0, 0.98, 0.98, 1.0);       
    assertEquals( dist, myGUI.calcDistance(eventList), 1 );
  }

  @Test
  public void getSimplifiedGpsCells_test() {
    // create stream sink and events
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    GpsEvent ev1 = new GpsEvent("", -0.1, 0.1, 1);
    GpsEvent ev2 = new GpsEvent("", 0.1, -0.1, 1);
    GpsEvent ev3 = new GpsEvent("", 0.10001, 0.1001, 1);

    // prepare control panel and timer system
    StreamSink<Unit> sClicked = new StreamSink<Unit>();
    ControlPanel ctrlPnl = new ControlPanel(sClicked);
    SecondsTimerSystem sys = new SecondsTimerSystem();

    // set up 4 distances we want to check for
    int dist1 = (int) Math.ceil( myGUI.calcEuclidean(ev1.latitude, ev1.longitude, ev1.altitude, 
      ev2.latitude, ev2.longitude, ev2.altitude) );
    int dist2 = (int) Math.ceil( myGUI.calcEuclidean(ev1.latitude, ev1.longitude, ev1.altitude, 
      ev3.latitude, ev3.longitude, ev3.altitude) );
    int dist3 = (int) Math.ceil( myGUI.calcEuclidean(ev2.latitude, ev2.longitude, ev2.altitude, 
      ev3.latitude, ev3.longitude, ev3.altitude) );
    int dist4 = dist1 + dist3;

    // get cell we want to test
    Cell<String> cTest = myGUI.getDistTravelledCell(sys, ctrlPnl, sGpsEvents);

    // send events
    sGpsEvents.send(ev1);
    sGpsEvents.send(ev2);
    sGpsEvents.send(ev3);

    // wait 1 second for sliding window to catch up (sliding window updates itself every 1 second)
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});  // this will let timer system update sys.time before checking
      Thread.sleep(5); 
    } catch (InterruptedException e) {}

    // sliding window should contain all 3 events
    assertEquals( String.valueOf(dist4) + " m", cTest.sample() );

    // set values of control panel's text fields and simulate button click
    ctrlPnl.latMin.setText("-90");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("-180");
    ctrlPnl.lonMax.setText("0.1");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // sliding window should only contain ev1 and ev2
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( String.valueOf(dist1) + " m", cTest.sample() );

    // set new range
    ctrlPnl.latMin.setText("-90");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("0.1");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // now sliding window should only contain ev1 and ev3
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( String.valueOf(dist2) + " m", cTest.sample() );

    // set new range
    ctrlPnl.latMin.setText("0");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("-180");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // now sliding window should only contain ev2 and ev3
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( String.valueOf(dist3) + " m", cTest.sample() );

    // set default restrictions
    ctrlPnl.latMin.setText("-90");
    ctrlPnl.latMax.setText("90");
    ctrlPnl.lonMin.setText("-180");
    ctrlPnl.lonMax.setText("180");
    try { Thread.sleep(5); } catch (InterruptedException e) {}
    sClicked.send(Unit.UNIT);

    // now sliding window should contain all 3 events again
    try { 
      Thread.sleep(1000); 
      Transaction.runVoid(() -> {});
      Thread.sleep(5); 
    } catch (InterruptedException e) {}
    assertEquals( String.valueOf(dist4) + " m", cTest.sample() );
  }
}
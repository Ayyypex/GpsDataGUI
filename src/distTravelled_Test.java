import static org.junit.Assert.*;
import org.junit.*;
import nz.sodium.*;
import nz.sodium.time.*;
import java.util.*;

// supress warnings from converting the linked list of streams to an array
@SuppressWarnings("unchecked")

/** Tests the functions related to calculating the distance travelled. */
public class distTravelled_Test {
  @Test
  public void slidingWindow_test1() {
    // create stream sink and events we will send down the stream
    StreamSink<GpsEvent> sGpsEvents = new StreamSink<GpsEvent>();
    GpsEvent ev1 = new GpsEvent("Tracker0", 1.11, 2.22, 3.33);
    GpsEvent ev2 = new GpsEvent("Tracker1", 4.44, 5.55, 6.66);

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
    GpsEvent ev1 = new GpsEvent("Tracker0", -90, 0, 1.1);
    GpsEvent ev2 = new GpsEvent("Tracker1", 5, 0, 2.2);
    GpsEvent ev3 = new GpsEvent("Tracker2", 0, -180, 3.3);
    GpsEvent ev4 = new GpsEvent("Tracker3", 0, 5, 4.44);
    GpsEvent ev5 = new GpsEvent("Tracker4", 5, -180, 5.5);
    GpsEvent ev6 = new GpsEvent("Tracker5", -90, 5, 6.6);

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
    // give it time to catch up, otherwise it won't detect the newly set text
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
  public void convertToECEF_test() {
    //
  }

  @Test
  public void calcEuclidean_test() {
    //
  }

  @Test
  public void calcDistance_test() {
    //
  }

  @Test
  public void getSimplifiedGpsCells_test() {
    //
  }
}
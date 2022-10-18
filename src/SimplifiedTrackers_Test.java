import static org.junit.Assert.assertEquals;
import org.junit.*;
import nz.sodium.*;

/** Tests the simplifiedTrackersComponent. */
public class SimplifiedTrackers_Test {
  @Test
  public void stripAltitude_test() {
    StreamSink<GpsEvent> GpsStream = new StreamSink<GpsEvent>();
    
    Stream<SimpleGpsEvent> simpleGpsStream = SimplifiedTrackersComponent.stripAltitude(GpsStream);
    GpsEvent event = new GpsEvent("Tracker0",  0.00, 1.11, 2.22);
    GpsStream.send(event);

    simpleGpsStream.listen( (SimpleGpsEvent ev) -> {
      assertEquals(event.name, ev.name);
      assertEquals(event.latitude, ev.latitude, 0.00);
      assertEquals(event.longitude, ev.longitude, 0.00);
    });
  }

  @Test
  public void singleStream_test() {
    assertEquals(1, 1);
  }

  @Test
  public void doubleStream_test() {
    assertEquals(1, 1);
  }
}
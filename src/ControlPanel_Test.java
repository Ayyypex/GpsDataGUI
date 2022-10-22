import static org.junit.Assert.*;
import org.junit.*;
import nz.sodium.*;
import swidgets.*;

/** Tests the ControlPanelComponent. */
public class ControlPanel_Test {
  @Test
  public void validLatitudeInput_test() {
    // list of valid latitude input
    String[][] input =  {
      {"-90", "90"}, 
      {"-89.99999999015409", "89.984123184312"},
      {"-89.99999999015409", "-89.99999998"},
      {"89.9999", "90"},
      {"90.00", "90.0"},
      {"-90", "-90"},
    };
    
    // test each input
    for ( int i=0; i < input.length; i++ ) {
      //System.out.println("Output is not as expected: " + i);
      assertTrue( ControlPanelComponent.checkCoord(input[i][0], input[i][1], "lat") );
    }
  }

  @Test
  public void invalidLatitudeInput_test() {
    // list of invalid latitude input
    String[][] input =  {
      {"-90.000001", "90"}, 
      {"-90", "90.000001"}, 
      {"0", "-89.984123184312"},
      {"89", "91"},
      {"0.001", "0.0001"},
      {"-90a", "-90"},
      {"-172.0659", "122.1"},
      {"abc", "def"},
    };
    
    for ( int i=0; i < input.length; i++ ) {
      //System.out.println("Output is not as expected: " + i);
      assertFalse( ControlPanelComponent.checkCoord(input[i][0], input[i][1], "lat") );
    }
  }

  @Test
  public void validLongitudeInput_test() {
    // list of valid longitude input
    String[][] input =  {
      {"-90", "90"}, 
      {"-89.99999999015409", "89.984123184312"},
      {"-89.99999999015409", "-89.99999998"},
      {"89.9999", "90"},
      {"90.00", "90.0"},
      {"-90", "-90"},
      {"-180", "180"},
      {"-179.13", "-179"},
    };
    
    for ( int i=0; i < input.length; i++ ) {
      //System.out.println("Output is not as expected: " + i);
      assertTrue( ControlPanelComponent.checkCoord(input[i][0], input[i][1], "long") );
    }
  }

  @Test
  public void invalidLongitudeInput_test() {
    // list of invalid longitude input
    String[][] input =  {
      {"0", "-89.984123184312"},
      {"0.001", "0.0001"},
      {"-90a", "-90"},
      {"-181", "180"},
      {"-180", "181"},
      {"-179", "-179.13"},
      {"abc", "def"},
    };
    
    for ( int i=0; i < input.length; i++ ) {
      //System.out.println("Output is not as expected: " + i);
      assertFalse( ControlPanelComponent.checkCoord(input[i][0], input[i][1], "lon") );
    }
  }

  @Test
  public void applyRestrictions_test() {
    // We are testing that the cells are updated correctly and that
    // the text fields are cleared too. We will assume the input that
    // we send is valid.

    // stream sink that we will use to simulate the button click
    StreamSink<Unit> sClicked = new StreamSink<Unit>();

    // set up stream that will fire an empty string upon the simulated button click
    Stream<String> sClear = sClicked.map(u -> "");

    // set up STextFields
    STextField latMin = new STextField(sClear, "0");
    STextField latMax = new STextField(sClear, "45");
    STextField lonMin = new STextField(sClear, "60");
    STextField lonMax = new STextField(sClear, "120");
    
    // set up cells to hold the lat/lon values
    Cell<String> cLatMin = sClicked.snapshot(latMin.text, (u, lat1) -> lat1)
      .hold("-90.0");
    Cell<String> cLatMax = sClicked.snapshot(latMax.text, (u, lat2) -> lat2)
      .hold("90.0");
    Cell<String> cLonMin = sClicked.snapshot(lonMin.text, (u, lon1) -> lon1)
      .hold("-180.0");
    Cell<String> cLonMax = sClicked.snapshot(lonMax.text, (u, lon2) -> lon2)
      .hold("180.0");

    // check initial values are set correctly
    assertEquals("-90.0", cLatMin.sample());
    assertEquals("90.0", cLatMax.sample());
    assertEquals("-180.0", cLonMin.sample());
    assertEquals("180.0", cLonMax.sample());
    assertEquals("0", latMin.text.sample());
    assertEquals("45", latMax.text.sample());
    assertEquals("60", lonMin.text.sample());
    assertEquals("120", lonMax.text.sample());

    // simulate click of the button
    sClicked.send(Unit.UNIT);

    // check that cells are set to values from text field
    assertEquals("0", cLatMin.sample());
    assertEquals("45", cLatMax.sample());
    assertEquals("60", cLonMin.sample());
    assertEquals("120", cLonMax.sample());

    // check that text fields are now cleared
    assertEquals("", latMin.text.sample());
    assertEquals("", latMax.text.sample());
    assertEquals("", lonMin.text.sample());
    assertEquals("", lonMax.text.sample());
  }
}
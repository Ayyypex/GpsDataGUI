import org.junit.*;
import static org.junit.Assert.*;
import nz.sodium.*;

/** Tests the ControlPanel class. */
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
      assertTrue( ControlPanel.checkCoord(input[i][0], input[i][1], "lat") );
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
      assertFalse( ControlPanel.checkCoord(input[i][0], input[i][1], "lat") );
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
      assertTrue( ControlPanel.checkCoord(input[i][0], input[i][1], "long") );
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
      assertFalse( ControlPanel.checkCoord(input[i][0], input[i][1], "lon") );
    }
  }

  @Test
  public void applyRestrictions_test() {
    // create stream sink that we will use to simulate the button click
    StreamSink<Unit> sClicked = new StreamSink<Unit>();

    // create instance of control panel
    ControlPanel ctrlPnl = new ControlPanel(sClicked);

    // init test values
    String latMinVal = "0";
    String latMaxVal = "45";
    String lonMinVal = "60";
    String lonMaxVal = "120";

    // set values of text fields
    ctrlPnl.latMin.setText(latMinVal);
    ctrlPnl.latMax.setText(latMaxVal);
    ctrlPnl.lonMin.setText(lonMinVal);
    ctrlPnl.lonMax.setText(lonMaxVal);

    // check initial values are set correctly
    assertEquals("-90.0", ctrlPnl.cLatMin.sample());
    assertEquals("90.0", ctrlPnl.cLatMax.sample());
    assertEquals("-180.0", ctrlPnl.cLonMin.sample());
    assertEquals("180.0", ctrlPnl.cLonMax.sample());
    assertEquals(latMinVal, ctrlPnl.latMin.text.sample());
    assertEquals(latMaxVal, ctrlPnl.latMax.text.sample());
    assertEquals(lonMinVal, ctrlPnl.lonMin.text.sample());
    assertEquals(lonMaxVal, ctrlPnl.lonMax.text.sample());

    // simulate click of the button
    sClicked.send(Unit.UNIT);

    // check that cells are set to values from text field
    assertEquals(latMinVal, ctrlPnl.cLatMin.sample());
    assertEquals(latMaxVal, ctrlPnl.cLatMax.sample());
    assertEquals(lonMinVal, ctrlPnl.cLonMin.sample());
    assertEquals(lonMaxVal, ctrlPnl.cLonMax.sample());

    // check that text fields are now cleared
    assertEquals("", ctrlPnl.latMin.text.sample());
    assertEquals("", ctrlPnl.latMax.text.sample());
    assertEquals("", ctrlPnl.lonMin.text.sample());
    assertEquals("", ctrlPnl.lonMax.text.sample());
  }
}
import nz.sodium.*;
import swidgets.*;
import javax.swing.*;
import java.awt.*;

/** 
 * Control panel consisting of a latitude input field, latitude input field,
 * and a button to set the restrictions.
 */
public class ControlPanel extends JPanel {

  public Cell<String> cLatMin;
  public Cell<String> cLatMax;
  public Cell<String> cLonMin;
  public Cell<String> cLonMax;
  public STextField latMin;
  public STextField latMax;
  public STextField lonMin;
  public STextField lonMax;

  /**
   * Constructs the control panel.
   * 
   * @param sTest   A Unit StreamSink used for testing the component.
   */
  public ControlPanel(StreamSink<Unit> sTest) {
    // configure main panel
    this.setLayout(new GridBagLayout());
    this.setBorder(BorderFactory.createEtchedBorder());
    
    // loop() needs to be run in explicit Transaction
    Transaction.runVoid(() -> {
      // define business rule
      Rule validInputRule = new Rule( (String lat1, String lat2, String lon1, String lon2) -> 
        checkCoord(lat1, lat2, "lat") && checkCoord(lon1, lon2, "lon") );

      // create CellLoop so the apply button also clears the text fields
      CellLoop<Boolean> cValidInput = new CellLoop<>();

      // create SButton that will only be clickable if business rule is met
      SButton apply = new SButton("Set Range", cValidInput);
      apply.setFocusable(false);

      // set up sClicked, we will pass a StreamSink if we wish to test this
      Stream<Unit> sClicked = apply.sClicked;
      if ( sTest != null ) {
        sClicked = sTest;
      }

      // create stream that will fire an empty string upon the button click
      Stream<String> sClear = sClicked.map((Unit u) -> "");

      // create STextFields
      latMin = new STextField(sClear, "");
      latMax = new STextField(sClear, "");
      lonMin = new STextField(sClear, "");
      lonMax = new STextField(sClear, "");

      // set cell to hold the business rule validity
      cValidInput.loop(
        validInputRule.reify(latMin.text, latMax.text, lonMin.text, lonMax.text) );
      
      // set up cells to hold the lat/lon values upon the click of the button
      cLatMin = sClicked.snapshot(latMin.text, (Unit u, String lat1) -> lat1)
        .hold("-90.0");
      cLatMax = sClicked.snapshot(latMax.text, (Unit u, String lat2) -> lat2)
        .hold("90.0");
      cLonMin = sClicked.snapshot(lonMin.text, (Unit u, String lon1) -> lon1)
        .hold("-180.0");
      cLonMax = sClicked.snapshot(lonMax.text, (Unit u, String lon2) -> lon2)
        .hold("180.0");

      // create header labels
      JLabel latHeader1 = new JLabel("Latitude", SwingConstants.RIGHT);
      JLabel latHeader2 = new JLabel("Latitude", SwingConstants.RIGHT);
      JLabel lonHeader1 = new JLabel("Longitude", SwingConstants.RIGHT);
      JLabel lonHeader2 = new JLabel("Longitude", SwingConstants.RIGHT);
      JLabel minHeader = new JLabel("Min: ");
      JLabel maxHeader = new JLabel("Max: ");
      JLabel currMinHeader = new JLabel("Current Min: ", SwingConstants.CENTER);
      JLabel currMaxHeader = new JLabel("Current Max: ", SwingConstants.CENTER);

      // create SLabels to display the current restrictions
      SLabel latMinLabel = new SLabel(cLatMin);
      SLabel latMaxLabel = new SLabel(cLatMax);
      SLabel lonMinLabel = new SLabel(cLonMin);
      SLabel lonMaxLabel = new SLabel(cLonMax);

      // configure SLabels
      latMinLabel.setHorizontalAlignment(SwingConstants.CENTER);
      latMaxLabel.setHorizontalAlignment(SwingConstants.CENTER);
      lonMinLabel.setHorizontalAlignment(SwingConstants.CENTER);
      lonMaxLabel.setHorizontalAlignment(SwingConstants.CENTER);
      Font valueFont = new Font("Courier", Font.PLAIN, 14);
      latMinLabel.setFont(valueFont);
      latMaxLabel.setFont(valueFont);
      lonMinLabel.setFont(valueFont);
      lonMaxLabel.setFont(valueFont);

      // define insets
      Insets minInsets = new Insets(5, 5, 5, 5);

      // add components to control panel
      myGUI.addComponent(this, minHeader, 0, 1, 1, 1, minInsets);
      myGUI.addComponent(this, maxHeader, 0, 2, 1, 1, minInsets);
      myGUI.addComponent(this, latHeader1, 1, 0, 1, 1, minInsets);
      myGUI.addComponent(this, latMin, 1, 1, 1, 1, minInsets);
      myGUI.addComponent(this, latMax, 1, 2, 1, 1, minInsets);
      myGUI.addComponent(this, lonHeader1, 2, 0, 1, 1, minInsets);
      myGUI.addComponent(this, lonMin, 2, 1, 1, 1, minInsets);
      myGUI.addComponent(this, lonMax, 2, 2, 1, 1, minInsets);
      myGUI.addComponent(this, apply, 3, 1, 2, 1, minInsets);

      // add separator
      JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
      sep.setForeground(Color.black);
      sep.setBackground(Color.black);
      myGUI.addComponent(this, sep, 4, 0, 3, 1, minInsets);

      // add current restrictions
      myGUI.addComponent(this, currMinHeader, 5, 1, 1, 1, minInsets);
      myGUI.addComponent(this, currMaxHeader, 5, 2, 1, 1, minInsets);
      sep = new JSeparator(SwingConstants.HORIZONTAL);
      sep.setForeground(Color.lightGray);
      sep.setBackground(Color.lightGray);
      myGUI.addComponent(this, sep, 6, 0, 3, 1, minInsets);
      myGUI.addComponent(this, latHeader2, 7, 0, 1, 1, minInsets);
      myGUI.addComponent(this, latMinLabel, 7, 1, 1, 1, minInsets);
      myGUI.addComponent(this, latMaxLabel, 7, 2, 1, 1, minInsets);
      sep = new JSeparator(SwingConstants.HORIZONTAL);
      sep.setForeground(Color.lightGray);
      sep.setBackground(Color.lightGray);
      myGUI.addComponent(this, sep, 8, 0, 3, 1, minInsets);
      myGUI.addComponent(this, lonHeader2, 9, 0, 1, 1, minInsets);
      myGUI.addComponent(this, lonMinLabel, 9, 1, 1, 1, minInsets);
      myGUI.addComponent(this, lonMaxLabel, 9, 2, 1, 1, minInsets);  
    });
  }

  /**
   * Checks whether a pair of strings are valid coordinates depending on its type.
   * 
   * @param minStr  The string representing the min value in a range.
   * @param maxStr  The string representing the max value in a range.
   * @param type    The type of string the input should be. 'lat' or 'lon'
   * @return        true if input is within it's type's range and minStr <= maxStr.
   */
  public static boolean checkCoord(String minStr, String maxStr, String type) {
    Double min;
    Double max;

    // try parsing the input as a double, return false if not a double
    try { 
      min = Double.parseDouble(minStr); 
      max = Double.parseDouble(maxStr); 
    } 
    catch (NumberFormatException e) { 
      return false; 
    }
    
    // min can't be bigger than max
    if ( min > max ) {
      return false;
    }

    // return whether coordinates are in range
    if ( type.equals("lat") ) {
      return (min >= -90.0 && min <= 90.0 && max >= -90.0 && max <= 90.0);
    }
    return (min >= -180.0 && min <= 180.0 && max >= -180.0 && max <= 180.0);
  }
}
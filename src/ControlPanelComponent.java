import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;

/** 
 * Control panel consisting of a latitude input field, latitude input field,
 * and a button.
 */
public class ControlPanelComponent extends JPanel {

  public STextField latMin;
  public STextField latMax;
  public STextField lonMin;
  public STextField lonMax;

  public Cell<String> cLatMin;
  public Cell<String> cLatMax;
  public Cell<String> cLonMin;
  public Cell<String> cLonMax;

  public CellLoop<Boolean> cValidInput;

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
    catch(NumberFormatException e) { 
      return false; 
    }
    
    // min can't be bigger than max
    if ( min > max ) {
      return false;
    }

    // return whether value is in range
    if ( type.equals("lat") ) {
      return (min >= -90.0 && min <= 90.0 && max >= -90.0 && max <= 90.0);
    }
    return (min >= -180.0 && min <= 180.0 && max >= -180.0 && max <= 180.0);
  }

  /** Constructs the control panel. */
  public ControlPanelComponent() {
    // configure main panel
    this.setLayout(new GridBagLayout());
    this.setBorder(BorderFactory.createEtchedBorder());
    this.setPreferredSize(new Dimension(500, 300));
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    
    // define business rule
    Rule validInputRule = new Rule( (lat1, lat2, lon1, lon2) -> 
      checkCoord(lat1, lat2, "lat") && checkCoord(lon1, lon2, "lon") );
    
    // loop() needs to be run in explicit Transaction
    Transaction.runVoid(() -> {

      // create CellLoop so the apply button also clears the text fields
      cValidInput = new CellLoop<>();

      // set up SButton that will only be clickable if business rule is met
      SButton apply = new SButton("Apply", cValidInput);
      apply.setFocusable(false);

      // set up stream that will fire an empty string upon the button click
      Stream<String> sClear = apply.sClicked.map(u -> "");

      // set up STextFields
      latMin = new STextField(sClear, "");
      latMax = new STextField(sClear, "");
      lonMin = new STextField(sClear, "");
      lonMax = new STextField(sClear, "");

      // set cell to hold the business rule validity
      cValidInput.loop(
        validInputRule.reify(latMin.text, latMax.text, lonMin.text, lonMax.text) );
      
      // set up cells to hold the lat/lon values upon the click of the button
      cLatMin = apply.sClicked.snapshot(latMin.text, (u, lat1) -> lat1)
        .hold("-90.0");
      cLatMax = apply.sClicked.snapshot(latMax.text, (u, lat2) -> lat2)
        .hold("90.0");
      cLonMin = apply.sClicked.snapshot(lonMin.text, (u, lon1) -> lon1)
        .hold("-180.0");
      cLonMax = apply.sClicked.snapshot(lonMax.text, (u, lon2) -> lon2)
        .hold("180.0");

      // set up Slabels to display the current restrictions
      SLabel latMinLabel = new SLabel(cLatMin);
      SLabel latMaxLabel = new SLabel(cLatMax);
      SLabel lonMinLabel = new SLabel(cLonMin);
      SLabel lonMaxLabel = new SLabel(cLonMax);

      // set up header labels
      JLabel latHeader1 = new JLabel("Latitude");
      JLabel latHeader2 = new JLabel("Latitude");
      JLabel lonHeader1 = new JLabel("Longitude");
      JLabel lonHeader2 = new JLabel("Longitude");
      JLabel minHeader = new JLabel("Min: ");
      JLabel maxHeader = new JLabel("Max: ");
      JLabel currMinHeader = new JLabel("Current Min: ");
      JLabel currMaxHeader = new JLabel("Current Max: ");

      // configure spacing and add each element to subpanel
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;  // column
      c.gridy=0;  // row
      this.add(minHeader, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;  
      c.gridy=0;  
      this.add(maxHeader, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=0;
      c.gridy=1;
      this.add(latHeader1, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;
      c.gridy=1;
      this.add(latMin, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;
      c.gridy=1;
      this.add(latMax, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=0;
      c.gridy=2;
      this.add(lonHeader1, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;
      c.gridy=2;
      this.add(lonMin, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;
      c.gridy=2;
      this.add(lonMax, c);
      
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridwidth = 2;
      c.gridx=1;
      c.gridy=3;
      this.add(apply, c);

      JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
      sep.setForeground(Color.black);
      sep.setBackground(Color.black);
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridy=4;
      this.add(sep, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;  
      c.gridy=5;  
      this.add(currMinHeader, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;
      c.gridy=5;
      this.add(currMaxHeader, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=0;
      c.gridy=6;
      this.add(latHeader2, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;
      c.gridy=6;
      this.add(latMinLabel, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;
      c.gridy=6;
      this.add(latMaxLabel, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=0;
      c.gridy=7;
      this.add(lonHeader2, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=1;
      c.gridy=7;
      this.add(lonMinLabel, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx=2;
      c.gridy=7;
      this.add(lonMaxLabel, c);      
    });
  }
}
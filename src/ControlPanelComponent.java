import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;

/** 
 * Control panel consisting of a latitude input field, latitude input field,
 * and a button.
 */
public class ControlPanelComponent extends JPanel {

  /**
   * Checks whether a pair of strings are valid coordinates depending on its type.
   * 
   * @param minStr  The string representing the min value in a range.
   * @param maxStr  The string representing the max value in a range.
   * @param type    The type of string the input should be. 'lat' or 'lon'
   * @return        true if input is within it's type's range and minStr <= maxStr.
   */
  public static boolean checkCoordinates(String minStr, String maxStr, String type) {
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

  // temporary, just testing it
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.add( new ControlPanelComponent() );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize( new Dimension(700, 400) );
    frame.setVisible(true);
  }

  boolean Testing = true;
  public Cell<String> latitudeCell;
  public Cell<String> longitudeCell;

  /** Constructs the control panel. */
  public ControlPanelComponent() {
    // configure main panel
    this.setLayout(new GridBagLayout());

    JPanel panel = new JPanel( new GridLayout(3,3) );

    // set up STextFields
    STextField latitudeMin = new STextField("");
    STextField latitudeMax = new STextField("");
    STextField longitudeMin = new STextField("");
    STextField longitudeMax = new STextField("");
    
    // define business rule
    Rule validInputRule = new Rule( (latMin, latMax, lonMin, lonMax) -> 
      checkCoordinates(latMin, latMax, "lat") && checkCoordinates(lonMin, lonMax, "lon") );
    
    // create cell that holds the business rule validity
    Cell<Boolean> validInput = validInputRule.reify(latitudeMin.text, latitudeMax.text, longitudeMin.text, longitudeMax.text);

    // set up SButton that will only be clickable if business rule is met
    SButton apply = new SButton("Apply", (validInput));
    apply.setFocusable(false);

    //
    apply.sClicked.listen((e) -> System.out.println(e) );

    // add to main panel
    panel.add(latitudeMin);
    panel.add(latitudeMax);
    panel.add(longitudeMin);
    panel.add(longitudeMax);
    panel.add(apply);

    this.add(panel);
  }
}
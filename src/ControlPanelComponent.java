import javax.swing.*;
import java.awt.*;
import nz.sodium.*;
import swidgets.*;

/** 
 * Control panel consisting of a latitude input field, latitude input field,
 * and a button.
 */
public class ControlPanelComponent extends JPanel {

  public static boolean isValidLat(String latitude) {
    // check if all numbers + decimal
    Double lat;
    try 
    {
      lat = Double.parseDouble(latitude);

    } 
    catch(NumberFormatException e) 
    {
      return false;
    }
    
    return (lat >= -90.0 && lat <= 90.0);
  }

  public static boolean isValidLong(String latitude) {
    // check if all numbers + decimal
    Double longitude;
    try 
    {
      longitude = Double.parseDouble(latitude);

    } 
    catch(NumberFormatException e) 
    {
      return false;
    }
    
    return (longitude >= -180.0 && longitude <= 180.0);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.add( new ControlPanelComponent() );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);        // place gui window at center of screen
    frame.setSize( new Dimension(700, 400) );
    frame.setVisible(true);
  }

  boolean Testing = true;
  public Cell<String> latitudeCell;
  public Cell<String> longitudeCell;

  /** 
   * Constructs the control panel
   */
  public ControlPanelComponent() {
    this.setLayout(new GridBagLayout());

   
    
    STextField latitudeField = new STextField("");
    STextField longitudeField = new STextField("");
    //Stream<String> sText = new S
    



    Rule r = new Rule((lat,lon) -> isValidLat(lat) && isValidLong(lon));
    Cell<Boolean> valid = r.reify(latitudeField.text, longitudeField.text);

    SButton apply = new SButton("Apply", valid);
    apply.setFocusable(false);


    this.add(latitudeField);
    this.add(longitudeField);
    this.add(apply);
    
  }
}
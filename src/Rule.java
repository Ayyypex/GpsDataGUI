import nz.sodium.*;

/** Rule encapsulates a business rule. */
public class Rule {
  public final Lambda4<String,String,String,String,Boolean> f;

  /** Sets the rule's lambda function. */
  public Rule(Lambda4<String,String,String,String,Boolean> f) {
    this.f = f;
  }
  
  /**
   * Applies the lambda function to four string cells, to produce a fifth boolean cell.
   * 
   * @param latitude    The latitude string cell
   * @param longitude   The longitude string cell
   * @return            Boolean cell that will depend on both cells
   */
  public Cell<Boolean> reify(Cell<String> latMin, Cell<String> latMax, Cell<String> lonMin, Cell<String> lonMax) {
    return latMin.lift(latMax, lonMin, lonMax, f);
  }
}
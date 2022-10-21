import nz.sodium.*;

public class Rule {
  public final Lambda2<String,String,Boolean> f;

  public Rule(Lambda2<String,String,Boolean> f) {
    this.f = f;
  }
  
  public Cell<Boolean> reify(Cell<String> latitude, Cell<String> longitude) {
    return latitude.lift(longitude, f);
  }

  public Rule and(Rule other) {
    return new Rule(
      (d, r) -> this.f.apply(d, r) && other.f.apply(d, r)
    );
  }
}
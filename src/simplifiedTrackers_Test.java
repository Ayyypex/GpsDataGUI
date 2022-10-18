import static org.junit.Assert.assertEquals;

/** Tests the simplifiedTrackersComponent. */
public class simplifiedTrackers_Test {
  public static void checkSingleStream_test() {
    assertEquals(1, 2);
  }

  public static void checkDoubleStream_test() {
    assertEquals(1, 1);
  }

  public static void main(String[] args) {
    // run all tests
    checkSingleStream_test();
    checkDoubleStream_test();

    System.out.println("All tests passed.");
  }
}
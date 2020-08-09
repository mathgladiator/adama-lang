/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class TestClassTests {
  @Test
  public void coverage() throws Exception {
    final var tc = new TestClass("Z");
    tc.addTest(new TestFile("Demo", "Bomb", true));
    tc.addTest(new TestFile("Operational", "Goodwill", false));
    final var root = new File("./test_data");
    tc.finish(root);
    final var generated = new File(root, "GeneratedZTests.java");
    Assert.assertTrue(generated.exists());
    generated.delete();
  }
}

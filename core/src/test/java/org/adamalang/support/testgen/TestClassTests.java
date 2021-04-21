/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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

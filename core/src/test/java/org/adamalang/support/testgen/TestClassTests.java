/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

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

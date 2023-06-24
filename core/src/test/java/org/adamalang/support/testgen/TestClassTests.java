/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

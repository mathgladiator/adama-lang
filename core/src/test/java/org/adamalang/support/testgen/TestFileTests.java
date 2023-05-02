/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class TestFileTests {
  @Test
  public void happy() {
    final var tst = new TestFile("Z", "x", true);
    Assert.assertEquals("Z_x_success.a", tst.filename());
    final var tst2 = TestFile.fromFilename(tst.filename());
    Assert.assertEquals("Z", tst.clazz);
    Assert.assertEquals("x", tst.name);
    Assert.assertTrue(tst2.success);
  }

  @Test
  public void sad() {
    final var tst = new TestFile("Z", "x", false);
    Assert.assertEquals("Z_x_failure.a", tst.filename());
    final var tst2 = TestFile.fromFilename(tst.filename());
    Assert.assertEquals("Z", tst2.clazz);
    Assert.assertEquals("x", tst2.name);
    Assert.assertFalse(tst2.success);
  }

  @Test
  public void failure() {
    try {
      new TestFile("Z", "x_x", false);
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex.getMessage().contains("underscore"));
    }
  }
}

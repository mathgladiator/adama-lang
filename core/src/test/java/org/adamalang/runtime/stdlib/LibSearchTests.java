/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibSearchTests {
  @Test
  public void flow() {
    Assert.assertTrue(LibSearch.test("", "x y z"));
    Assert.assertTrue(LibSearch.test("x y", "x y z"));
    Assert.assertFalse(LibSearch.test("t", "x y z"));
    Assert.assertTrue(LibSearch.test("x z", "x y z"));
  }
}

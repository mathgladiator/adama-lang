/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibStringTests {
  @Test
  public void compare() {
    Assert.assertEquals(0, LibString.compare(null, null));
    Assert.assertEquals(0, LibString.compare("x", "x"));
    Assert.assertEquals(-1, LibString.compare("x", "y"));
    Assert.assertEquals(1, LibString.compare("y", "x"));
    Assert.assertEquals(1, LibString.compare("x", null));
    Assert.assertEquals(-1, LibString.compare(null, "x"));
  }

  @Test
  public void equality() {
    Assert.assertTrue(LibString.equality(null, null));
    Assert.assertTrue(LibString.equality("x", "x"));
    Assert.assertFalse(LibString.equality("x", "y"));
    Assert.assertFalse(LibString.equality("x", null));
    Assert.assertFalse(LibString.equality(null, "y"));
  }

  @Test
  public void mult() {
    Assert.assertEquals("", LibString.multiply("x", 0));
    Assert.assertEquals("x", LibString.multiply("x", 1));
    Assert.assertEquals("xx", LibString.multiply("x", 2));
    Assert.assertEquals("xxx", LibString.multiply("x", 3));
    Assert.assertEquals("xxxxx", LibString.multiply("x", 5));
  }

  @Test
  public void of() {
    Assert.assertEquals("1", LibString.of(1));
    Assert.assertEquals("1", LibString.of(1L));
    Assert.assertEquals("1.5", LibString.of(1.5));
    Assert.assertEquals("true", LibString.of(true));
  }

  @Test
  public void reverse() {
    Assert.assertEquals("zen", LibString.reverse("nez"));
  }
}

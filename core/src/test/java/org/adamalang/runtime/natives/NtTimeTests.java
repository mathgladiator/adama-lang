/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtTimeTests {
  @Test
  public void coverage() {
    NtTime t = new NtTime(12, 17);
    Assert.assertEquals(t, t);
    Assert.assertEquals(t, new NtTime(12, 17));
    Assert.assertNotEquals(t, new NtTime(1, 4));
    Assert.assertNotEquals(t, null);
    Assert.assertNotEquals(t, "");
    t.hashCode();
    Assert.assertEquals("12:17", t.toString());
  }

  @Test
  public void pad() {
    NtTime t = new NtTime(12, 1);
    Assert.assertEquals("12:01", t.toString());
  }

  @Test
  public void compare() {
    NtTime t1 = new NtTime(12, 1);
    NtTime t2 = new NtTime(12, 5);
    NtTime t3 = new NtTime(11, 1);
    NtTime t4 = new NtTime(13, 1);
    Assert.assertTrue(t1.compareTo(t2) < 0);
    Assert.assertTrue(t2.compareTo(t1) > 0);
    Assert.assertTrue(t1.compareTo(t4) < 0);
    Assert.assertTrue(t3.compareTo(t1) < 0);
  }
}

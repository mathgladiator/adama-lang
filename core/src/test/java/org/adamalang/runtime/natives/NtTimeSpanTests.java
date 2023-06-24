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

public class NtTimeSpanTests {
  @Test
  public void coverage() {
    NtTimeSpan ts = new NtTimeSpan(123);
    Assert.assertEquals(ts, ts);
    Assert.assertEquals(ts, new NtTimeSpan(123));
    Assert.assertNotEquals(ts, new NtTimeSpan(423));
    Assert.assertNotEquals(ts, "");
    Assert.assertNotEquals(ts, null);
    ts.hashCode();
    Assert.assertEquals("123.0 sec", ts.toString());
    Assert.assertEquals(24, ts.memory());
  }

  @Test
  public void compare() {
    NtTimeSpan a = new NtTimeSpan(1);
    NtTimeSpan b = new NtTimeSpan(54);
    Assert.assertTrue(a.compareTo(b) < 0);
    Assert.assertTrue(b.compareTo(a) > 0);
    Assert.assertEquals(0, a.compareTo(a));
  }
}

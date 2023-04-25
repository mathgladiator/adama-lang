/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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

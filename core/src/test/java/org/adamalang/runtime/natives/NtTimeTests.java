/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

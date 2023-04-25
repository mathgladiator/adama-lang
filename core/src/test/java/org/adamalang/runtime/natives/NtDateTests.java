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

public class NtDateTests {
  @Test
  public void coverage() {
    NtDate d = new NtDate(2010, 11, 22);
    Assert.assertEquals(d, d);
    Assert.assertEquals(d, new NtDate(2010, 11, 22));
    Assert.assertNotEquals(d, new NtDate(2222, 11, 22));
    Assert.assertNotEquals(d, new NtDate(2010, 7, 22));
    Assert.assertNotEquals(d, new NtDate(2010, 11, 18));
    Assert.assertNotEquals(d, "");
    Assert.assertNotEquals(d, null);
    d.hashCode();
    Assert.assertEquals("2010/11/22", d.toString());
    Assert.assertEquals(24, d.memory());
  }

  @Test
  public void compare() {
    Assert.assertEquals(0, new NtDate(2010, 11, 22).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(-1, new NtDate(2010, 11, 22).compareTo(new NtDate(2011, 11, 22)));
    Assert.assertEquals(1, new NtDate(2011, 11, 22).compareTo(new NtDate(2010, 11, 22)));
  }
}

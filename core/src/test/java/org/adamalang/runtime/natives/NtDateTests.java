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
    Assert.assertEquals("2010-11-22", d.toString());
    Assert.assertEquals(24, d.memory());
  }

  @Test
  public void toint() {
    NtDate d = new NtDate(2010, 11, 22);
    Assert.assertEquals(23541494, d.toInt());
  }

  @Test
  public void compare_year() {
    Assert.assertEquals(0, new NtDate(2010, 11, 22).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(-1, new NtDate(2010, 11, 22).compareTo(new NtDate(2011, 11, 22)));
    Assert.assertEquals(1, new NtDate(2011, 11, 22).compareTo(new NtDate(2010, 11, 22)));
  }

  @Test
  public void compare_month() {
    Assert.assertEquals(-1, new NtDate(2010, 10, 22).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(1, new NtDate(2010, 12, 22).compareTo(new NtDate(2010, 11, 22)));
  }

  @Test
  public void compare_day() {
    Assert.assertEquals(-1, new NtDate(2010, 11, 21).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(1, new NtDate(2010, 11, 23).compareTo(new NtDate(2010, 11, 22)));
  }
}

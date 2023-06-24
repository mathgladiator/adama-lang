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

public class NtDynamicTests {
  @Test
  public void coverage() {
    NtDynamic T = new NtDynamic("true");
    NtDynamic F = new NtDynamic("false");
    Assert.assertEquals("true", T.toString());
    Assert.assertEquals("false", F.toString());
    Assert.assertEquals(14, T.compareTo(F));
    Assert.assertEquals(-14, F.compareTo(T));
    Assert.assertTrue(T.equals(T));
    Assert.assertTrue(F.equals(F));
    Assert.assertTrue(T.equals(new NtDynamic("true")));
    Assert.assertTrue(F.equals(new NtDynamic("false")));
    Assert.assertFalse(T.equals(F));
    Assert.assertFalse(F.equals(T));
    Assert.assertFalse(F.equals(false));
    T.hashCode();
    Assert.assertTrue(NtDynamic.NULL.equals(new NtDynamic("null")));
    Assert.assertEquals(10, F.memory());
  }

  @Test
  public void cache() {
    NtDynamic T = new NtDynamic("{}");
    Assert.assertTrue(T.cached() == T.cached());
  }
}

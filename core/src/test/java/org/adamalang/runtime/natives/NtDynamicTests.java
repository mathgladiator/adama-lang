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
  }
}

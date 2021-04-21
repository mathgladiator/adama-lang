/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
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

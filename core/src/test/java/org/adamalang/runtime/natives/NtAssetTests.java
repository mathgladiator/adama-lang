/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtAssetTests {
  @Test
  public void flow() {
    NtAsset a = new NtAsset("123", "name", "png", 42, "hash", "sheesh");
    NtAsset b = new NtAsset("42", "name", "png", 42, "hash", "sheesh");

    Assert.assertEquals(-3, a.compareTo(b));
    Assert.assertEquals(3, b.compareTo(a));
    Assert.assertEquals(-973748297, a.hashCode());
    Assert.assertTrue(a.equals(a));
    Assert.assertFalse(a.equals(""));
    Assert.assertFalse(a.equals(b));
  }
}

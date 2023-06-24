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
    Assert.assertEquals("123", a.id());
    Assert.assertEquals("name", a.name());
    Assert.assertEquals("png", a.type());
    Assert.assertEquals(42, a.size());
    Assert.assertTrue(a.valid());
    Assert.assertEquals(88, a.memory());
  }
}

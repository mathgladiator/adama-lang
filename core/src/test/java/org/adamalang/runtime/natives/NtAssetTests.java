package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtAssetTests {
  @Test
  public void flow() {
    NtAsset a = new NtAsset(123, "name", "png", 42, "hash", "sheesh");
    NtAsset b = new NtAsset(42, "name", "png", 42, "hash", "sheesh");

    Assert.assertEquals(1, a.compareTo(b));
    Assert.assertEquals(-1, b.compareTo(a));
    Assert.assertEquals(163678990, a.hashCode());
    Assert.assertTrue(a.equals(a));
    Assert.assertFalse(a.equals(""));
    Assert.assertFalse(a.equals(b));
  }
}

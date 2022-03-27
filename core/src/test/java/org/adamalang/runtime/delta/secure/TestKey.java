package org.adamalang.runtime.delta.secure;

import org.junit.Assert;
import org.junit.Test;

public class TestKey {
  public static final String COMMON_KEY = "AgLwVwN0agFV9/2FAIcNez6v+ksLHDNF";

  public static final AssetIdEncoder ENCODER = new AssetIdEncoder(COMMON_KEY);

  @Test
  public void sanity() {
    Assert.assertEquals("123", SecureAssetUtil.decryptFromBase64(SecureAssetUtil.secretKeyOf(COMMON_KEY), ENCODER.encrypt("123")));
  }
}

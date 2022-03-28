package org.adamalang.runtime.delta.secure;

import org.junit.Assert;
import org.junit.Test;

public class TestKey {
  public static final String COMMON_KEY = "DS9srRiyRay6yBJE8ONlT3XenV97g2GS";

  public static final AssetIdEncoder ENCODER = new AssetIdEncoder(COMMON_KEY);

  @Test
  public void sanity() {
    String header = SecureAssetUtil.makeAssetKeyHeader();
    AssetIdEncoder encoder = new AssetIdEncoder(header);
    Assert.assertEquals("123", SecureAssetUtil.decryptFromBase64(SecureAssetUtil.secretKeyOf(header), encoder.encrypt("123")));
  }
}

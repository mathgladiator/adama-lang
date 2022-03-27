package org.adamalang.runtime.delta.secure;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;

public class AssetIdEncoderTests {
  @Test
  public void flow() {
    String key = SecureAssetUtil.makeAssetKeyHeader();
    System.err.println("|" + key);
    AssetIdEncoder encoder = new AssetIdEncoder(key);
    String enc = encoder.encrypt("1234");
    System.err.println(enc);
    SecretKey pkey = SecureAssetUtil.secretKeyOf(key);
    Assert.assertEquals("1234", SecureAssetUtil.decryptFromBase64(pkey, enc));
  }
}

package org.adamalang.runtime.delta.secure;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;

public class SecureAssetUtilTests {
  @Test
  public void assets() throws Exception {
    String str = SecureAssetUtil.makeAssetKeyHeader();
    SecretKey key = SecureAssetUtil.secretKeyOf(str);
    key.getEncoded();
    key.getAlgorithm();
    key.getFormat();
  }

  @Test
  public void coverage() {
    try {
      SecureAssetUtil.encryptToBase64(null, "x");
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      SecureAssetUtil.decryptFromBase64(null, "x");
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      SecureAssetUtil.getKeyGenerator("X");
      Assert.fail();
    } catch (RuntimeException re) {

    }
  }
}

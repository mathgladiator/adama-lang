package org.adamalang.runtime.delta.secure;

import javax.crypto.SecretKey;

public class AssetIdEncoder {

  private final SecretKey key;

  public AssetIdEncoder(String keyHeader) {
    this.key = SecureAssetUtil.secretKeyOf(keyHeader);
  }

  public String encrypt(String id) {
    return SecureAssetUtil.encryptToBase64(key, id);
  }
}

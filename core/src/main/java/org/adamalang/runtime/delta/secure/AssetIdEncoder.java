/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.delta.secure;

import org.adamalang.common.ErrorCodeException;

import javax.crypto.SecretKey;

/** encode assets for secure usage by clients */
public class AssetIdEncoder {
  private final SecretKey key;

  public AssetIdEncoder(String keyHeader) throws ErrorCodeException {
    this.key = SecureAssetUtil.secretKeyOf(keyHeader);
  }

  /** encrypt the id per the client's given header */
  public String encrypt(String id) {
    return SecureAssetUtil.encryptToBase64(key, id);
  }
}

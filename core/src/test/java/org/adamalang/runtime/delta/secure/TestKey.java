/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.delta.secure;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class TestKey {
  public static final String COMMON_KEY = "DS9srRiyRay6yBJE8ONlT3XenV97g2GS";

  public static final AssetIdEncoder ENCODER = MAKE();

  private static AssetIdEncoder MAKE() {
    try {
      return new AssetIdEncoder(COMMON_KEY);
    } catch (ErrorCodeException ece) {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  public void sanity() throws Exception {
    String header = SecureAssetUtil.makeAssetKeyHeader();
    AssetIdEncoder encoder = new AssetIdEncoder(header);
    Assert.assertEquals("123", SecureAssetUtil.decryptFromBase64(SecureAssetUtil.secretKeyOf(header), encoder.encrypt("123")));
  }
}

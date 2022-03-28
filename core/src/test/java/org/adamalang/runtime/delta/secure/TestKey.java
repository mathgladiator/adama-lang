/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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

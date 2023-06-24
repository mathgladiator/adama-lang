/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets;

import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.junit.Assert;
import org.junit.Test;

public class AssetRequestTests {

  @Test
  public void flow() throws Exception {
    String assetKey = SecureAssetUtil.makeAssetKeyHeader();
    String uri = "space/key/id=" + SecureAssetUtil.encryptToBase64(SecureAssetUtil.secretKeyOf(assetKey), "my-id");
    AssetRequest ar = AssetRequest.parse(uri, assetKey);
    Assert.assertEquals("space", ar.space);
    Assert.assertEquals("key", ar.key);
    Assert.assertEquals("my-id", ar.id);
  }

  @Test
  public void flowWithSlash() throws Exception {
    String assetKey = SecureAssetUtil.makeAssetKeyHeader();
    String uri = "space/key/path/path2/id=" + SecureAssetUtil.encryptToBase64(SecureAssetUtil.secretKeyOf(assetKey), "my-id");
    AssetRequest ar = AssetRequest.parse(uri, assetKey);
    Assert.assertEquals("space", ar.space);
    Assert.assertEquals("key/path/path2", ar.key);
    Assert.assertEquals("my-id", ar.id);
  }

}

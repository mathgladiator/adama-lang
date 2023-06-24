/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class AssetUploadBodyTests {
  @Test
  public void just_file() {
    AssetUploadBody body = AssetUploadBody.WRAP(new File("."));
    Assert.assertNotNull(body.getFileIfExists());
    Assert.assertNull(body.getBytes());
  }

  @Test
  public void just_bytes() {
    AssetUploadBody body = AssetUploadBody.WRAP("XYZ".getBytes(StandardCharsets.UTF_8));
    Assert.assertNull(body.getFileIfExists());
    Assert.assertNotNull(body.getBytes());
  }
}

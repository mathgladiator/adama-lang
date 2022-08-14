/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.contracts;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class AssetUploadBodyTests {
  @Test
  public void just_file() {
    AssetUploadBody body = AssetUploadBody.WRAP(new File("."));
    Assert.assertNotNull(body.getFileIsExists());
    Assert.assertNull(body.getBytes());
  }

  @Test
  public void just_bytes() {
    AssetUploadBody body = AssetUploadBody.WRAP("XYZ".getBytes(StandardCharsets.UTF_8));
    Assert.assertNull(body.getFileIsExists());
    Assert.assertNotNull(body.getBytes());
  }
}

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
import java.nio.file.Files;

public class AssetFactTests {
  @Test
  public void just_bytes() throws Exception {
    AssetUploadBody body = AssetUploadBody.WRAP("XYZ".getBytes(StandardCharsets.UTF_8));
    AssetFact fact = AssetFact.of(body);
    Assert.assertEquals(3, fact.size);
    Assert.assertEquals("5lB11VD5tb+ZkvodcaExvg==", fact.md5);
    Assert.assertEquals("Fl8D+bwAJF//H6j+vvK8eG7KPhF3O4j3BdiLo8zCa2OvtTUCkBO/aCYC/8DqqrSC", fact.sha384);
  }

  @Test
  public void just_file() throws Exception {
    File file = File.createTempFile("ADAMATEST_", "temp_file");
    try {
      Files.writeString(file.toPath(), "ABCDEF");
      AssetUploadBody body = AssetUploadBody.WRAP(file);
      AssetFact fact = AssetFact.of(body);
      Assert.assertEquals(6, fact.size);
      Assert.assertEquals("iCekESKlAouYCMe/hLn89g==", fact.md5);
      Assert.assertEquals("OeDMGwK4wIK2RkPNyuDmGA5GDh9xeCTHixf5RsmGTJDATFUGeyMeabCtW9GdEqBl", fact.sha384);
    } finally {
      file.delete();
    }
  }
}

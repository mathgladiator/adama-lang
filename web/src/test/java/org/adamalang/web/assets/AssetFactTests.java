/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

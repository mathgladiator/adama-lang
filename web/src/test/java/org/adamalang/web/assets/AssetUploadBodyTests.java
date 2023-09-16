/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

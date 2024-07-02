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
package org.adamalang.web.assets.transforms;

import org.adamalang.common.Hashing;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.MessageDigest;

public class ImageTransformTests {
  private static File imageRoot() {
    File file = new File("images");
    if (file.exists() && file.isDirectory()) {
      return file;
    }
    throw new UnsupportedOperationException("failed to find images directory:" + file.getAbsolutePath());
  }

  private static void transformForAudit(String file, String transform) throws Exception {
    ImageTransform it = new ImageTransform("png", transform);
    FileInputStream input = new FileInputStream(new File(imageRoot(), file));
    File tempDir = new File(imageRoot(), "output");
    tempDir.mkdirs();
    try {
      File output = new File(tempDir, "result_" + transform + "_" + file);
      if (output.exists()) {
        output.delete();
      }
      it.execute(input, output);
    } finally {
      input.close();
    }
  }

  private void battery(String file) throws Exception{
    transformForAudit(file, "gray");
    transformForAudit(file, "w100_h50_fc");
    transformForAudit(file, "w50_h100_fc");
    transformForAudit(file, "w50_sq");
    transformForAudit(file, "h50_sq");
  }

  @Test
  public void square() throws Exception{
    battery("square.png");
  }

  @Test
  public void tall() throws Exception{
    battery("tall.png");
  }

  @Test
  public void wide() throws Exception{
    battery("wide.png");
  }

  // test runner for validating things
  /*
  @Test
  public void flow_new_stuff() throws Exception{
    ImageTransform it = new ImageTransform("png", "w500_sq_gray");
    FileInputStream input = new FileInputStream(new File(imageRoot(), "wide.png"));
    try {
      File output = new File(imageRoot(), "bleeding.png");
      try {
        it.execute(input, output);
      } finally {
        // output.delete();
      }
    } finally {
      input.close();
    }
  }
  */
}

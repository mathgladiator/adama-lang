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

  private static String transformHash(String file, String transform) throws Exception {
    ImageTransform it = new ImageTransform("png", transform);
    FileInputStream input = new FileInputStream(new File(imageRoot(), file));
    try {
      File output = File.createTempFile("adama_imaging", ".png");
      try {
        it.execute(input, output);
        MessageDigest md = Hashing.md5();
        md.update(Files.readAllBytes(output.toPath()));
        return Hashing.finishAndEncodeHex(md);
      } finally {
        output.delete();
      }
    } finally {
      input.close();
    }
  }

  @Test
  public void flow_square_w48_h48_sq() throws Exception{
    transformHash("square.png", "w90_h48_fc");
  }

  @Test
  public void flow_square_w48_sq() throws Exception{
    transformHash("wide.png", "w90_sq");
  }

  // test runner for validating things
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
}

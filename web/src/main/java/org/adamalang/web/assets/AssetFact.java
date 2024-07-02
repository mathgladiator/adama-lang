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

import org.adamalang.common.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/** Facts about an asset which are computed */
public class AssetFact {
  public final long size;
  public final String md5;
  public final String sha384;

  public AssetFact(long size, String md5, String sha384) {
    this.size = size;
    this.md5 = md5;
    this.sha384 = sha384;
  }

  public static AssetFact of(AssetUploadBody body) throws IOException  {
    MessageDigest md5 = Hashing.md5();
    MessageDigest sha384 = Hashing.sha384();
    long size = 0;
    File file = body.getFileIfExists();
    if (file != null && file.exists()) {
      try(FileInputStream input = new FileInputStream(file)) {
        byte[] chunk = new byte[8196];
        int sz;
        while ((sz = input.read(chunk)) >= 0) {
          size += sz;
          md5.update(chunk, 0, sz);
          sha384.update(chunk, 0, sz);
       }
      }
    } else {
      byte[] bytes = body.getBytes();
      size = bytes.length;
      md5.update(bytes);
      sha384.update(bytes);
    }
    return new AssetFact(size, Hashing.finishAndEncode(md5), Hashing.finishAndEncode(sha384));
  }
}

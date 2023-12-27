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
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Hashing;
import org.adamalang.common.Hex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileReaderHttpRequestBody implements SimpleHttpRequestBody {
  public final String sha256;
  public final long size;
  private final File file;
  private final BufferedInputStream input;

  public FileReaderHttpRequestBody(File file) throws Exception {
    this.file = file;
    BufferedInputStream inputDigest = null;
    MessageDigest digest = Hashing.sha256();
    long _size = 0;
    try {
      inputDigest = new BufferedInputStream(new FileInputStream(file));
      byte[] chunk = new byte[8196];
      int sz = 0;
      while ((sz = inputDigest.read(chunk)) >= 0) {
        digest.update(chunk, 0, sz);
        _size += sz;
      }
      this.sha256 = Hex.of(digest.digest());
      this.size = _size;
    } finally {
      if (inputDigest != null) {
        inputDigest.close();
      }
    }
    this.input = new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public long size() {
    return this.size;
  }

  @Override
  public int read(byte[] chunk) throws Exception {
    return this.input.read(chunk);
  }

  @Override
  public void finished(boolean success) throws Exception {
    this.input.close();
  }

  @Override
  public void pumpLogEntry(ObjectNode body) {
    body.put("type", "file");
    body.put("size", size);
    body.put("filename", file.getName());
    body.put("sha256", sha256);
  }
}

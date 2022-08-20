/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.common.Hashing;
import org.adamalang.common.Hex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileReaderHttpRequestBody implements SimpleHttpRequestBody {
  public final String sha256;
  public final long size;

  private final BufferedInputStream input;

  public FileReaderHttpRequestBody(File file) throws Exception {
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
}

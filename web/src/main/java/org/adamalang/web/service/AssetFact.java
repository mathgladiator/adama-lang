/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import org.adamalang.common.Hashing;
import org.adamalang.web.contracts.AssetUploadBody;

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
    File file = body.getFileIsExists();
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

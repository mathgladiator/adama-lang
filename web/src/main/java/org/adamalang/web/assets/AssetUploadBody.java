/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets;

import java.io.File;

/** wrapper around the body of an assets */
public interface AssetUploadBody {
  public File getFileIsExists();

  public byte[] getBytes();

  public static AssetUploadBody WRAP(File file) {
    return new AssetUploadBody() {
      @Override
      public File getFileIsExists() {
        return file;
      }

      @Override
      public byte[] getBytes() {
        return null;
      }
    };
  }

  public static AssetUploadBody WRAP(byte[] bytes) {
    return new AssetUploadBody() {
      @Override
      public File getFileIsExists() {
        return null;
      }

      @Override
      public byte[] getBytes() {
        return bytes;
      }
    };
  }
}

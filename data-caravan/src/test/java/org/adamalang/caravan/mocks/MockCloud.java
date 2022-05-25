/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.mocks;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.io.File;
import java.util.HashMap;

public class MockCloud implements Cloud {
  private final File path;
  private final HashMap<String, File> map;

  public MockCloud() {
    try {
      path = new File(File.createTempFile("abc", "def").getParentFile(), "_ADAMA_TEMP_" + System.currentTimeMillis());
      path.mkdir();
      map = new HashMap<>();
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          for (File file : path.listFiles()) {
            file.delete();
          }
          path.delete();
        }
      }));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public File path() {
    return path;
  }

  @Override
  public void restore(String archiveKey, Callback<File> callback) {
    File found = map.get(archiveKey + ".archive");
    if (found == null) {
      callback.failure(new ErrorCodeException(-102));
    } else {
      callback.success(found);
    }
  }

  @Override
  public void backup(File archiveFile, Callback<Void> callback) {
    map.put(archiveFile.getName(), archiveFile);
    callback.success(null);
  }
}

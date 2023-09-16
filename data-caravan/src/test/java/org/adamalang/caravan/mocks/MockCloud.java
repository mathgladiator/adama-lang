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
package org.adamalang.caravan.mocks;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;

import java.io.File;
import java.util.HashMap;

public class MockCloud implements Cloud {
  private final File path;
  private final HashMap<String, File> map;

  public MockCloud() {
    try {
      path = new File(File.createTempFile("adama_abc", "def").getParentFile(), "_ADAMA_TEMP_" + System.currentTimeMillis());
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
  public void delete(Key key, String archiveKey, Callback<Void> callback) {
    File found = map.remove(archiveKey);
    if (found != null) {
      found.delete();
    }
  }

  @Override
  public File path() {
    return path;
  }

  @Override
  public void exists(Key key, String archiveKey, Callback<Void> callback) {
    File found = map.get(archiveKey);
    if (found == null) {
      callback.failure(new ErrorCodeException(-102));
    } else {
      callback.success(null);
    }
  }

  @Override
  public void restore(Key key, String archiveKey, Callback<File> callback) {
    if (archiveKey.equals("notfound")) {
      callback.success(new File(path, "nope"));
      return;
    }
    File found = map.get(archiveKey);
    if (found == null) {
      callback.failure(new ErrorCodeException(-102));
    } else {
      callback.success(found);
    }
  }

  @Override
  public void backup(Key key, File archiveFile, Callback<Void> callback) {
    map.put(archiveFile.getName(), archiveFile);
    callback.success(null);
  }
}

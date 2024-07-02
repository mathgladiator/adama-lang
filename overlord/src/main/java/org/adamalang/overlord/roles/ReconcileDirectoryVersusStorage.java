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
package org.adamalang.overlord.roles;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.model.FinderOperations;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.data.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/** list directory and compare to S3 to ensure everything is present */
public class ReconcileDirectoryVersusStorage {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReconcileDirectoryVersusStorage.class);

  public static void kickOff(DataBase dataBase, OverlordMetrics metrics, Cloud cloud) {
    SimpleExecutor executor = SimpleExecutor.create("storage-reconcile-once");
    executor.schedule(new NamedRunnable("initial-scan-reconcile") {
      @Override
      public void execute() throws Exception {
        ArrayList<DocumentIndex> all = FinderOperations.listAll(dataBase);
        LOGGER.error("sweeping documents: {}", all.size());
        for (final DocumentIndex doc : all) {
          if (doc.archive != null && !doc.archive.equals("")) {
            cloud.exists(new Key(doc.space, doc.key), doc.archive, new Callback<Void>() {
              @Override
              public void success(Void value) {
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOGGER.error("FAILEDFIND:" + doc.space + "/" + doc.key + " archive:" + doc.archive + " (" + ex.code + ")");
                metrics.found_missing_document.up();
              }
            });
          }
          Thread.sleep(50);
        }
      }
    }, 60000);
  }
}

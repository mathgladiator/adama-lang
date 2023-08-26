/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

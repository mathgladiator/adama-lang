/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord.roles;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.MeteringSpaceSummary;
import org.adamalang.mysql.data.ResourcesPerPenny;
import org.adamalang.mysql.data.UnbilledResources;
import org.adamalang.mysql.model.*;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.metering.BillingDocumentFinder;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class GlobalStorageReporter {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStorageReporter.class);

  public static void kickOff(OverlordMetrics metrics, MultiRegionClient client, DataBase dataBase, BillingDocumentFinder finder) throws Exception {
    new HourlyStorageAccountantTask(metrics, client, dataBase, finder);
  }

  public static class HourlyStorageAccountantTask extends NamedRunnable {
    private final OverlordMetrics metrics;
    private final SimpleExecutor executor;
    private final MultiRegionClient client;
    private final DataBase dataBase;
    private final BillingDocumentFinder finder;

    public HourlyStorageAccountantTask(OverlordMetrics metrics, MultiRegionClient client, DataBase dataBase, BillingDocumentFinder finder) throws Exception {
      super("hourly-accountant");
      this.metrics = metrics;
      this.executor = SimpleExecutor.create("hourly-accountant-executor");
      this.client = client;
      this.dataBase = dataBase;
      this.finder = finder;
      // wait 30 seconds for the client to warm up
      executor.schedule(this, 30000);
    }

    @Override
    public void execute() throws Exception {
      try {
        accountForStorage();
      } finally {
        executor.schedule(this, 1000 * 60 * 5);
      }
    }

    public void accountForStorage() throws Exception {
      AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal("overlord", "region"), new ConnectionContext("adama", "0.0.0.0", "adama", null));

      // inventory the backend
      long timestamp = System.currentTimeMillis();
      HashMap<String, Long> inventory = FinderOperations.inventoryStorage(dataBase);
      for (Map.Entry<String, Long> entry : inventory.entrySet()) {
        ObjectNode message = Json.newJsonObject();
        message.put("space", entry.getKey());
        message.put("timestamp", timestamp);
        message.put("bytes_used", entry.getValue());
        try {
          Spaces.setSpaceStorage(dataBase, entry.getKey(), entry.getValue());
        } catch (Exception ex) {
          LOGGER.error("failed-set-storage: " + entry.getKey(), ex);
        }


        finder.find(entry.getKey(), new Callback<Key>() {
          String space = entry.getKey();
          String msg = message.toString();
          @Override
          public void success(Key key) {
            client.directSend(user, key.space, key.key, "storage-" + timestamp + "-" + space, "ingest_new_storage_record", msg, new Callback<Integer>() {
              @Override
              public void success(Integer value) {
                LOGGER.error("billed-for:" + space);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOGGER.error("failed-storage-billing:" + space + ":" + ex.code);
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            LOGGER.error("failed-find:" + space + ":" + ex.code);
          }
        });
      }
    }
  }
}

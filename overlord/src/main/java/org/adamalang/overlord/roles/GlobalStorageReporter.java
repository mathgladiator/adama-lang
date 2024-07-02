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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.SystemUsageInventoryRecord;
import org.adamalang.mysql.model.*;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.metering.BillingDocumentFinder;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
        long timestamp = System.currentTimeMillis();
        try {
          accountForStorage(timestamp);
        } catch (Exception ex) {
          LOGGER.error("failed-storage-accounting", ex);
        }
        try {
          accountForSystemUsage(timestamp);
        } catch (Exception ex) {
          LOGGER.error("failed-usage-accounting", ex);
        }
      } finally {
        executor.schedule(this, 1000 * 60 * 5);
      }
    }

    public void accountForStorage(long timestamp) throws Exception {
      AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal("overlord", "region"), new ConnectionContext("adama", "0.0.0.0", "adama", new TreeMap<>()));
      HashMap<String, Long> inventory = Inventory.inventoryStorage(dataBase);
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
            client.directSend(user, key.space, key.key, "storage-" + timestamp + "-" + space, "ingest_new_storage_record", msg, metrics.storage_record_sent.wrap(new Callback<Integer>() {
              @Override
              public void success(Integer value) {}

              @Override
              public void failure(ErrorCodeException ex) {
                LOGGER.error("failed-storage-billing:" + space + ":" + ex.code);
              }
            }));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            LOGGER.error("failed-find:" + space + ":" + ex.code);
          }
        });
      }
    }

    public void accountForSystemUsage(long timestamp) throws Exception {
      AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal("overlord", "region"), new ConnectionContext("adama", "0.0.0.0", "adama", new TreeMap<>()));
      HashMap<Integer, SystemUsageInventoryRecord> inventory = Inventory.inventorySystemUsage(dataBase);
      for (Map.Entry<Integer, SystemUsageInventoryRecord> entry : inventory.entrySet()) {
        ObjectNode message = Json.newJsonObject();
        message.put("timestamp", timestamp);
        message.put("domains", entry.getValue().domains);
        message.put("authorities", entry.getValue().authorities);
        String msg = message.toString();
        client.directSend(user, "billing", "" + entry.getKey(), "system-usage-" + timestamp + "-" + entry.getKey(), "ingest_new_system_usage", msg, metrics.system_usage_record_sent.wrap(new Callback<Integer>() {
          @Override
          public void success(Integer value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        }));
      }
    }
  }
}

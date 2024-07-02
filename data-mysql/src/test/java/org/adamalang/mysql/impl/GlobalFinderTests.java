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
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.mysql.mocks.SimpleFinderCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.mysql.model.FinderOperations;
import org.adamalang.mysql.model.Inventory;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocationType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalFinderTests {
  private final Key KEY1 = new Key("space-1", "key-1");
  private final Key KEY2 = new Key("space-2", "key-2");

  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        Assert.assertFalse(FinderOperations.exists(dataBase, 1));
        GlobalFinder machineA = new GlobalFinder(dataBase, "region", "machine-a");
        GlobalFinder machineB = new GlobalFinder(dataBase, "region", "machine-b");
        ArrayList<DocumentIndex> listing;
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY1, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        listing = FinderOperations.list(dataBase, "space-1", "", 4);
        Assert.assertEquals(0, listing.size());
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.bind(KEY1, callback);
          callback.assertSuccess();
        }
        listing = FinderOperations.list(dataBase, "space-1", "", 4);
        Assert.assertEquals(1, FinderOperations.listAll(dataBase).size());
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("key-1", listing.get(0).key);

        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY1, cb);
          long id = cb.assertSuccessAndGetId();
          Assert.assertTrue(FinderOperations.exists(dataBase, id));
        }

        List<GCTask> tasks = FinderOperations.produceGCTasks(dataBase);
        Assert.assertEquals(0, tasks.size());

        Assert.assertEquals(KEY1.key, listing.get(0).key);
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.bind(KEY1, callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineB.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.bind(KEY1, callback);
          callback.assertFailure(667658);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.bind(KEY1, callback);
          callback.assertSuccess();
        }
        BackupResult result1 = new BackupResult("new-achive-key", 0, 1, 4);
        BackupResult result2 = new BackupResult("new-achive-key-old", 0, 1, 8);
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.backup(KEY1, result1, callback);
          callback.assertFailure(722034);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.backup(KEY1, result1, callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineB.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "new-achive-key");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineB.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b",  "new-achive-key");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.backup(KEY1, result2, callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineB.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "new-achive-key-old");
        }
        CountDownLatch listFinished = new CountDownLatch(1);
        machineB.list(new Callback<List<Key>>() {
          @Override
          public void success(List<Key> value) {
            for (Key result : value) {
              Assert.assertEquals(result, KEY1);
            }
            Assert.assertEquals(1, value.size());
            listFinished.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(listFinished.await(5000, TimeUnit.MILLISECONDS));
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.backup(KEY1, result2, callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineB.find(KEY1, cb);
          cb.assertSuccess(LocationType.Machine, "machine-b", "new-achive-key-old");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.free(KEY1, callback);
          callback.assertSuccess();
        }
        tasks = FinderOperations.produceGCTasks(dataBase);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(1, tasks.get(0).id);
        Assert.assertEquals("new-achive-key-old", tasks.get(0).archiveKey);
        Assert.assertEquals("space-1", tasks.get(0).space);
        Assert.assertEquals("key-1", tasks.get(0).key);
        Assert.assertTrue(FinderOperations.validateTask(dataBase, tasks.get(0)));
        FinderOperations.lowerTask(dataBase, tasks.get(0));
        tasks = FinderOperations.produceGCTasks(dataBase);
        Assert.assertEquals(0, tasks.size());
        HashMap<String, Long> inventory = Inventory.inventoryStorage(dataBase);
        Assert.assertEquals(1, inventory.size());
        Assert.assertEquals(9, (long) inventory.get(KEY1.space));
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machineA.find(KEY1, cb);
          cb.assertSuccess(LocationType.Archive, "", "new-achive-key-old");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.bind(KEY1, callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.markDelete(KEY1, callback);
          callback.assertFailure(773247);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineB.commitDelete(KEY1, callback);
          callback.assertFailure(787655);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.markDelete(KEY1, callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback fauxCallback = new SimpleMockCallback();
          machineA.listDeleted(new Callback<List<Key>>() {
            @Override
            public void success(List<Key> value) {
              Assert.assertTrue(value.size() == 1);
              fauxCallback.success(null);
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          fauxCallback.assertSuccess();
        }
        {
          SimpleMockCallback fauxCallback = new SimpleMockCallback();
          machineA.list(new Callback<List<Key>>() {
            @Override
            public void success(List<Key> value) {
              Assert.assertTrue(value.size() == 0);
              fauxCallback.success(null);
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          fauxCallback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.bind(KEY1, callback);
          callback.assertFailure(667658);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machineA.commitDelete(KEY1, callback);
          callback.assertSuccess();
        }
        listing = FinderOperations.list(dataBase, "space-1", "", 4);
        Assert.assertEquals(0, FinderOperations.listAll(dataBase).size());
        Assert.assertEquals(0, listing.size());
      } finally {
        installer.uninstall();
      }
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.data.GCTask;
import org.adamalang.mysql.mocks.SimpleFinderCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FinderTests {
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
        Finder machine = new Finder(dataBase, "region");
        ArrayList<DocumentIndex> listing;
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        listing = FinderOperations.list(dataBase, "space-1", "", 4);
        Assert.assertEquals(0, listing.size());
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.bind(KEY1, "machineB:523", callback);
          callback.assertSuccess();
        }
        listing = FinderOperations.list(dataBase, "space-1", "", 4);
        Assert.assertEquals(1, FinderOperations.listAll(dataBase).size());
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("key-1", listing.get(0).key);

        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          long id = cb.assertSuccessAndGetId();
          Assert.assertTrue(FinderOperations.exists(dataBase, id));
        }

        List<GCTask> tasks = FinderOperations.produceGCTasks(dataBase);
        Assert.assertEquals(0, tasks.size());

        Assert.assertEquals(KEY1.key, listing.get(0).key);
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.bind(KEY1, "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.bind(KEY1, "machineA:124", callback);
          callback.assertFailure(667658);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.bind(KEY1, "machineB:523", callback);
          callback.assertSuccess();
        }
        BackupResult result1 = new BackupResult("new-achive-key", 0, 1, 4);
        BackupResult result2 = new BackupResult("new-achive-key-old", 0, 1, 8);
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.backup(KEY1, result1, "machineA:124", callback);
          callback.assertFailure(722034);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.backup(KEY1, result1, "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "new-achive-key");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "new-achive-key");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.backup(KEY1, result2, "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "new-achive-key-old");
        }
        CountDownLatch listFinished = new CountDownLatch(1);
        machine.list("machineB:523", new Callback<List<Key>>() {
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
          machine.backup(KEY1, result2, "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523", "new-achive-key-old");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.free(KEY1, "machineB:523", callback);
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
        HashMap<String, Long> inventory = FinderOperations.inventoryStorage(dataBase);
        Assert.assertEquals(1, inventory.size());
        Assert.assertEquals(9, (long) inventory.get(KEY1.space));
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Archive, "", "new-achive-key-old");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.bind(KEY1, "targetNew", callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.markDelete(KEY1, "nop", callback);
          callback.assertFailure(773247);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.commitDelete(KEY1, "nop", callback);
          callback.assertFailure(787655);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.markDelete(KEY1, "targetNew", callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback fauxCallback = new SimpleMockCallback();
          machine.listDeleted("targetNew", new Callback<List<Key>>() {
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
          machine.list("targetNew", new Callback<List<Key>>() {
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
          machine.bind(KEY1, "targetNew", callback);
          callback.assertFailure(667658);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.commitDelete(KEY1, "targetNew", callback);
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

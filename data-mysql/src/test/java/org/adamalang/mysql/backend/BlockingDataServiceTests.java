/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.mocks.SimpleDataCallback;
import org.adamalang.mysql.mocks.SimpleIntCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.ComputeMethod;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class BlockingDataServiceTests {
  private static final Key KEY_1 = new Key("space", "key1");
  private static final Key KEY_2 = new Key("space", "key2");
  private static final RemoteDocumentUpdate UPDATE_1 =
      new RemoteDocumentUpdate(
          1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_2 =
      new RemoteDocumentUpdate(
          2, 2, null, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_3 =
      new RemoteDocumentUpdate(
          3, 3, null, "REQUEST", "{\"x\":3}", "{\"x\":2,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_4 =
      new RemoteDocumentUpdate(
          4, 4, null, "REQUEST", "{\"x\":4}", "{\"x\":3,\"z\":42}", true, 0, 100, UpdateType.AddUserData);

  @Test
  public void flow_1() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory(), "noop"))) {
      BackendDataServiceInstaller installer = new BackendDataServiceInstaller(dataBase);
      try {
        // make sure the database and tables are all proper and set
        installer.install();
        BlockingDataService service = new BlockingDataService(new BackendMetrics(new NoOpMetricsFactory()), dataBase);

        // create the key the first time, should work
        SimpleMockCallback cb1 = new SimpleMockCallback();
        service.initialize(KEY_1, UPDATE_1, cb1);
        cb1.assertSuccess();

        // do some fun listing
        Assert.assertEquals(1, BackendOperations.list(dataBase, KEY_1.space, "", 100).size());
        Assert.assertEquals(0, BackendOperations.list(dataBase, KEY_1.space, KEY_1.key, 100).size());

        // second time to create the key should fail
        SimpleMockCallback cb2 = new SimpleMockCallback();
        service.initialize(KEY_1, UPDATE_1, cb2);
        cb2.assertFailure(667658);

        // update the key and put it in an active state
        SimpleMockCallback cb3 = new SimpleMockCallback();
        service.patch(KEY_1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb3);
        cb3.assertSuccess();

        // patching with same sequencer should fail
        SimpleMockCallback cb4 = new SimpleMockCallback();
        service.patch(KEY_1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb4);
        cb4.assertFailure(621580);

        // getting the data should return a composite
        SimpleDataCallback cb5 = new SimpleDataCallback();
        service.get(KEY_1, cb5);
        cb5.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb5.value);

        SimpleMockCallback cb5_X = new SimpleMockCallback();
        service.patch(KEY_1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb5_X);
        cb5_X.assertSuccess();

        HashMap<String, Long> inventory = BackendOperations.inventoryStorage(dataBase);
        Assert.assertEquals(384L, (long) inventory.get("space"));

        SimpleDataCallback cb5_X2 = new SimpleDataCallback();
        service.get(KEY_1, cb5_X2);
        cb5_X2.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb5_X2.value);

        SimpleDataCallback cb6 = new SimpleDataCallback();
        service.compute(KEY_1, ComputeMethod.Rewind, 1, cb6);
        cb6.assertSuccess();
        Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", cb6.value);

        SimpleDataCallback cb7 = new SimpleDataCallback();
        service.compute(KEY_1, ComputeMethod.Rewind, 2, cb7);
        cb7.assertSuccess();
        Assert.assertEquals("{\"x\":1,\"z\":42}", cb7.value);

        SimpleDataCallback cb8 = new SimpleDataCallback();
        service.compute(KEY_1, ComputeMethod.Rewind, 20, cb8);
        cb8.assertFailure(694287);

        SimpleDataCallback cb11 = new SimpleDataCallback();
        service.compute(KEY_1, ComputeMethod.HeadPatch, 1, cb11);
        cb11.assertSuccess();
        Assert.assertEquals("{\"x\":2}", cb11.value);

        SimpleDataCallback cb12 = new SimpleDataCallback();
        service.compute(KEY_1, ComputeMethod.HeadPatch, 200, cb12);
        cb12.assertFailure(602115);

        SimpleDataCallback cb13 = new SimpleDataCallback();
        service.compute(KEY_1, null, 1, cb13);
        cb13.assertFailure(656396);

        SimpleMockCallback cb14 = new SimpleMockCallback();
        service.delete(KEY_1, cb14);
        cb14.assertSuccess();

        SimpleMockCallback cb15 = new SimpleMockCallback();
        service.delete(KEY_1, cb15);
        cb15.assertFailure(625676);
      } finally {
        installer.uninstall();
      }
    }
  }

  @Test
  public void compact() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory(), "noop"))) {
      BackendDataServiceInstaller installer = new BackendDataServiceInstaller(dataBase);
      try {
        // make sure the database and tables are all proper and set
        installer.install();
        BlockingDataService service = new BlockingDataService(new BackendMetrics(new NoOpMetricsFactory()), dataBase);

        // create the key the first time, should work
        {
          SimpleMockCallback cb1 = new SimpleMockCallback();
          service.initialize(KEY_2, UPDATE_1, cb1);
          cb1.assertSuccess();

          SimpleMockCallback cb2 = new SimpleMockCallback();
          service.patch(KEY_2, new RemoteDocumentUpdate[]{UPDATE_2}, cb2);
          cb2.assertSuccess();

          // getting the data should return a composite
          SimpleDataCallback cb3 = new SimpleDataCallback();
          service.get(KEY_2, cb3);
          cb3.assertSuccess();
          Assert.assertEquals("{\"x\":2,\"y\":4}", cb3.value);
          Assert.assertEquals(2, cb3.reads);

          SimpleMockCallback cb4 = new SimpleMockCallback();
          service.patch(KEY_2, new RemoteDocumentUpdate[]{UPDATE_3, UPDATE_4}, cb4);
          cb4.assertSuccess();
        }

        {
          SimpleDataCallback s_cb1 = new SimpleDataCallback();
          service.get(KEY_2, s_cb1);
          s_cb1.assertSuccess();
          Assert.assertEquals("{\"x\":4,\"y\":4}", s_cb1.value);
          Assert.assertEquals(4, s_cb1.reads);

          SimpleDataCallback s_cb2 = new SimpleDataCallback();
          service.compute(KEY_2, ComputeMethod.Rewind, 0, s_cb2);
          Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", s_cb2.value);
          Assert.assertEquals(4, s_cb2.reads);
        }

        SimpleIntCallback cb5 = new SimpleIntCallback();
        service.snapshot(KEY_2, 1, "{}", 10000, cb5);
        cb5.assertSuccess(0);

        {
          SimpleDataCallback s_cb1 = new SimpleDataCallback();
          service.get(KEY_2, s_cb1);
          s_cb1.assertSuccess();
          Assert.assertEquals("{\"x\":4,\"y\":4}", s_cb1.value);
          Assert.assertEquals(4, s_cb1.reads);

          SimpleDataCallback s_cb2 = new SimpleDataCallback();
          service.compute(KEY_2, ComputeMethod.Rewind, 0, s_cb2);
          Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", s_cb2.value);
          Assert.assertEquals(4, s_cb2.reads);
        }

        SimpleIntCallback cb6 = new SimpleIntCallback();
        service.snapshot(KEY_2, 1, "{}", 2, cb6);
        cb6.assertSuccess(1);

        {
          SimpleDataCallback s_cb1 = new SimpleDataCallback();
          service.get(KEY_2, s_cb1);
          s_cb1.assertSuccess();
          Assert.assertEquals("{\"x\":4,\"y\":4}", s_cb1.value);
          Assert.assertEquals(3, s_cb1.reads);

          SimpleDataCallback s_cb2 = new SimpleDataCallback();
          service.compute(KEY_2, ComputeMethod.Rewind, 0, s_cb2);
          Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", s_cb2.value);
          Assert.assertEquals(3, s_cb2.reads);
        }

        SimpleIntCallback cb7 = new SimpleIntCallback();
        service.snapshot(KEY_2, 1,"{}", 0, cb7);
        cb7.assertSuccess(2);

        {
          SimpleDataCallback s_cb1 = new SimpleDataCallback();
          service.get(KEY_2, s_cb1);
          s_cb1.assertSuccess();
          Assert.assertEquals("{\"x\":4,\"y\":4}", s_cb1.value);
          Assert.assertEquals(1, s_cb1.reads);

          SimpleDataCallback s_cb2 = new SimpleDataCallback();
          service.compute(KEY_2, ComputeMethod.Rewind, 0, s_cb2);
          Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", s_cb2.value);
          Assert.assertEquals(1, s_cb2.reads);
        }
      } finally {
        installer.uninstall();
      }
    }
  }
}

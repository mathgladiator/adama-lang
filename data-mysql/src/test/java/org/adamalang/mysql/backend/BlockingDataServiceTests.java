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

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.adamalang.mysql.mocks.SimpleDataCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class BlockingDataServiceTests {
  private static final Key KEY_1 = new Key("space", "key1");
  private static final DataService.RemoteDocumentUpdate UPDATE_1 =
      new DataService.RemoteDocumentUpdate(
          1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0);
  private static final DataService.RemoteDocumentUpdate UPDATE_2 =
      new DataService.RemoteDocumentUpdate(
          2, null, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":42}", true, 0);
  private static final DataService.RemoteDocumentUpdate UPDATE_3 =
      new DataService.RemoteDocumentUpdate(
          3, null, "REQUEST", "{\"x\":3}", "{\"x\":2,\"z\":42}", true, 0);
  private static final DataService.RemoteDocumentUpdate UPDATE_4 =
      new DataService.RemoteDocumentUpdate(
          4, null, "REQUEST", "{\"x\":4}", "{\"x\":3,\"z\":42}", true, 0);

  @Test
  public void flow_1() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig)) {
      BackendDataServiceInstaller installer = new BackendDataServiceInstaller(dataBase);
      try {
        // make sure the database and tables are all proper and set
        installer.install();
        BlockingDataService service = new BlockingDataService(dataBase);

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
        service.patch(KEY_1, new DataService.RemoteDocumentUpdate[] { UPDATE_2 }, cb3);
        cb3.assertSuccess();

        // patching with same sequencer should fail
        SimpleMockCallback cb4 = new SimpleMockCallback();
        service.patch(KEY_1, new DataService.RemoteDocumentUpdate[] { UPDATE_2 }, cb4);
        cb4.assertFailure(621580);

        // getting the data should return a composite
        SimpleDataCallback cb5 = new SimpleDataCallback();
        service.get(KEY_1, cb5);
        cb5.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb5.value);

        SimpleMockCallback cb5_X = new SimpleMockCallback();
        service.patch(KEY_1, new DataService.RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb5_X);
        cb5_X.assertSuccess();

        SimpleDataCallback cb5_X2 = new SimpleDataCallback();
        service.get(KEY_1, cb5_X2);
        cb5_X2.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb5_X2.value);

        SimpleDataCallback cb6 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.Rewind, 1, cb6);
        cb6.assertSuccess();
        Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", cb6.value);

        SimpleDataCallback cb7 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.Rewind, 2, cb7);
        cb7.assertSuccess();
        Assert.assertEquals("{\"x\":1,\"z\":42}", cb7.value);

        SimpleDataCallback cb8 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.Rewind, 20, cb8);
        cb8.assertFailure(694287);

        SimpleDataCallback cb9 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.Unsend, 20, cb9);
        cb9.assertFailure(650252);

        SimpleDataCallback cb10 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.Unsend, 1, cb10);
        cb10.assertSuccess();
        Assert.assertEquals("{\"y\":0}", cb10.value);

        SimpleDataCallback cb11 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.HeadPatch, 1, cb11);
        cb11.assertSuccess();
        Assert.assertEquals("{\"x\":2}", cb11.value);

        SimpleDataCallback cb12 = new SimpleDataCallback();
        service.compute(KEY_1, DataService.ComputeMethod.HeadPatch, 200, cb12);
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
}

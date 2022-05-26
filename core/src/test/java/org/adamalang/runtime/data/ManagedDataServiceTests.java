/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.data.mocks.SimpleDataCallback;
import org.adamalang.runtime.data.mocks.SimpleIntCallback;
import org.adamalang.runtime.data.mocks.SimpleVoidCallback;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ManagedDataServiceTests {
  public static final Key KEY1 = new Key("space", "123");
  public static final Key KEY2 = new Key("space", "1232");
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

  private static class Setup implements AutoCloseable {
    public final MockFinderService finder;
    public final SimpleExecutor executor;
    public final MockInstantDataService data;
    public final MockArchiveDataSource archive;
    public final Base base;
    public final ManagedDataService managed;

    public Setup() throws Exception {
      this.finder = new MockFinderService();
      this.executor = SimpleExecutor.create("setup");
      this.data = new MockInstantDataService();
      this.archive = new MockArchiveDataSource(data);
      this.base = new Base(new MockFinderService(), archive, "region", "target", executor, 250);
      this.managed = new ManagedDataService(base);
    }

    @Override
    public void close() throws Exception {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void flow() throws Exception {
    try (Setup setup = new Setup()) {
      Runnable firstArchive = setup.archive.latchLogAt(1);
      Runnable secondArchive = setup.archive.latchLogAt(3);
      Runnable secondArchiveExec = setup.archive.latchLogAt(4);
      Runnable waitForDelete = setup.data.latchLogAt(7);
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(new Key("space", "cant-bind"), UPDATE_1, cb_Init);
        cb_Init.assertFailure(-1234);
      }
      Runnable waitInit = setup.data.latchLogAt(1);
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY1, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
      }
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY1, UPDATE_1, cb_Init);
        cb_Init.assertFailure(667658);
      }
      waitInit.run();
      {
        SimpleVoidCallback cb_Patch = new SimpleVoidCallback();
        setup.managed.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_Patch);
        cb_Patch.assertSuccess();
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":70000,\"__seq\":10000}", cb_Get.value);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.compute(KEY1, ComputeMethod.Rewind, 1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":1000}", cb_Get.value);
      }
      {
        SimpleIntCallback cb_Snapshot = new SimpleIntCallback();
        setup.managed.snapshot(KEY1, 2, "{\"x\":1234}", 1, cb_Snapshot);
        cb_Snapshot.assertSuccess(-1);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }
      {
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY1, cb_Close);
        cb_Close.assertSuccess();
      }
      {
        SimpleVoidCallback cb_Patch = new SimpleVoidCallback();
        setup.managed.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3 }, cb_Patch);
        cb_Patch.assertFailure(734320);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.compute(KEY1, ComputeMethod.Rewind, 1, cb_Get);
        cb_Get.assertFailure(768112);
      }

      firstArchive.run();
      setup.archive.driveBackup();
      secondArchive.run();
      setup.archive.driveBackup();
      secondArchiveExec.run();
      setup.archive.assertLogAt(0, "BACKUP:space/123");
      setup.archive.assertLogAt(1, "BACKUP-EXEC:space/123");
      setup.archive.assertLogAt(2, "BACKUP:space/123");
      setup.archive.assertLogAt(3, "BACKUP-EXEC:space/123");
      waitForDelete.run();
      setup.data.assertLogAt(0, "INIT:space/123:1->{\"x\":1,\"y\":4}");
      setup.data.assertLogAt(1, "PATCH:space/123:2-2->{\"x\":2}");
      setup.data.assertLogAt(2, "LOAD:space/123");
      setup.data.assertLogAt(3, "LOAD:space/123");
      setup.data.assertLogAt(4, "LOAD:space/123");
      setup.data.assertLogAt(5, "LOAD:space/123");
      setup.data.assertLogAt(6, "DELETE:space/123");


      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }


      //      setup.archive.assertLogAt(3, "");


    }
  }
}

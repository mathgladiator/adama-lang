/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan;

import org.adamalang.caravan.contracts.KeyToIdService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DurableListStoreMetrics;
import org.adamalang.caravan.mocks.*;
import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class CaravanDataServiceTests {
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

  private static class Setup {
    private final KeyToIdService keyToIdService;
    private final DurableListStore store;
    private final SimpleExecutor executor;
    private final File root;
    private final CaravanDataService service;
    private final Thread flusher;
    private final MockCloud cloud;

    public Setup() throws Exception {
      this.executor = SimpleExecutor.create("executor");
      this.root = new File(File.createTempFile("ADAMATEST_", "yyy").getParentFile(), "base-" + System.currentTimeMillis());
      root.delete();
      root.mkdirs();
      this.store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(root, "STORE"), root, 1024 * 1024, 64 * 1024, 1024 * 1024 * 32);
      this.keyToIdService = new KeyToIdService() {
        @Override
        public void translate(Key key, Callback<Long> callback) {
          callback.success(Long.parseLong(key.key));
        }

        @Override
        public void forget(Key key) {
        }
      };
      this.cloud = new MockCloud();
      this.service = new CaravanDataService(cloud, keyToIdService, store, executor);
      this.flusher = new Thread(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              Thread.sleep(0, 800000);
              service.flush(false).await(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
              return;
            }
          }
        }
      });
      this.flusher.start();
    }

    private void clean() throws Exception {
      service.flush(true).await(10000, TimeUnit.MILLISECONDS);
      this.flusher.interrupt();
      this.flusher.join();
      store.shutdown();
    }
  }

  private void flow(BadConsumer<Setup> test) throws Exception {
    Setup setup = new Setup();
    try {
      test.accept(setup);
    } finally {
      setup.clean();
    }
  }

  @Test
  public void flow() throws Exception {
    flow((setup) -> {
      SimpleDataCallback cb_GetFailed = new SimpleDataCallback();
      setup.service.get(KEY1, cb_GetFailed);
      cb_GetFailed.assertFailure(625676);

      SimpleMockCallback cb_PatchFailsFNF = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchFailsFNF);
      cb_PatchFailsFNF.assertFailure(625676);

      SimpleIntCallback cb_CompactFailsFNF = new SimpleIntCallback();
      setup.service.snapshot(KEY1, new DocumentSnapshot(1, "{}", 1, 1234L), cb_CompactFailsFNF);
      cb_CompactFailsFNF.assertFailure(625676);

      SimpleDataCallback cb_ComputeFailsFNF_Rewind = new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.Rewind, 1, cb_ComputeFailsFNF_Rewind);
      cb_ComputeFailsFNF_Rewind.assertFailure(625676);

      SimpleDataCallback cb_ComputeFailsFNF_HeadPatch= new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_ComputeFailsFNF_HeadPatch);
      cb_ComputeFailsFNF_HeadPatch.assertFailure(625676);

      SimpleMockCallback cb_DeleteSuccessFNF = new SimpleMockCallback();
      setup.service.delete(KEY1, cb_DeleteSuccessFNF);
      cb_DeleteSuccessFNF.assertSuccess();

      SimpleMockCallback cb_InitSuccess = new SimpleMockCallback();
      setup.service.initialize(KEY1, UPDATE_1, cb_InitSuccess);
      cb_InitSuccess.assertSuccess();

      SimpleDataCallback cb_GetWorksNow = new SimpleDataCallback();
      setup.service.get(KEY1, cb_GetWorksNow);
      cb_GetWorksNow.assertSuccess();
      Assert.assertEquals("{\"x\":1,\"y\":4}", cb_GetWorksNow.value);
      Assert.assertEquals(1, cb_GetWorksNow.reads);

      SimpleMockCallback cb_PatchOne = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchOne);
      cb_PatchOne.assertSuccess();

      {
        SimpleMockCallback cb_PatchFourAheadOfSchedule = new SimpleMockCallback();
        setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_4 }, cb_PatchFourAheadOfSchedule);
        cb_PatchFourAheadOfSchedule.assertFailure(621580);
      }

      SimpleMockCallback cb_PatchTwo = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_PatchTwo);
      cb_PatchTwo.assertSuccess();

      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, 2, cb_Rewind);
        cb_Rewind.assertSuccess();
        Assert.assertEquals(1, cb_Rewind.reads);
        Assert.assertEquals("{\"x\":1,\"z\":42}", cb_Rewind.value);
      }
      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_Patch);
        cb_Patch.assertSuccess();
        Assert.assertEquals(1, cb_Patch.reads);
        Assert.assertEquals("{\"x\":4}", cb_Patch.value);
      }

      {
        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);
      }

      {
        SimpleMockCallback cb_Close = new SimpleMockCallback();
        setup.service.close(KEY1, cb_Close);
        cb_Close.assertSuccess();
      }

      {
        SimpleMockCallback cb_Close = new SimpleMockCallback();
        setup.service.close(KEY1, cb_Close);
        cb_Close.assertSuccess();
      }

      {
        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);
      }

      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_Patch);
        cb_Patch.assertSuccess();
        Assert.assertEquals("{\"x\":4}", cb_Patch.value);
      }
      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, -100, cb_Patch);
        cb_Patch.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_Patch.value);
      }
      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 100, cb_Patch);
        cb_Patch.assertFailure(787507);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, 2, cb_Rewind);
        cb_Rewind.assertSuccess();
        Assert.assertEquals(1, cb_Rewind.reads);
        Assert.assertEquals("{\"x\":1,\"z\":42}", cb_Rewind.value);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, -100, cb_Rewind);
        cb_Rewind.assertFailure(791602);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, 100, cb_Rewind);
        cb_Rewind.assertFailure(791602);
      }

      {
        SimpleDataCallback cbUnknown = new SimpleDataCallback();
        setup.service.compute(KEY1, null, 100, cbUnknown);
        cbUnknown.assertFailure(785491);
      }

      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.snapshot(KEY1, new DocumentSnapshot(4, "{\"x\":10,\"y\":10}", 100, 1234L), cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":10,\"y\":10}", cb_GetCompactedResults.value);
        Assert.assertEquals(1, cb_GetCompactedResults.reads);
      }
      {
        SimpleIntCallback cbCompactFailsNegHistory = new SimpleIntCallback();
        setup.service.snapshot(KEY1, new DocumentSnapshot(1, "{}", -1, 1234L), cbCompactFailsNegHistory);
        cbCompactFailsNegHistory.assertFailure(734263);
      }

      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.snapshot(KEY1, new DocumentSnapshot(1, "{\"x\":11,\"y\":11}", 2, 1234L), cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":11}", cb_GetCompactedResults.value);
        Assert.assertEquals(4, cb_GetCompactedResults.reads);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.snapshot(KEY1, new DocumentSnapshot(5, "{\"x\":12,\"y\":12}", 1, 1234L), cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":12,\"y\":12}", cb_GetCompactedResults.value);
        Assert.assertEquals(1, cb_GetCompactedResults.reads);
      }

      SimpleMockCallback cb_InitFailAlreadyExists = new SimpleMockCallback();
      setup.service.initialize(KEY1, UPDATE_1, cb_InitFailAlreadyExists);
      cb_InitFailAlreadyExists.assertFailure(667658);

      {
        SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
        setup.service.delete(KEY1, cb_DeleteWorks);
        cb_DeleteWorks.assertSuccess();
      }
      {
        SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
        setup.service.delete(KEY1, cb_DeleteWorks);
        cb_DeleteWorks.assertSuccess();
      }

      SimpleMockCallback cb_Init2 = new SimpleMockCallback();
      setup.service.initialize(KEY2, UPDATE_1, cb_Init2);
      cb_Init2.assertSuccess();

      {
        SimpleMockCallback cb_Close2 = new SimpleMockCallback();
        setup.service.close(KEY2, cb_Close2);
        cb_Close2.assertSuccess();
      }

      SimpleMockCallback cb_PatchOne2 = new SimpleMockCallback();
      setup.service.patch(KEY2, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchOne2);
      cb_PatchOne2.assertSuccess();

      {
        SimpleMockCallback cb_Close2 = new SimpleMockCallback();
        setup.service.close(KEY2, cb_Close2);
        cb_Close2.assertSuccess();
      }

      SimpleMockCallback cb_PatchTwo2 = new SimpleMockCallback();
      setup.service.patch(KEY2, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_PatchTwo2);
      cb_PatchTwo2.assertSuccess();

      {
        SimpleMockCallback cb_Close2 = new SimpleMockCallback();
        setup.service.close(KEY2, cb_Close2);
        cb_Close2.assertSuccess();
      }

      {
        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        setup.service.get(KEY2, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);
      }
      {
        SimpleMockCallback cb_Close2 = new SimpleMockCallback();
        setup.service.close(KEY2, cb_Close2);
        cb_Close2.assertSuccess();
      }

      final String archiveKey;
      {
        SimpleBackupCallback cb_Backup = new SimpleBackupCallback();
        setup.service.backup(KEY2, cb_Backup);
        cb_Backup.assertSuccess();
        archiveKey = cb_Backup.value.archiveKey;
      }

      {
        SimpleMockCallback cb_Restore = new SimpleMockCallback();
        setup.service.restore(KEY2, archiveKey, cb_Restore);
        cb_Restore.assertSuccess();
      }

      {
        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        setup.service.get(KEY2, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);
      }

      {
        SimpleMockCallback cb_Delete = new SimpleMockCallback();
        setup.service.delete(KEY2, cb_Delete);
        cb_Delete.assertSuccess();
      }

      {
        SimpleMockCallback cb_Restore = new SimpleMockCallback();
        setup.service.restore(KEY2, archiveKey, cb_Restore);
        cb_Restore.assertSuccess();
      }

      {
        SimpleMockCallback cb_RestoreFailedInCloud = new SimpleMockCallback();
        setup.service.restore(KEY2, archiveKey + ".notfound", cb_RestoreFailedInCloud);
        cb_RestoreFailedInCloud.assertFailure(-102);
      }

      {
        SimpleMockCallback cb_RestoreFailed2 = new SimpleMockCallback();
        setup.service.restore(KEY2, "notfound", cb_RestoreFailed2);
        cb_RestoreFailed2.assertFailure(739471);
      }

      {
        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        setup.service.get(KEY2, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);
      }

      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.snapshot(KEY2, new DocumentSnapshot(4, "{\"x\":10,\"y\":10}", 1, 1234L), cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY2, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":10,\"y\":10}", cb_GetCompactedResults.value);
        Assert.assertEquals(1, cb_GetCompactedResults.reads);
      }
    });
  }
}

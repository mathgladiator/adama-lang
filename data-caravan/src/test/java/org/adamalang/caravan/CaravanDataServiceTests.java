package org.adamalang.caravan;

import org.adamalang.caravan.contracts.TranslateKeyService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DurableListStoreMetrics;
import org.adamalang.caravan.mocks.BadConsumer;
import org.adamalang.caravan.mocks.SimpleDataCallback;
import org.adamalang.caravan.mocks.SimpleIntCallback;
import org.adamalang.caravan.mocks.SimpleMockCallback;
import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class CaravanDataServiceTests {
  public static final Key KEY1 = new Key("space", "123");
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
    private final TranslateKeyService translate;
    private final DurableListStore store;
    private final SimpleExecutor executor;
    private final File root;
    private final CaravanDataService service;
    private final Thread flusher;

    public Setup() throws Exception {
      this.executor = SimpleExecutor.create("executor");
      this.root = new File(File.createTempFile("ADAMATEST_", "yyy").getParentFile(), "base-" + System.currentTimeMillis());
      root.delete();
      root.mkdirs();
      this.store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(root, "STORE"), root, 1024 * 1024, 64 * 1024, 1024 * 1024 * 32);
      this.translate = new TranslateKeyService() {
        @Override
        public void lookup(Key key, Callback<Long> callback) {
          callback.success(Long.parseLong(key.key));
        }
      };
      this.service = new CaravanDataService(translate, store, executor);
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
      store.shutdown();
      this.flusher.interrupt();
      this.flusher.join();
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
      /*
      SimpleDataCallback cb_GetFailed = new SimpleDataCallback();
      setup.service.get(KEY1, cb_GetFailed);
      cb_GetFailed.assertFailure(625676);

      SimpleMockCallback cb_PatchFailsFNF = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchFailsFNF);
      cb_PatchFailsFNF.assertFailure(724019);

      SimpleIntCallback cb_CompactFailsFNF = new SimpleIntCallback();
      setup.service.compactAndSnapshot(KEY1, 1, "{}", 1, cb_CompactFailsFNF);
      cb_CompactFailsFNF.assertFailure(724019);

      SimpleDataCallback cb_ComputeFailsFNF_Rewind = new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.Rewind, 1, cb_ComputeFailsFNF_Rewind);
      cb_ComputeFailsFNF_Rewind.assertFailure(724019);

      SimpleDataCallback cb_ComputeFailsFNF_HeadPatch= new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_ComputeFailsFNF_HeadPatch);
      cb_ComputeFailsFNF_HeadPatch.assertFailure(724019);
*/

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

      /*
      {
        SimpleMockCallback cb_PatchFourAheadOfSchedule = new SimpleMockCallback();
        setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_4 }, cb_PatchFourAheadOfSchedule);
        cb_PatchFourAheadOfSchedule.assertFailure(621580);
      }

*/

      SimpleMockCallback cb_PatchTwo = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_PatchTwo);
      cb_PatchTwo.assertSuccess();

      /*
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

*/
      SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
      setup.service.get(KEY1, cb_GetIsMergedResults);
      cb_GetIsMergedResults.assertSuccess();
      Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
      Assert.assertEquals(4, cb_GetIsMergedResults.reads);

      /*
      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_Patch);
        cb_Patch.assertFailure(787507);
      }
      {
        SimpleDataCallback cb_Patch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, -100, cb_Patch);
        cb_Patch.assertFailure(787507);
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
        cbUnknown.assertFailure(732208);
      }
      */
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, 4, "{\"x\":10,\"y\":10}", 100, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":10,\"y\":10}", cb_GetCompactedResults.value);
        Assert.assertEquals(1, cb_GetCompactedResults.reads);
      }
      /*
      {
        SimpleIntCallback cbCompactFailsNegHistory = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, 1, "{}", -1, cbCompactFailsNegHistory);
        cbCompactFailsNegHistory.assertFailure(734263);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, 1, "{\"x\":11,\"y\":11}",2, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(2);
        setup.flush(KEY1);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":11,\"y\":11}", cb_GetCompactedResults.value);
        Assert.assertEquals(3, cb_GetCompactedResults.reads);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, 1, "{\"x\":12,\"y\":12}", 1, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(2);
        setup.flush(KEY1);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":12,\"y\":12}", cb_GetCompactedResults.value);
        Assert.assertEquals(2, cb_GetCompactedResults.reads);
      }

*/
      SimpleMockCallback cb_InitFailAlreadyExists = new SimpleMockCallback();
      setup.service.initialize(KEY1, UPDATE_1, cb_InitFailAlreadyExists);
      cb_InitFailAlreadyExists.assertFailure(667658);
      /*

      {
        SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
        setup.service.delete(KEY1, cb_DeleteWorks);
        cb_DeleteWorks.assertSuccess();
      }
      setup.flush(KEY1);
      {
        SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
        setup.service.delete(KEY1, cb_DeleteWorks);
        cb_DeleteWorks.assertSuccess();
      }
       */
    });
  }
}

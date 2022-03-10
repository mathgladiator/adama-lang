/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.disk.demo.BadConsumer;
import org.adamalang.disk.mocks.SimpleDataCallback;
import org.adamalang.disk.mocks.SimpleIntCallback;
import org.adamalang.disk.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.ComputeMethod;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DiskDataServiceTests {
  public static final Key KEY1 = new Key("space", "key1");
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
    private final SimpleExecutor executor;
    private final File root;
    private final DiskBase base;
    private final WriteAheadLog log;
    private final DiskDataService service;

    public Setup() throws Exception {
      this.executor = SimpleExecutor.create("executor");
      this.root = new File(File.createTempFile("ADAMATEST_", "yyy").getParentFile(), "base-" + System.currentTimeMillis());
      this.base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), executor, root);
      this.log = new WriteAheadLog(base, 8196, 1000000, 64 * 1024);
      this.service = new DiskDataService(base, log);
    }

    public void flush(Key key) throws Exception {
      CountDownLatch latch = new CountDownLatch(1);
      executor.execute(new NamedRunnable("flushing-key", key.space, key.key) {
        @Override
        public void execute() throws Exception {
          base.memory.get(key).flush();
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    private void clean() {
      log.close();
      base.shutdown();
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

      SimpleMockCallback cb_PatchTwo = new SimpleMockCallback();
      setup.service.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_PatchTwo);
      cb_PatchTwo.assertSuccess();

      SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
      setup.service.get(KEY1, cb_GetIsMergedResults);
      cb_GetIsMergedResults.assertSuccess();
      Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
      Assert.assertEquals(4, cb_GetIsMergedResults.reads);

      /*
      {
        SimpleDataCallback cb_HeadPatch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_HeadPatch);
        cb_HeadPatch.assertSuccess();
        Assert.assertEquals(4, cb_HeadPatch.reads);
        Assert.assertEquals("{\"x\":4}", cb_HeadPatch.value);
      }
      {
        SimpleDataCallback cb_HeadPatch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, -100, cb_HeadPatch);
        cb_HeadPatch.assertSuccess();
        Assert.assertEquals(4, cb_HeadPatch.reads);
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_HeadPatch.value);
      }
      {
        SimpleDataCallback cb_HeadPatch = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.HeadPatch, 100, cb_HeadPatch);
        cb_HeadPatch.assertFailure(725039);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, 2, cb_Rewind);
        cb_Rewind.assertSuccess();
        Assert.assertEquals(4, cb_Rewind.reads);
        Assert.assertEquals("{\"x\":1,\"z\":42}", cb_Rewind.value);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, -100, cb_Rewind);
        cb_Rewind.assertSuccess();
        Assert.assertEquals(4, cb_Rewind.reads);
        Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", cb_Rewind.value);
      }
      {
        SimpleDataCallback cb_Rewind = new SimpleDataCallback();
        setup.service.compute(KEY1, ComputeMethod.Rewind, 100, cb_Rewind);
        cb_Rewind.assertFailure(783395);
      }
      */
      {
        SimpleDataCallback cbUnknown = new SimpleDataCallback();
        setup.service.compute(KEY1, null, 100, cbUnknown);
        cbUnknown.assertFailure(732208);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, 1, "{\"x\":10,\"y\":10}", 100, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        setup.flush(KEY1);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":10,\"y\":10}", cb_GetCompactedResults.value);
        Assert.assertEquals(4, cb_GetCompactedResults.reads);
      }
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

      SimpleMockCallback cb_InitFailAlreadyExists = new SimpleMockCallback();
      setup.service.initialize(KEY1, UPDATE_1, cb_InitFailAlreadyExists);
      cb_InitFailAlreadyExists.assertFailure(667658);

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
    });
  }
}

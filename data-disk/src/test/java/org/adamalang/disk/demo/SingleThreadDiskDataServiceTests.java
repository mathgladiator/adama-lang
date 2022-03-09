/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.disk.mocks.SimpleDataCallback;
import org.adamalang.disk.mocks.SimpleIntCallback;
import org.adamalang.disk.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class SingleThreadDiskDataServiceTests {
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
    private final File root;
    public final SingleThreadDiskDataService service;

    public Setup() throws Exception {
      this.root = new File(File.createTempFile("xxx", "yyy").getParentFile(), "disk-data-" + System.currentTimeMillis());
      service = new SingleThreadDiskDataService(root, new DiskMetrics(new NoOpMetricsFactory()));
    }

    private void clean() {
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
  public void failedToCreateDirectory() throws Exception {
    File file = File.createTempFile("xxx", "yyy");
    Files.writeString(file.toPath(), "x");
    try {
      new SingleThreadDiskDataService(file, new DiskMetrics(new NoOpMetricsFactory()));
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertTrue(re.getMessage().startsWith("failed to create root directory:"));
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
      cb_PatchFailsFNF.assertFailure(794627);

      SimpleIntCallback cb_CompactFailsFNF = new SimpleIntCallback();
      setup.service.compactAndSnapshot(KEY1, "{}", 1, cb_CompactFailsFNF);
      cb_CompactFailsFNF.assertFailure(784401);

      SimpleDataCallback cb_ComputeFailsFNF_Rewind = new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.Rewind, 1, cb_ComputeFailsFNF_Rewind);
      cb_ComputeFailsFNF_Rewind.assertFailure(790544);

      SimpleDataCallback cb_ComputeFailsFNF_HeadPatch= new SimpleDataCallback();
      setup.service.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_ComputeFailsFNF_HeadPatch);
      cb_ComputeFailsFNF_HeadPatch.assertFailure(790544);

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
      {
        SimpleDataCallback cbUnknown = new SimpleDataCallback();
        setup.service.compute(KEY1, null, 100, cbUnknown);
        cbUnknown.assertFailure(705583);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, "{}", 100, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(0);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetCompactedResults.value);
        Assert.assertEquals(4, cb_GetCompactedResults.reads);
      }
      {
        SimpleIntCallback cbCompactFailsNegHistory = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, "{}", -1, cbCompactFailsNegHistory);
        cbCompactFailsNegHistory.assertFailure(777259);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, "{}",2, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(2);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetCompactedResults.value);
        Assert.assertEquals(2, cb_GetCompactedResults.reads);
      }
      {
        SimpleIntCallback cb_CompactWorks = new SimpleIntCallback();
        setup.service.compactAndSnapshot(KEY1, "{}", 1, cb_CompactWorks);
        cb_CompactWorks.assertSuccess(1);
        SimpleDataCallback cb_GetCompactedResults = new SimpleDataCallback();
        setup.service.get(KEY1, cb_GetCompactedResults);
        cb_GetCompactedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetCompactedResults.value);
        Assert.assertEquals(1, cb_GetCompactedResults.reads);
      }

      SimpleMockCallback cb_InitFailAlreadyExists = new SimpleMockCallback();
      setup.service.initialize(KEY1, UPDATE_1, cb_InitFailAlreadyExists);
      cb_InitFailAlreadyExists.assertFailure(667658);

      SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
      setup.service.delete(KEY1, cb_DeleteWorks);
      cb_DeleteWorks.assertSuccess();

    });
  }
}

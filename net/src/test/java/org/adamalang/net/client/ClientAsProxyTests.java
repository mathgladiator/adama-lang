/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.mocks.SimpleDataCallback;
import org.adamalang.net.client.mocks.SimpleIntCallback;
import org.adamalang.net.client.mocks.SimpleMockCallback;
import org.adamalang.net.client.proxy.ProxyDataService;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ClientAsProxyTests {
  public static final Key KEY1 = new Key("space", "key1");
  private static final RemoteDocumentUpdate UPDATE_1 =
      new RemoteDocumentUpdate(
          1, 1, NtPrincipal.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);
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
  public void flow() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12500,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        ClientTests.waitForRouting(bed, client);
        AtomicReference<ProxyDataService> proxyRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.getProxy("127.0.0.1:12500", new Callback<ProxyDataService>() {
          @Override
          public void success(ProxyDataService value) {
            proxyRef.set(value);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        ProxyDataService proxy = proxyRef.get();
        Assert.assertNotNull(proxy);

        SimpleDataCallback cb_GetFailed = new SimpleDataCallback();
        proxy.get(KEY1, cb_GetFailed);
        cb_GetFailed.assertFailure(625676);

        SimpleMockCallback cb_PatchFailsFNF = new SimpleMockCallback();
        proxy.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchFailsFNF);
        cb_PatchFailsFNF.assertFailure(144944);

        SimpleIntCallback cb_CompactFailsFNF = new SimpleIntCallback();
        proxy.snapshot(KEY1, new DocumentSnapshot(1, "{}", 1, 1234L), cb_CompactFailsFNF);
        cb_CompactFailsFNF.assertFailure(103060);

        SimpleDataCallback cb_ComputeFailsFNF_Rewind = new SimpleDataCallback();
        proxy.compute(KEY1, ComputeMethod.Rewind, 1, cb_ComputeFailsFNF_Rewind);
        cb_ComputeFailsFNF_Rewind.assertFailure(106546);

        SimpleDataCallback cb_ComputeFailsFNF_HeadPatch= new SimpleDataCallback();
        proxy.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_ComputeFailsFNF_HeadPatch);
        cb_ComputeFailsFNF_HeadPatch.assertFailure(106546);

        SimpleMockCallback cb_DeleteSuccessFNF = new SimpleMockCallback();
        proxy.delete(KEY1, cb_DeleteSuccessFNF);
        cb_DeleteSuccessFNF.assertFailure(117816);

        SimpleMockCallback cb_InitSuccess = new SimpleMockCallback();
        proxy.initialize(KEY1, UPDATE_1, cb_InitSuccess);
        cb_InitSuccess.assertSuccess();

        SimpleDataCallback cb_GetWorksNow = new SimpleDataCallback();
        proxy.get(KEY1, cb_GetWorksNow);
        cb_GetWorksNow.assertSuccess();
        Assert.assertEquals("{\"x\":1,\"y\":4}", cb_GetWorksNow.value);
        Assert.assertEquals(1, cb_GetWorksNow.reads);

        SimpleMockCallback cb_PatchOne = new SimpleMockCallback();
        proxy.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_PatchOne);
        cb_PatchOne.assertSuccess();

        {
          SimpleMockCallback cb_PatchFourAheadOfSchedule = new SimpleMockCallback();
          proxy.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_4 }, cb_PatchFourAheadOfSchedule);
          cb_PatchFourAheadOfSchedule.assertFailure(621580);
        }

        SimpleMockCallback cb_PatchTwo = new SimpleMockCallback();
        proxy.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_PatchTwo);
        cb_PatchTwo.assertSuccess();

        {
          SimpleDataCallback cb_Rewind = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.Rewind, 2, cb_Rewind);
          cb_Rewind.assertSuccess();
          Assert.assertEquals(3, cb_Rewind.reads);
          Assert.assertEquals("{\"x\":1,\"z\":42}", cb_Rewind.value);
        }
        {
          SimpleDataCallback cb_Patch = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_Patch);
          cb_Patch.assertSuccess();
          Assert.assertEquals(2, cb_Patch.reads);
          Assert.assertEquals("{\"x\":4}", cb_Patch.value);
        }

        SimpleDataCallback cb_GetIsMergedResults = new SimpleDataCallback();
        proxy.get(KEY1, cb_GetIsMergedResults);
        cb_GetIsMergedResults.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_GetIsMergedResults.value);
        Assert.assertEquals(4, cb_GetIsMergedResults.reads);

        {
          SimpleDataCallback cb_Patch = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.HeadPatch, 2, cb_Patch);
          cb_Patch.assertSuccess();
        }
        {
          SimpleDataCallback cb_Patch = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.HeadPatch, 100, cb_Patch);
          cb_Patch.assertFailure(120944);
        }
        {
          SimpleDataCallback cb_Rewind = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.Rewind, 2, cb_Rewind);
          cb_Rewind.assertSuccess();
          Assert.assertEquals(3, cb_Rewind.reads);
          Assert.assertEquals("{\"x\":1,\"z\":42}", cb_Rewind.value);
        }
        {
          SimpleDataCallback cb_Rewind = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.Rewind, -100, cb_Rewind);
          cb_Rewind.assertSuccess();
          Assert.assertEquals("{\"x\":0,\"z\":42,\"y\":0}", cb_Rewind.value);
        }
        {
          SimpleDataCallback cb_Rewind = new SimpleDataCallback();
          proxy.compute(KEY1, ComputeMethod.Rewind, 100, cb_Rewind);
          cb_Rewind.assertFailure(128052);
        }

        {
          SimpleDataCallback cbUnknown = new SimpleDataCallback();
          proxy.compute(KEY1, null, 100, cbUnknown);
          cbUnknown.assertFailure(127034);
        }

        SimpleMockCallback cb_InitFailAlreadyExists = new SimpleMockCallback();
        proxy.initialize(KEY1, UPDATE_1, cb_InitFailAlreadyExists);
        cb_InitFailAlreadyExists.assertFailure(667658);

        SimpleIntCallback cb_Snapshot = new SimpleIntCallback();
        proxy.snapshot(KEY1, new DocumentSnapshot(3, "{}", 0, 1234L), cb_Snapshot);
        cb_Snapshot.assertSuccess(3);

        {
          SimpleMockCallback cb_DeleteWorks = new SimpleMockCallback();
          proxy.delete(KEY1, cb_DeleteWorks);
          cb_DeleteWorks.assertSuccess();
        }
      } finally{
        client.shutdown();
      }
    }
  }

}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.client.routing.cache.RoutingTableTarget;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LocalFinderTests {

  @Test
  public void flow() throws Exception {
    try (TestBed bed =
             new TestBed(
                 13542,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new LocalRegionClientMetrics(new NoOpMetricsFactory())), null);
      try {
        LocalRegionClientTests.waitForRouting(bed, client);

        CountDownLatch latch = new CountDownLatch(2);
        client.finder.find(new Key("space", "key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation value) {
            Assert.assertEquals("test-region", value.region);
            Assert.assertEquals("the-machine", value.machine);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });

        client.finder.find(new Key("this-space-can-exist", "key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation value) {
            Assert.assertEquals("test-region", value.region);
            Assert.assertEquals("the-machine", value.machine);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));



      } finally{
        client.shutdown();
      }
    }
  }
}

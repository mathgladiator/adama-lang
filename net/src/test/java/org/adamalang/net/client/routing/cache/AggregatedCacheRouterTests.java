/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AggregatedCacheRouterTests {
  @Test
  public void flow() throws Exception {
    AggregatedCacheRouter router = new AggregatedCacheRouter(SimpleExecutor.NOW);
    CountDownLatch latch = new CountDownLatch(7);
    router.random((host) -> {
      Assert.assertNull(host);
      latch.countDown();
    });
    router.list("space", (list) -> {
      Assert.assertEquals(0, list.size());
      latch.countDown();
    });
    router.integrate("t1", Collections.singleton("space"));
    router.random((host) -> {
      Assert.assertEquals("t1", host);
      latch.countDown();
    });
    router.integrate("t2", Collections.singleton("space"));
    router.integrate("t3", Collections.singleton("space"));
    router.get(new Key("space", "key1"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t2", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.get(new Key("space", "key2"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t1", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.get(new Key("space", "key5"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t3", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.remove("t2");
    router.get(new Key("space", "key1"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t3", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }
}

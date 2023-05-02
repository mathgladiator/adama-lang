/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MeteringPubSubTests {
  @Test
  public void flow() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"a\":\"public int x; @construct { x = 100; } @connected { return true; }\"},\"default\":\"a\"}",
            (t, errorCode) -> {
              t.printStackTrace();
            });
    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);
    MeteringPubSub pubsub = new MeteringPubSub(new MockTime(), base);
    {
      AtomicInteger pubs = new AtomicInteger(0);
      CountDownLatch latch = new CountDownLatch(10);
      Assert.assertEquals(0, pubsub.size());
      pubsub.subscribe((x) -> {
        latch.countDown();
        return pubs.getAndIncrement() < 5;
      });
      Assert.assertEquals(1, pubsub.size());
      Consumer<HashMap<String, PredictiveInventory.MeteringSample>> publisher = pubsub.publisher();
      HashMap<String, PredictiveInventory.MeteringSample> map = new HashMap<>();
      map.put("space", new PredictiveInventory.MeteringSample(0, 1, 2, 3, 4, 5, 6, 7));
      for (int k = 0; k < 11; k++) {
        publisher.accept(map);
      }
      Assert.assertEquals(0, pubsub.size());
      latch.await(1000, TimeUnit.MILLISECONDS);
      Assert.assertEquals(6, pubs.get());
    }
    {
      AtomicInteger pubs = new AtomicInteger(0);
      Assert.assertEquals(0, pubsub.size());
      pubsub.subscribe((x) -> {
        pubs.getAndIncrement();
        return false;
      });
      Assert.assertEquals(0, pubsub.size());
      Assert.assertEquals(1, pubs.get());
      pubsub.subscribe((x) -> {
        pubs.getAndIncrement();
        return true;
      });
      Assert.assertEquals(1, pubsub.size());
      Assert.assertEquals(2, pubs.get());
    }
  }
}

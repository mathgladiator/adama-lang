package org.adamalang.grpc.client.routing;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.runtime.contracts.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RoutingEngineTests {
  @Test
  public void flow() throws Exception {
    MockSpaceTrackingEvents events = new MockSpaceTrackingEvents();
    RoutingEngine engine = new RoutingEngine(SimpleExecutor.create("derp"), events, 50, 25);

    AtomicReference<Runnable> cancelRunnable = new AtomicReference<>();
    CountDownLatch latchGotCancel = new CountDownLatch(1);
    CountDownLatch becameZ = new CountDownLatch(1);
    CountDownLatch becameW = new CountDownLatch(1);
    CountDownLatch becameWAgain = new CountDownLatch(2);

    engine.subscribe(new Key("space", "key"), (target) -> {
      System.err.println(target);
      if ("z".equals(target)) {
        becameZ.countDown();
      }
      if ("w".equals(target)) {
        becameW.countDown();
        becameWAgain.countDown();
      }
    }, (cancel) -> {
      cancelRunnable.set(cancel);
      latchGotCancel.countDown();
    });
    Assert.assertTrue(latchGotCancel.await(10000, TimeUnit.MILLISECONDS));
    // NOTE: this list was built to be adversarial, so each one will trigger an immediate change without the broadcast delay
    // you can play with this by increase the thread sleep to 200 and each thing will pop out
    for (String inj : new String[] { "y", "3", "t", "4", "w", "2", "1", "x", "z"}) {
      engine.integrate(inj, Collections.singleton("space"));
      if ("w".equals(inj)) {
        // we checkpoint on W because we will be removing a bunch of stuff
        Assert.assertTrue(becameW.await(10000, TimeUnit.MILLISECONDS));
      }
      Thread.sleep(25);
    }
    Assert.assertTrue(becameZ.await(10000, TimeUnit.MILLISECONDS));
    for (String kill : new String[] {"2", "1", "x", "z"}) {
      engine.remove(kill);
    }
    Assert.assertTrue(becameWAgain.await(10000, TimeUnit.MILLISECONDS));
  }
}

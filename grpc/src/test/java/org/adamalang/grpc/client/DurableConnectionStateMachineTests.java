package org.adamalang.grpc.client;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class DurableConnectionStateMachineTests {

  @Test
  public void streamPersists() {
    CountDownLatch toProvide = new CountDownLatch(5);
    AtomicReference<InstanceClient> client = new AtomicReference<>(null);
    InstanceClientFinder finder = (space, key, callback) -> {
      toProvide.countDown();
      InstanceClient test = client.get();
      if (test == null) {
         callback.nope();
      } else {
        callback.found(test);
      }
    };
    DurableClientBase base = new DurableClientBase(finder, Executors.newSingleThreadScheduledExecutor());
    base.shutdown();
  }
}

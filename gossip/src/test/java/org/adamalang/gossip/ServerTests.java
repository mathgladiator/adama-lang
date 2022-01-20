/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import io.grpc.stub.StreamObserver;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.gossip.proto.GossipReverse;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTests {
  @Test
  public void shutdownProducesQuickComplete() {
    AtomicBoolean alive = new AtomicBoolean(false);
    ServerHandler handler = new ServerHandler(SimpleExecutor.NOW, null, alive, new MockGossipMetrics());

    AtomicBoolean done = new AtomicBoolean(false);
    handler.exchange(
        new StreamObserver<GossipReverse>() {
          @Override
          public void onNext(GossipReverse gossipReverse) {}

          @Override
          public void onError(Throwable throwable) {}

          @Override
          public void onCompleted() {
            done.set(true);
          }
        });
    Assert.assertTrue(done.get());
  }
}

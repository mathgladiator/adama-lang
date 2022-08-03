package org.adamalang.net.client;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class TargetsQuorumTests {
  @Test
  public void quorom() {
    AtomicReference<Collection<String>> last = new AtomicReference<>();
    TargetsQuorum quorum = new TargetsQuorum(new ClientMetrics(new NoOpMetricsFactory()), last::set);
    quorum.deliverDatabase(Collections.singleton("X"));
    quorum.deliverGossip(Collections.singleton("X"));
    Assert.assertEquals(1, last.get().size());
  }
}

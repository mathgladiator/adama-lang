/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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

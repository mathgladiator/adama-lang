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

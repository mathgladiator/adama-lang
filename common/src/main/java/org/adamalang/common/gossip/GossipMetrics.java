/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipMetrics {
  /*
  private static final Logger LOGGER = LoggerFactory.getLogger(GossipMetrics.class);

  public final Runnable gossip_wake;
  public final Runnable gossip_sad;
  public final Runnable gossip_slow_c;
  public final Runnable gossip_optimistic;
  public final Runnable gossip_turn;
  public final Runnable start;
  public final Runnable foundReverse;
  public final Runnable quickGossip;
  public final Runnable serverSlowGossip;
  public final Inflight inflight;
  */

  public GossipMetrics(MetricsFactory factory) {
    /*
    gossip_wake = factory.counter("gossip_wake");
    gossip_sad = factory.counter("gossip_sad");
    gossip_slow_c = factory.counter("gossip_slow_c");
    gossip_optimistic = factory.counter("gossip_optimistic");
    gossip_turn = factory.counter("gossip_turn");
    start = factory.counter("gossip_start");
    foundReverse = factory.counter("gossip_found_rev");
    quickGossip = factory.counter("gossip_quick");
    serverSlowGossip = factory.counter("gossip_slow_s");
    inflight = factory.inflight("gossip_inflight");
    */
  }
}

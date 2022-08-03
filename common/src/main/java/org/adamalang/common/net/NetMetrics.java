/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.net;

import org.adamalang.common.gossip.GossipMetrics;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the network */
public class NetMetrics {
  public final Runnable net_create_client_handler;
  public final Runnable net_create_server_handler;

  public final GossipMetrics gossip;

  public NetMetrics(MetricsFactory factory) {
    this.net_create_client_handler = factory.counter("net_create_client_handler");
    this.net_create_server_handler = factory.counter("net_create_server_handler");
    this.gossip = new GossipMetrics(factory);
  }
}

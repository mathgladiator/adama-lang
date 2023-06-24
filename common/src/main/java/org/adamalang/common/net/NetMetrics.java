/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

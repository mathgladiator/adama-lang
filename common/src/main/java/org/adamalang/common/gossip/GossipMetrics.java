/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class GossipMetrics {
  public final Inflight gossip_active_clients;
  public final Runnable gossip_wake;
  public final Runnable gossip_send_begin;
  public final Runnable gossip_read_reverse_slow_gossip;
  public final Runnable gossip_read_reverse_quick_gossip;
  public final Runnable gossip_read_hash_not_found;
  public final Runnable gossip_send_reverse_hash_found;
  public final Runnable gossip_send_forward_slow_gossip;
  public final Runnable gossip_read_hash_found_forward_quick_gossip;
  public final Runnable gossip_read_forward_slow_gossip;
  public final Runnable gossip_read_reverse_hash_found;
  public final Runnable gossip_read_forward_quick_gossip;
  public final Runnable gossip_read_begin_gossip;
  public final Runnable gossip_send_hash_found;
  public final Runnable gossip_send_hash_not_found;
  public final Inflight gossip_inflight;

  public GossipMetrics(MetricsFactory factory) {
    gossip_active_clients = factory.inflight("gossip_active_clients");
    gossip_wake = factory.counter("gossip_wake");
    gossip_send_begin = factory.counter("gossip_send_begin");
    gossip_read_reverse_slow_gossip = factory.counter("gossip_read_reverse_slow_gossip");
    gossip_read_reverse_quick_gossip = factory.counter("gossip_read_reverse_quick_gossip");
    gossip_read_hash_not_found = factory.counter("gossip_read_hash_not_found");
    gossip_send_reverse_hash_found = factory.counter("gossip_send_reverse_hash_found");
    gossip_send_forward_slow_gossip = factory.counter("gossip_send_forward_slow_gossip");
    gossip_read_hash_found_forward_quick_gossip = factory.counter("gossip_read_hash_found_forward_quick_gossip");
    gossip_read_forward_slow_gossip = factory.counter("gossip_read_forward_slow_gossip");
    gossip_read_reverse_hash_found = factory.counter("gossip_read_reverse_hash_found");
    gossip_read_forward_quick_gossip = factory.counter("gossip_read_forward_quick_gossip");
    gossip_read_begin_gossip = factory.counter("gossip_read_begin_gossip");
    gossip_send_hash_found = factory.counter("gossip_send_hash_found");
    gossip_send_hash_not_found = factory.counter("gossip_send_hash_not_found");
    gossip_inflight = factory.inflight("gossip_inflight");
  }
}

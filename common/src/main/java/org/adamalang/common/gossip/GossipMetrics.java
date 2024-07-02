/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

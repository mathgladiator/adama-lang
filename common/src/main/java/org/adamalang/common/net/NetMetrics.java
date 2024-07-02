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

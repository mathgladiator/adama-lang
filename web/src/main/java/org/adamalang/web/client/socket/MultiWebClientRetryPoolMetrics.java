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
package org.adamalang.web.client.socket;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for maintaining a connection to a websocket endpoint */
public class MultiWebClientRetryPoolMetrics {
  public final ItemActionMonitor queue;
  public final Runnable disconnected;
  public final Runnable failure;
  public final Runnable slow;
  public final Inflight inflight;

  public MultiWebClientRetryPoolMetrics(MetricsFactory factory) {
    queue = factory.makeItemActionMonitor("mwcr_pool_queue");
    disconnected = factory.counter("mwcr_pool_disconnected");
    failure = factory.counter("mwcr_pool_failure");
    slow = factory.counter("mwcr_pool_slow");
    inflight = factory.inflight("mwcr_pool_inflight");
  }
}

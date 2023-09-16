/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.net.client.routing.finder;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.net.client.routing.cache.AggregatedCacheRouter;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.capacity.MachinePicker;

@Deprecated
public class ReactiveCacheMachinePicker implements MachinePicker {
  private final LocalRegionClientMetrics metrics;
  private final AggregatedCacheRouter engine;
  private final MachinePicker fallback;

  public ReactiveCacheMachinePicker(LocalRegionClientMetrics metrics, AggregatedCacheRouter engine, MachinePicker fallback) {
    this.metrics = metrics;
    this.engine = engine;
    this.fallback = fallback;
  }

  @Override
  public void pickHost(Key key, Callback<String> callback) {
    engine.get(key, new RoutingCallback() {
      @Override
      public void onRegion(String region) {
        // impossible for now
        callback.failure(new ErrorCodeException(ErrorCodes.NET_FINDER_ROUTER_REGION_NOT_EXPECTED));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }

      @Override
      public void onMachine(String machine) {
        if (machine == null) {
          metrics.client_rxcache_fallback.run();
          fallback.pickHost(key, callback);
        } else {
          metrics.client_rxcache_found.run();
          callback.success(machine);
        }
      }
    });
  }
}

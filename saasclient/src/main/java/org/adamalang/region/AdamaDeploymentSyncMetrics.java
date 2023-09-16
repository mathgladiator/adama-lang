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
package org.adamalang.region;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.StreamMonitor;

public class AdamaDeploymentSyncMetrics {
  public final CallbackMonitor adamasync_connected;
  public final Inflight adamasync_watching;
  public final StreamMonitor adamasync_streaming_update;

  public AdamaDeploymentSyncMetrics(MetricsFactory factory) {
    this.adamasync_connected = factory.makeCallbackMonitor("adamasync_connected");
    this.adamasync_watching = factory.inflight("adamasync_watching");
    this.adamasync_streaming_update = factory.makeStreamMonitor("adamasync_streaming_update");
  }
}

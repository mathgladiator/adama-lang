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
package org.adamalang;

import org.adamalang.api.SelfClient;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.internal.InternalSigner;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.metrics.ThirdPartyMetrics;
import org.adamalang.runtime.remote.ServiceConfigFactory;
import org.adamalang.web.client.WebClientBase;

/** the large nexus for the external services */
public class CoreServicesNexus {
  public final SimpleExecutor executor;
  public final SimpleExecutor offload;
  public final FirstPartyMetrics fpMetrics;
  public final ThirdPartyMetrics tpMetrics;
  public final WebClientBase webClientBase;
  public final SelfClient adamaClientRaw;
  public final InternalSigner signer;
  public final ServiceConfigFactory serviceConfigFactory;
  public final ServiceLogger logger;

  public CoreServicesNexus(SimpleExecutor executor, SimpleExecutor offload, MetricsFactory factory, WebClientBase webClientBase, SelfClient adamaClientRaw, InternalSigner signer, ServiceConfigFactory serviceConfigFactory) {
    this.executor = executor;
    this.offload = offload;
    this.fpMetrics = new FirstPartyMetrics(factory);
    this.tpMetrics = new ThirdPartyMetrics(factory);
    this.webClientBase = webClientBase;
    this.adamaClientRaw = adamaClientRaw;
    this.signer = signer;
    this.serviceConfigFactory = serviceConfigFactory;
    this.logger = new ServiceLogger();
  }

  public static CoreServicesNexus NOOP() {
    return new CoreServicesNexus(SimpleExecutor.NOW, SimpleExecutor.NOW, new NoOpMetricsFactory(), null, null, null, null);
  }
}

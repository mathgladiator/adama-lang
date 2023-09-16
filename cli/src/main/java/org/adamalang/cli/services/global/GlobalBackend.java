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
package org.adamalang.cli.services.global;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.Role;
import org.adamalang.cli.services.common.EveryMachine;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.deploy.DeploymentMetrics;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

public class GlobalBackend {
  public static void run(Config config) throws Exception {
    EveryMachine em = new EveryMachine(config, Role.Adama);
    DataBaseBoot db = new DataBaseBoot(em.alive, config, em.metricsFactory, em.system);

    CoreMetrics coreMetrics = new CoreMetrics(em.metricsFactory);
    DeploymentMetrics deploymentMetrics = new DeploymentMetrics(em.metricsFactory);
    // pull config
    int coreThreads = config.get_int("service-thread-count", 8);
    String billingRootPath = config.get_string("billing-path", "billing");
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();

    // TODO: use the new Ondemand Factory Base HERE

    // CaravanBoot caravan = new CaravanBoot(init.alive, config.get_string("caravan-root", "caravan"), em.metricsFactory, em.region, em.machine, em.globalFinder, em.s3, em.s3);


    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    // CoreService service = new CoreService(coreMetrics, factoryProxy, meteringPubSub.publisher(), caravan.service, TimeSource.REAL_TIME, coreThreads);

    ServiceHeatEstimator.HeatVector low = config.get_heat("heat-low", 1, 100, 1, 100);
    ServiceHeatEstimator.HeatVector hot = config.get_heat("heat-hot", 1000, 100000, 250, 2000);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, hot);
    meteringPubSub.subscribe(estimator);
  }
}

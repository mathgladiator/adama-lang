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
package org.adamalang.system.distributed;

import org.adamalang.caravan.CaravanBoot;
import org.adamalang.mysql.impl.*;
import org.adamalang.system.CommonServiceInit;
import org.adamalang.system.Role;
import org.adamalang.common.*;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.region.AdamaDeploymentSync;
import org.adamalang.region.AdamaDeploymentSyncMetrics;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.deploy.*;
import org.adamalang.runtime.sys.*;
import org.adamalang.runtime.sys.capacity.CapacityAgent;
import org.adamalang.runtime.sys.capacity.CapacityMetrics;
import org.adamalang.runtime.sys.capacity.CapacityOverseer;
import org.adamalang.runtime.sys.metering.*;
import org.adamalang.system.contracts.JsonConfig;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Backend {
  private static final Logger LOGGER = LoggerFactory.getLogger(Backend.class);
  public final CommonServiceInit init;
  public final Thread serverThread;

  public Backend(CommonServiceInit init, Thread serverThread) {
    this.init = init;
    this.serverThread = serverThread;
  }

  public static Backend run(JsonConfig config) throws Exception {
    // create the common/shared service issues
    CommonServiceInit init = new CommonServiceInit(config, Role.Adama);
    // create metrics
    CoreMetrics coreMetrics = new CoreMetrics(init.metricsFactory);
    // pull config
    int coreThreads = config.get_int("service-thread-count", 8);
    String billingRootPath = config.get_string("billing-path", "billing");

    PlanFetcher fetcher = new GlobalPlanFetcher(init.database, init.masterKey);

    ManagedAsyncByteCodeCache managedByteCodeCache = new ManagedAsyncByteCodeCache(init.s3, init.em.compileOffload, init.deploymentMetrics);
    CachedAsyncByteCodeCache cachedAsyncByteCodeCache = new CachedAsyncByteCodeCache(TimeSource.REAL_TIME, 1024, 120000, init.system, managedByteCodeCache);
    cachedAsyncByteCodeCache.startSweeping(init.alive, 30000, 90000);
    RuntimeEnvironment env = ("test".equalsIgnoreCase(init.em.environment) || "beta".equalsIgnoreCase(init.em.environment)) ? RuntimeEnvironment.Beta : RuntimeEnvironment.Production;
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase(cachedAsyncByteCodeCache, env);
    CapacityOverseer overseer = new GlobalCapacityOverseer(init.database);

    DelayedDeploy delayedDeploy = new DelayedDeploy();
    AdamaDeploymentSync syncMonitor = new AdamaDeploymentSync(new AdamaDeploymentSyncMetrics(init.em.metricsFactory), init.em.adamaCurrentRegionClient, init.em.system, init.em.regionalIdentity, delayedDeploy, deploymentFactoryBase);
    GlobalCapacitySync syncWithDatabase = new GlobalCapacitySync(init.database, init.em.region, init.em.machine, init.system, syncMonitor);
    OndemandDeploymentFactoryBase factoryProxy = new OndemandDeploymentFactoryBase(init.deploymentMetrics, deploymentFactoryBase, fetcher, syncWithDatabase);

    BoundLocalFinderService finder = new BoundLocalFinderService(init.system, init.globalFinder, init.region, init.machine);
    CaravanBoot caravan = new CaravanBoot(init.alive, config.get_string("caravan-root", "caravan"), init.metricsFactory, init.region, init.machine, finder, init.s3, init.s3);

    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    GlobalMetricsReporter metricsReporter = new GlobalMetricsReporter(init.database, init.em.metrics);
    CoreService service = new CoreService(coreMetrics, factoryProxy, meteringPubSub.publisher(), metricsReporter, caravan.service, init.s3, TimeSource.REAL_TIME, coreThreads);
    delayedDeploy.set(factoryProxy, service);

    ServiceHeatEstimator.HeatVector low = config.get_heat("heat-low", 1, 100, 1, 100);
    ServiceHeatEstimator.HeatVector hot = config.get_heat("heat-hot", 1000, 100000, 250, 2000);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, hot);
    meteringPubSub.subscribe(estimator);

    CapacityAgent capacityAgent = new CapacityAgent(new CapacityMetrics(init.metricsFactory), overseer, service, deploymentFactoryBase, estimator, init.system, init.alive, service.shield, init.region, init.machine);
    deploymentFactoryBase.attachDeliverer(service);

    meteringPubSub.subscribe((bills) -> {
      estimator.apply(bills);
      return true;
    });

    init.engine.createLocalApplicationHeartbeat("adama", init.servicePort, init.monitoringPort, (hb) -> {
      init.system.execute(new NamedRunnable("heartbeat") {
        @Override
        public void execute() throws Exception {
          hb.run();
          if (init.alive.get()) {
            init.system.schedule(this, 100);
          }
        }
      });
    });

    BillingDocumentFinder billingDocumentFinder = new GlobalBillingDocumentFinder(init.database);

    MeteringBatchReady submitToAdama = init.em.makeMeteringBatchReady(billingDocumentFinder, init.publicKeyId);

    File billingRoot = new File(billingRootPath);
    billingRoot.mkdir();
    DiskMeteringBatchMaker billingBatchMaker = new DiskMeteringBatchMaker(TimeSource.REAL_TIME, SimpleExecutor.create("billing-batch-maker"), billingRoot, 10 * 60000L, submitToAdama);
    meteringPubSub.subscribe((bills) -> {
      for (MeterReading meterReading : bills) {
        billingBatchMaker.write(meterReading);
      }
      return true;
    });

    ServiceBoot.initializeWithDeployments(init.em.region, init.em.machine, overseer, factoryProxy, 1000);
    ServiceBoot.startup(init.globalFinder, service);

    ServerNexus nexus = new ServerNexus(init.netBase, init.identity, service, new ServerMetrics(init.metricsFactory), deploymentFactoryBase, finder, factoryProxy, meteringPubSub, billingBatchMaker, init.servicePort, 4);
    ServerHandle handle = init.netBase.serve(init.servicePort, (upstream) -> new Handler(nexus, upstream));
    Thread serverThread = new Thread(() -> handle.waitForEnd());
    serverThread.start();
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        // billingPubSub.terminate();
        // This will send to all connections an empty list which will remove from the routing table. At this point, we should wait all connections migrate away
        System.err.println("backend shutting down");
        handle.kill();
      }
    })));
    System.err.println("backend running");
    LOGGER.error("Started");
    return new Backend(init, serverThread);
  }
}

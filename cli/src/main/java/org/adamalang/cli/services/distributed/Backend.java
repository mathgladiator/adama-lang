/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.distributed;

import org.adamalang.caravan.CaravanBoot;
import org.adamalang.cli.Config;
import org.adamalang.cli.services.CommonServiceInit;
import org.adamalang.cli.services.Role;
import org.adamalang.common.*;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.mysql.impl.GlobalBillingDocumentFinder;
import org.adamalang.mysql.impl.GlobalCapacityOverseer;
import org.adamalang.mysql.model.Users;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.ops.DeploymentAgent;
import org.adamalang.ops.DeploymentMetrics;
import org.adamalang.ops.ProxyDeploymentFactory;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.capacity.CapacityAgent;
import org.adamalang.runtime.sys.capacity.CapacityMetrics;
import org.adamalang.runtime.sys.metering.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Backend {
  private static final Logger LOGGER = LoggerFactory.getLogger(Backend.class);
  public final CommonServiceInit init;
  public final Thread serverThread;

  public Backend(CommonServiceInit init, Thread serverThread) {
    this.init = init;
    this.serverThread = serverThread;
  }

  public static Backend run(Config config) throws Exception {
    // create the common/shared service issues
    CommonServiceInit init = new CommonServiceInit(config, Role.Adama);
    // create metrics
    CoreMetrics coreMetrics = new CoreMetrics(init.metricsFactory);
    DeploymentMetrics deploymentMetrics = new DeploymentMetrics(init.metricsFactory);
    // pull config
    int coreThreads = config.get_int("service-thread-count", 8);
    String billingRootPath = config.get_string("billing-path", "billing");
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
    ProxyDeploymentFactory factoryProxy = new ProxyDeploymentFactory(deploymentFactoryBase);

    BoundLocalFinderService finder = new BoundLocalFinderService(init.system, init.globalFinder, init.region, init.machine);

    CaravanBoot caravan = new CaravanBoot(init.alive, config.get_string("caravan-root", "caravan"), init.metricsFactory, init.region, init.machine, finder, init.s3, init.s3);

    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreService service = new CoreService(coreMetrics, factoryProxy, meteringPubSub.publisher(), caravan.service, TimeSource.REAL_TIME, coreThreads);
    DeploymentAgent deployAgent = new DeploymentAgent(init.system, init.database, deploymentMetrics, init.region, init.machine, deploymentFactoryBase, service, init.masterKey);

    ServiceHeatEstimator.HeatVector low = config.get_heat("heat-low", 1, 100, 1, 100);
    ServiceHeatEstimator.HeatVector hot = config.get_heat("heat-hot", 1000, 100000, 250, 2000);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, hot);
    meteringPubSub.subscribe(estimator);

    CapacityAgent capacityAgent = new CapacityAgent(new CapacityMetrics(init.metricsFactory), new GlobalCapacityOverseer(init.database), service, deploymentFactoryBase, estimator, init.system, init.alive, service.shield, init.region, init.machine);
    deploymentFactoryBase.attachDeliverer(service);
    // tell the proxy how to pull code on demand
    factoryProxy.setAgent(deployAgent);

    init.engine.createLocalApplicationHeartbeat("adama", init.servicePort, init.monitoringPort, (hb) -> {
      meteringPubSub.subscribe((bills) -> {
        estimator.apply(bills);
        hb.run();
        return true;
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

    // prime the host with spaces
    deployAgent.optimisticScanAll();

    // list all the documents on this machine, and spin them up
    init.globalFinder.list(new Callback<List<Key>>() {
      @Override
      public void success(List<Key> keys) {
        for (Key key : keys) {
          service.startupLoad(key);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.exit(-1);
      }
    });

    ServerNexus nexus = new ServerNexus(init.netBase, init.identity, service, new ServerMetrics(init.metricsFactory), deploymentFactoryBase, finder, deployAgent, meteringPubSub, billingBatchMaker, init.servicePort, 4);
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
    return new Backend(init, serverThread);
  }
}

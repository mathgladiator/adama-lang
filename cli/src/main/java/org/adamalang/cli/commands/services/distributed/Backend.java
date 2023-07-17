/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands.services.distributed;

import org.adamalang.cli.Config;
import org.adamalang.cli.commands.services.CaravanInit;
import org.adamalang.cli.commands.services.CommonServiceInit;
import org.adamalang.cli.commands.services.Role;
import org.adamalang.common.*;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.net.client.Client;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.ops.*;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

import java.io.File;
import java.util.List;

public class Backend {
  public final CommonServiceInit init;
  public final Thread serverThread;
  public final Client client;

  public Backend(CommonServiceInit init, Thread serverThread, Client client) {
    this.init = init;
    this.serverThread = serverThread;
    this.client = client;
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
    CaravanInit caravan = new CaravanInit(init, config);
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreService service = new CoreService(coreMetrics, factoryProxy, meteringPubSub.publisher(), caravan.service, TimeSource.REAL_TIME, coreThreads);
    DeploymentAgent deployAgent = new DeploymentAgent(init.picker, init.database, deploymentMetrics, init.region, init.machine, deploymentFactoryBase, service);

    ServiceHeatEstimator.HeatVector low = config.get_heat("heat-low", 1, 100, 1, 100);
    ServiceHeatEstimator.HeatVector hot = config.get_heat("heat-hot", 1000, 100000, 250, 2000);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, hot);
    meteringPubSub.subscribe(estimator);

    CapacityAgent capacityAgent = new CapacityAgent(new CapacityMetrics(init.metricsFactory), init.database, service, deploymentFactoryBase, estimator, init.system, init.alive, service.shield, init.region, init.machine);
    deploymentFactoryBase.attachDeliverer(service);
    // tell the proxy how to pull code on demand
    factoryProxy.setAgent(deployAgent);
    Client client = init.makeClient(capacityAgent);
    init.engine.subscribe("adama", (hosts) -> {
      capacityAgent.deliverAdamaHosts(hosts);
    });
    init.engine.createLocalApplicationHeartbeat("adama", init.servicePort, init.monitoringPort, (hb) -> {
      meteringPubSub.subscribe((bills) -> {
        capacityAgent.deliverMeteringRecords(bills);
        hb.run();
        return true;
      });
    });

    File billingRoot = new File(billingRootPath);
    billingRoot.mkdir();
    DiskMeteringBatchMaker billingBatchMaker = new DiskMeteringBatchMaker(TimeSource.REAL_TIME, SimpleExecutor.create("billing-batch-maker"), billingRoot, 10 * 60000L);
    meteringPubSub.subscribe((bills) -> {
      for (MeterReading meterReading : bills) {
        billingBatchMaker.write(meterReading);
      }
      return true;
    });

    // prime the host with spaces
    deployAgent.optimisticScanAll();

    // list all the documents on this machine, and spin them up
    init.finder.list(init.machine, new Callback<List<Key>>() {
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

    ServerNexus nexus = new ServerNexus(init.netBase, init.identity, service, new ServerMetrics(init.metricsFactory), deploymentFactoryBase, deployAgent, meteringPubSub, billingBatchMaker, init.servicePort, 4);
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
        caravan.shutdown();
      }
    })));
    System.err.println("backend running");
    return new Backend(init, serverThread, client);
  }
}

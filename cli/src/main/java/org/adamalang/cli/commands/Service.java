/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import org.adamalang.api.ApiMetrics;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.commands.services.FrontendHttpHandler;
import org.adamalang.extern.AssetSystemImpl;
import org.adamalang.extern.aws.SQS;
import org.adamalang.extern.stripe.StripeConfig;
import org.adamalang.extern.stripe.StripeMetrics;
import org.adamalang.frontend.FrontendMetrics;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.ops.CapacityAgent;
import org.adamalang.cli.commands.services.CaravanInit;
import org.adamalang.cli.commands.services.CommonServiceInit;
import org.adamalang.cli.commands.services.Role;
import org.adamalang.common.*;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.SES;
import org.adamalang.extern.prometheus.PrometheusDashboard;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.ops.CapacityMetrics;
import org.adamalang.ops.DeploymentAgent;
import org.adamalang.ops.DeploymentMetrics;
import org.adamalang.overlord.Overlord;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      serviceHelp(new String[0]);
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "auto":
        serviceAuto(config);
        return;
      case "backend":
        serviceBackend(config);
        return;
      case "overlord":
        serviceOverlord(config);
        return;
      case "frontend":
        serviceFrontend(config);
        return;
      case "dashboards":
        dashboards();
        return;
      case "help":
        serviceHelp(next);
        return;
    }
  }

  public static void serviceHelp(String[] next) {
    System.out.println(Util.prefix("Spin up a service.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama service", Util.ANSI.Green) + " " + Util.prefix("[SERVICESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SERVICESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("auto", Util.ANSI.Green) + "              The config will decide the role");
    System.out.println("    " + Util.prefix("backend", Util.ANSI.Green) + "           Spin up a gRPC back-end node");
    System.out.println("    " + Util.prefix("frontend", Util.ANSI.Green) + "          Spin up a WebSocket front-end node");
    System.out.println("    " + Util.prefix("overlord", Util.ANSI.Green) + "          Spin up the cluster overlord");
    System.out.println("    " + Util.prefix("dashboards", Util.ANSI.Green) + "        Produce dashboards for prometheus.");
  }

  public static void serviceAuto(Config config) throws Exception {
    String role = config.get_string("role", "none");
    switch (role) {
      case "backend":
        serviceBackend(config);
        return;
      case "overlord":
        serviceOverlord(config);
        return;
      case "frontend":
        serviceFrontend(config);
        return;
      default:
        System.err.println("invalid role:" + role);
    }
  }

  public static void serviceBackend(Config config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Adama);
    int coreThreads = config.get_int("service_thread_count", 8);
    String billingRootPath = config.get_string("billing_path", "billing");

    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
    FirstPartyServices.install(init.database);

    CaravanInit caravan = new CaravanInit(init, config);
    final DataService data = caravan.service;

    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreMetrics coreMetrics = new CoreMetrics(init.metricsFactory);
    CoreService service = new CoreService(coreMetrics, deploymentFactoryBase, meteringPubSub.publisher(), data, TimeSource.REAL_TIME, coreThreads);
    deploymentFactoryBase.attachDeliverer(service);

    CapacityAgent capacityAgent = new CapacityAgent(new CapacityMetrics(init.metricsFactory), init.database, service, init.system, init.alive, service.shield);
    init.makeClient(capacityAgent);

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

    DeploymentMetrics deploymentMetrics = new DeploymentMetrics(init.metricsFactory);
    DeploymentAgent deployAgent = new DeploymentAgent(init.database, deploymentMetrics, init.machine, deploymentFactoryBase, service);

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
    deployAgent.accept("*");

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

        handle.kill();
        caravan.shutdown();
      }
    })));
    System.err.println("backend running");
  }

  public static void serviceOverlord(Config config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Overlord);
    String scanPath = config.get_string("scan_path", "web_root");
    File targetsPath = new File(config.get_string("targets_filename", "targets.json"));
    init.engine.createLocalApplicationHeartbeat("overlord", init.webConfig.port, init.monitoringPort, (hb) -> {
      init.system.schedule(new NamedRunnable("overlord-hb") {
        @Override
        public void execute() throws Exception {
          hb.run();
          if (init.alive.get()) {
            init.system.schedule(this, 1000);
          }
        }
      }, 100);
    });
    ConcurrentCachedHttpHandler overlordHandler = new ConcurrentCachedHttpHandler();
    HeatTable heatTable = new HeatTable(overlordHandler);
    Client client = init.makeClient(heatTable::onSample);
    HttpHandler handler = Overlord.execute(overlordHandler, heatTable, client, init.engine, init.metricsFactory, targetsPath, init.database, scanPath, init.alive);
    ServiceBase serviceBase = ServiceBase.JUST_HTTP(handler);
    final var runnable = new ServiceRunnable(init.webConfig, new WebMetrics(init.metricsFactory), serviceBase, init.makeCertificateFinder(), () -> {});
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        System.err.println("shutting down overlord");
        runnable.shutdown();
      }
    }));
    System.err.println("running overlord web");
    runnable.run();
    System.err.println("overlord finished");
  }

  public static void serviceFrontend(Config config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Web);
    System.err.println("starting frontend");

    int gossipPort = config.get_int("gossip_frontend_port", 8004);
    System.err.println("gossiping on:" + gossipPort);
    System.err.println("standing up http on:" + init.servicePort);
    Client client = init.makeClient(null);

    FrontendHttpHandler http = new FrontendHttpHandler(init, client);

    Email email = new SES(init.webBase, init.awsConfig, init.awsMetrics);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    MultiRegionClient adama = new MultiRegionClient(init.database, client, init.region, init.finder);
    AssetSystemImpl assets = new AssetSystemImpl(init.database, adama, init.s3);
    StripeConfig stripe = new StripeConfig(new ConfigObject(config.get_or_create_child("stripe")));
    ArrayList<String> superKeys = config.get_str_list("super_public_keys");

    ExternNexus nexus = new ExternNexus(frontendConfig, email, init.database, adama, assets, init.metricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, init.masterKey, init.webBase, init.region, init.hostKey, init.publicKeyId, stripe, superKeys.toArray(new String[superKeys.size()]), init.sqs);
    System.err.println("nexus constructed");
    ServiceBase serviceBase = BootstrapFrontend.make(nexus, http);
    // TODO: have some sense of health checking in the web package
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latchForHeartbeat = new CountDownLatch(1);

    init.engine.createLocalApplicationHeartbeat("web", init.webConfig.port, init.monitoringPort, (hb) -> {
      heartbeat.set(hb);
      latchForHeartbeat.countDown();
    });

    if (!latchForHeartbeat.await(10000, TimeUnit.MILLISECONDS)) {
      System.err.println("failed to register");
      return;
    }
    WebMetrics webMetrics = new WebMetrics(init.metricsFactory);
    final var redirect = new RedirectAndWellknownServiceRunnable(init.webConfig, webMetrics, init.s3, () -> {});
    Thread redirectThread = new Thread(redirect);
    redirectThread.start();
    final var runnable = new ServiceRunnable(init.webConfig, webMetrics, serviceBase, init.makeCertificateFinder(), heartbeat.get());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("shutting down frontend");
      try {
        runnable.shutdown();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        redirect.shutdown();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        nexus.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }));
    System.err.println("running frontend");
    runnable.run();
    System.err.println("frontend finished");
  }

  public static void dashboards() throws Exception {
    PrometheusDashboard metricsFactory = new PrometheusDashboard();
    metricsFactory.page("web", "Web");
    new WebMetrics(metricsFactory);
    metricsFactory.page("api", "Public API");
    new FrontendMetrics(metricsFactory);
    new ApiMetrics(metricsFactory);
    metricsFactory.page("client", "Web to Adama");
    new ClientMetrics(metricsFactory);
    metricsFactory.page("server", "Adama Service");
    new ServerMetrics(metricsFactory);
    metricsFactory.page("adama", "Adama Core");
    new CoreMetrics(metricsFactory);
    metricsFactory.page("deploy", "Deploy");
    new DeploymentMetrics(metricsFactory);
    metricsFactory.page("capacity", "Capacity");
    new CapacityMetrics(metricsFactory);
    metricsFactory.page("database", "Database");
    new DataBaseMetrics(metricsFactory);
    metricsFactory.page("caravan", "Caravan");
    new CaravanMetrics(metricsFactory);
    metricsFactory.page("disk", "Disk");
    new DiskMetrics(metricsFactory);
    metricsFactory.page("overlord", "Overlord");
    new OverlordMetrics(metricsFactory);
    metricsFactory.page("net", "Network");
    new NetMetrics(metricsFactory);
    metricsFactory.page("aws", "AWS");
    new AWSMetrics(metricsFactory);
    metricsFactory.finish(new File("./prometheus/consoles"));
  }
}

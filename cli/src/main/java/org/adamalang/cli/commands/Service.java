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
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.events.FinderServiceToKeyToIdService;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
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
import org.adamalang.mysql.model.Deployments;
import org.adamalang.mysql.data.Deployment;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.overlord.Overlord;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    CommonServiceInit init = new CommonServiceInit(config, Role.Adama, config.get_int("adama_port", 8001));
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

    init.engine.createLocalApplicationHeartbeat("adama", init.servicePort, init.monitoringPort, (hb) -> {
      meteringPubSub.subscribe((bills) -> {
        hb.run();
        return true;
      });
    });

    DeploymentMonitor deploymentMonitor = new DeploymentMonitor() {
      @Override
      public void bumpDocument(boolean changed) {

      }

      @Override
      public void witnessException(ErrorCodeException ex) {

      }
    };

    // TODO: clean this up, this ... kind of sucks
    Consumer<String> scanForDeployments = (space) -> {
      try {
        if ("*".equals(space)) {
          ArrayList<Deployment> deployments = Deployments.listSpacesOnTarget(init.database, init.machine);
          for (Deployment deployment : deployments) {
            try {
              deploymentFactoryBase.deploy(deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {
              }));
              service.deploy(deploymentMonitor);
            } catch (Exception ex) {
              if (ex instanceof ErrorCodeException) {
                LOGGER.error("failed-scan-" + space + ":" + ((ErrorCodeException) ex).code);
              } else {
                LOGGER.error("failed-scan-" + space, ex);
              }
            }
          }
        } else {
          Deployment deployment = Deployments.get(init.database, init.machine, space);
          deploymentFactoryBase.deploy(deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {
          }));
          service.deploy(deploymentMonitor);
        }
      } catch (Exception ex) {
        if (ex instanceof ErrorCodeException) {
          LOGGER.error("failed-scan-" + space + ":" + ((ErrorCodeException) ex).code);
        } else {
          LOGGER.error("failed-scan-" + space, ex);
        }
      }
    };
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
    scanForDeployments.accept("*");
    ServerNexus nexus = new ServerNexus(init.netBase, init.identity, service, new ServerMetrics(init.metricsFactory), deploymentFactoryBase, scanForDeployments, meteringPubSub, billingBatchMaker, init.servicePort, 4);
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
    ConfigObject co = new ConfigObject(config.get_or_create_child("overlord_web"));
    co.intOf("http_port", 8081);
    WebConfig webConfig = new WebConfig(co);
    CommonServiceInit init = new CommonServiceInit(config, Role.Overlord, webConfig.port);
    String scanPath = config.get_string("scan_path", "web_root");
    File targetsPath = new File(config.get_string("targets_filename", "targets.json"));
    init.engine.createLocalApplicationHeartbeat("overlord", webConfig.port, init.monitoringPort, (hb) -> {
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
    Client client = init.makeClient();
    HttpHandler handler = Overlord.execute(client, init.engine, init.metricsFactory, targetsPath, init.database, scanPath);
    ServiceBase serviceBase = ServiceBase.JUST_HTTP(handler);
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(init.metricsFactory), serviceBase, () -> {});
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
    WebConfig webConfig = new WebConfig(new ConfigObject(config.get_or_create_child("web")));
    CommonServiceInit init = new CommonServiceInit(config, Role.Web, webConfig.port);
    System.err.println("starting frontend");
    String masterKey = config.get_string("master-key", null);
    int gossipPort = config.get_int("gossip_frontend_port", 8004);
    System.err.println("gossiping on:" + gossipPort);
    System.err.println("standing up http on:" + webConfig.port);
    Client client = init.makeClient();
    WebClientBase webBase = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));

    // TODO: bring this out, and this whole file is getting CRAZY
    HttpHandler http = new HttpHandler() {
      @Override
      public void handleOptions(String uri, Callback<Boolean> callback) {
        SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
        if (skr != null) {
          WebGet get = new WebGet(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, new TreeMap<>(), new NtDynamic("{}"));
          client.webOptions(skr.space, skr.key, get, new Callback<>() {
            @Override
            public void success(WebResponse value) {
              callback.success(value.cors);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
          callback.success(true);
        } else {
          callback.success(false);
        }
      }

      @Override
      public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
        if (skr != null) {
          // TODO: need a way to get an NtPrincipal token
          WebGet get = new WebGet(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, headers, new NtDynamic(parametersJson));
          client.webGet(skr.space, skr.key, get, new Callback<>() {
            @Override
            public void success(WebResponse value) {
              if (value != null) {
                if (value.asset != null) {
                  callback.success(new HttpResult(skr.space, skr.key, value.asset, value.cors));
                } else {
                  callback.success(new HttpResult(value.contentType, value.body.getBytes(StandardCharsets.UTF_8), value.cors));
                }
              } else {
                callback.success(null);
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        } else {
          callback.success(null);
        }
      }

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
        if (skr != null) {
          // TODO: need a way to get an NtPrincipal token
          WebPut put = new WebPut(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), new WebPutRaw(skr.uri, headers, new NtDynamic(parametersJson), body));
          client.webPut(skr.space, skr.key, put, new Callback<>() {
            @Override
            public void success(WebResponse value) {
              if (value != null) {
                callback.success(new HttpResult(value.contentType, value.body.getBytes(StandardCharsets.UTF_8), value.cors));
              } else {
                callback.success(null);
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        } else {
          callback.success(null);
        }
      }
    };

    Email email = new SES(init.awsConfig, init.awsMetrics);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    ExternNexus nexus = new ExternNexus(frontendConfig, email, init.s3, init.s3, init.database, init.finder, client, init.metricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, masterKey, webBase, init.region, init.hostKey, init.publicKeyId);
    System.err.println("nexus constructed");
    ServiceBase serviceBase = BootstrapFrontend.make(nexus, http);
    // TODO: have some sense of health checking in the web package
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latchForHeartbeat = new CountDownLatch(1);

    init.engine.createLocalApplicationHeartbeat("web", webConfig.port, init.monitoringPort, (hb) -> {
      heartbeat.set(hb);
      latchForHeartbeat.countDown();
    });

    if (!latchForHeartbeat.await(10000, TimeUnit.MILLISECONDS)) {
      System.err.println("failed to register");
      return;
    }

    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(init.metricsFactory), serviceBase, heartbeat.get());
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        System.err.println("shutting down frontend");
        runnable.shutdown();
        try {
          nexus.close();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
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
    new ApiMetrics(metricsFactory);
    metricsFactory.page("client", "Web to Adama");
    new ClientMetrics(metricsFactory);
    metricsFactory.page("server", "Adama Service");
    new ServerMetrics(metricsFactory);
    metricsFactory.page("adama", "Adama Core");
    new CoreMetrics(metricsFactory);
    metricsFactory.page("database", "Database");
    new DataBaseMetrics(metricsFactory);
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

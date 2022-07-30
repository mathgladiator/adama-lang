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
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.S3;
import org.adamalang.extern.aws.SES;
import org.adamalang.extern.prometheus.PrometheusDashboard;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.EngineRole;
import org.adamalang.gossip.GossipMetricsImpl;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.mysql.data.Deployment;
import org.adamalang.mysql.model.Finder;
import org.adamalang.mysql.model.Health;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.overlord.Overlord;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.grpc.OverlordClient;
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
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebPutRaw;
import org.adamalang.runtime.sys.web.WebResponse;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

  private static void startArchivingLogs(S3 s3, String prefix, AtomicBoolean alive) {
    SimpleExecutor executor = SimpleExecutor.create("s3-archive-logs");
    executor.schedule(new NamedRunnable("archive-s3") {
      @Override
      public void execute() throws Exception {
        try {
          s3.uploadLogs(new File("logs"), prefix);
        } catch (Exception ex) {
          LOGGER.error("error-uploading-logs", ex);
        } finally {
          if (alive.get()) {
            executor.schedule(this, 60000);
          } else {
            executor.shutdown();
          }
        }
      }
    }, 5000);
  }

  public static void serviceBackend(Config config) throws Exception {
    MachineHeat.install();
    int port = config.get_int("adama_port", 8001);
    int gossipPort = config.get_int("gossip_backend_port", 8002);
    int monitoringPort = config.get_int("monitoring_backend_port", 8003);
    PrometheusMetricsFactory prometheusMetricsFactory = new PrometheusMetricsFactory(monitoringPort);
    int coreThreads = config.get_int("service_thread_count", 8);
    String identityFileName = config.get_string("identity_filename", "me.identity");
    String billingRootPath = config.get_string("billing_path", "billing");
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);
    String machine = identity.ip + ":" + port;
    Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(config.get_str_list("bootstrap")), gossipPort, monitoringPort, new GossipMetricsImpl(prometheusMetricsFactory), EngineRole.Node);
    engine.start();
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(prometheusMetricsFactory));
    FirstPartyServices.install(dataBase);
    ScheduledExecutorService databasePings = Executors.newSingleThreadScheduledExecutor();
    databasePings.scheduleAtFixedRate(() -> {
      try {
        Health.pingDataBase(dataBase);
      } catch (Exception ex) {
        LOGGER.error("health-check-failure-database", ex);
      }
    }, 30000, 30000, TimeUnit.MILLISECONDS);

    final DataService data;
    final Finder finder;
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    AWSMetrics awsMetrics = new AWSMetrics(prometheusMetricsFactory);
    S3 s3 = new S3(awsConfig, awsMetrics);
    AtomicBoolean alive = new AtomicBoolean(true);
    startArchivingLogs(s3, "adama/" + identity.ip + "_" + gossipPort, alive);
    {
      String caravanRoot = config.get_string("caravan_root", "caravan");
      String region = config.get_string("region", null);


      SimpleExecutor caravanExecutor = SimpleExecutor.create("caravan");
      SimpleExecutor managedExecutor = SimpleExecutor.create("managed-base");
      File caravanPath = new File(caravanRoot);
      caravanPath.mkdir();
      File walRoot = new File(caravanPath, "wal");
      File dataRoot = new File(caravanPath, "data");
      walRoot.mkdir();
      dataRoot.mkdir();
      File storePath = new File(dataRoot, "store");
      DurableListStore store = new DurableListStore(new DiskMetrics(prometheusMetricsFactory), storePath, walRoot, 4L * 1024 * 1024 * 1024, 16 * 1024 * 1024, 64 * 1024 * 1024);
      finder = new Finder(dataBase, region);
      CaravanDataService caravanDataService = new CaravanDataService(s3, new FinderServiceToKeyToIdService(finder), store, caravanExecutor);
      Base managedBase = new Base(finder, caravanDataService, region, machine, managedExecutor, 2 * 60 * 1000);
      data = new ManagedDataService(managedBase);
      Thread flusher = new Thread(new Runnable() {
        @Override
        public void run() {
          while (alive.get()) {
            try {
              Thread.sleep(0, 800000);
              caravanDataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
              return;
            }
          }
        }
      });
      flusher.start();
      Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
        @Override
        public void run() throws Exception {
          alive.set(false);
        }
      })));
    }
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreMetrics coreMetrics = new CoreMetrics(prometheusMetricsFactory);
    CoreService service = new CoreService(coreMetrics, deploymentFactoryBase, meteringPubSub.publisher(), data, TimeSource.REAL_TIME, coreThreads);
    deploymentFactoryBase.attachDeliverer(service);

    // list all the documents on this machine, and spin them up
    finder.list(machine, new Callback<List<Key>>() {
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

    engine.newApp("adama", port, (hb) -> {
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
          ArrayList<Deployment> deployments = Deployments.listSpacesOnTarget(dataBase, identity.ip + ":" + port);
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
          Deployment deployment = Deployments.get(dataBase, identity.ip + ":" + port, space);
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
    NetBase netBase = new NetBase(identity, 1, 2);
    ServerNexus nexus = new ServerNexus(netBase, identity, service, new ServerMetrics(prometheusMetricsFactory), deploymentFactoryBase, scanForDeployments, meteringPubSub, billingBatchMaker, port, 4);
    ServerHandle handle = netBase.serve(port, (upstream) -> new Handler(nexus, upstream));
    Thread serverThread = new Thread(() -> handle.waitForEnd());
    serverThread.start();
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        // billingPubSub.terminate();
        // This will send to all connections an empty list which will remove from the routing table. At this point, we should wait all connections migrate away

        // TODO: for each connection, remove from routing table, stop
        databasePings.shutdown();
        handle.kill();
        netBase.shutdown();
      }
    })));
    System.err.println("backend running");
  }

  public static void serviceOverlord(Config config) throws Exception {
    MachineHeat.install();
    int gossipPort = config.get_int("gossip_overlord_port", 8010);
    int monitoringPort = config.get_int("monitoring_overlord_port", 8011);
    int overlordPort  = config.get_int("overlord_port", 8015);
    String scanPath = config.get_string("scan_path", "web_root");
    PrometheusMetricsFactory prometheusMetricsFactory = new PrometheusMetricsFactory(monitoringPort);
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(prometheusMetricsFactory));

    String identityFileName = config.get_string("identity_filename", "me.identity");
    File targetsPath = new File(config.get_string("targets_filename", "targets.json"));
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);

    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    AWSMetrics awsMetrics = new AWSMetrics(prometheusMetricsFactory);
    S3 s3 = new S3(awsConfig, awsMetrics);
    AtomicBoolean alive = new AtomicBoolean(true);
    startArchivingLogs(s3, "overlord/" +identity.ip + "_" + gossipPort, alive);

    Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(config.get_str_list("bootstrap")), gossipPort, monitoringPort, new GossipMetricsImpl(prometheusMetricsFactory), EngineRole.SuperNode);
    engine.start();

    System.err.println("running overlord web");

    HttpHandler handler = Overlord.execute(identity, engine, overlordPort, prometheusMetricsFactory, targetsPath, dataBase, scanPath);

    ConfigObject co = new ConfigObject(config.get_or_create_child("overlord_web"));
    co.intOf("http_port", 8081);
    WebConfig webConfig = new WebConfig(co);
    ServiceBase serviceBase = ServiceBase.JUST_HTTP(handler);
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(prometheusMetricsFactory), serviceBase, () -> {});
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        alive.set(false);
        System.err.println("shutting down overlord");
        runnable.shutdown();
      }
    }));

    System.err.println("running overlord web");
    runnable.run();
    System.err.println("overlord finished");
  }

  public static void serviceFrontend(Config config) throws Exception {
    MachineHeat.install();
    System.err.println("starting frontend");
    String identityFileName = config.get_string("identity_filename", "me.identity");
    String region = config.get_string("region", null);
    String masterKey = config.get_string("master-key", null);
    int gossipPort = config.get_int("gossip_frontend_port", 8004);
    int monitoringPort = config.get_int("monitoring_frontend_port", 8005);
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);
    PrometheusMetricsFactory prometheusMetricsFactory = new PrometheusMetricsFactory(monitoringPort);
    DataBase database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(prometheusMetricsFactory));
    ScheduledExecutorService databasePings = Executors.newSingleThreadScheduledExecutor();
    databasePings.scheduleAtFixedRate(() -> {
      try {
        Health.pingDataBase(database);
      } catch (Exception ex) {
        LOGGER.error("health-check-failure-database", ex);
      }
    }, 30000, 30000, TimeUnit.MILLISECONDS);

    System.err.println("using database: " + database.databaseName);
    System.err.println("identity: " + identity.ip);
    Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(config.get_str_list("bootstrap")), gossipPort, monitoringPort, new GossipMetricsImpl(prometheusMetricsFactory), EngineRole.Node);
    engine.start();
    System.err.println("gossiping on:" + gossipPort);
    WebConfig webConfig = new WebConfig(new ConfigObject(config.get_or_create_child("web")));
    System.err.println("standing up http on:" + webConfig.port);
    NetBase netBase = new NetBase(identity, 1, 2);
    ClientConfig clientConfig = new ClientConfig();
    ClientMetrics metrics = new ClientMetrics(prometheusMetricsFactory);
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    AWSMetrics awsMetrics = new AWSMetrics(prometheusMetricsFactory);
    S3 s3 = new S3(awsConfig, awsMetrics);
    AtomicBoolean alive = new AtomicBoolean(true);
    startArchivingLogs(s3, "web/" + identity.ip + "_" + gossipPort, alive);

    Finder finder = new Finder(database, region);
    ClientRouter router = ClientRouter.FINDER(metrics, finder, region);
    Client client = new Client(netBase, clientConfig, metrics, router, null);
    Consumer<Collection<String>> targetPublisher = client.getTargetPublisher();

    engine.subscribe("adama", (targets) -> {
      StringBuilder notice = new StringBuilder();
      boolean append = false;
      for (String target : targets) {
        if (append) {
          notice.append(", ");
        }
        append = true;
        notice.append(target);
      }
      System.err.println("adama targets:" + notice);
      targetPublisher.accept(targets);
    });

    OverlordClient overlordClient = new OverlordClient(identity, webConfig.port);
    engine.subscribe("overlord", new Consumer<Collection<String>>() {
      @Override
      public void accept(Collection<String> targets) {
        for (String first : targets) {
          overlordClient.setTarget(first);
          return;
        }
        overlordClient.setTarget(null);
      }
    });
    WebClientBase webBase = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));

    // TODO: bring this out, and this whole file is getting CRAZY
    HttpHandler http = new HttpHandler() {
      @Override
      public void handleOptions(String uri, Callback<Boolean> callback) {
        SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
        if (skr != null) {
          WebGet get = new WebGet(NtPrincipal.NO_ONE, skr.uri, new TreeMap<>(), new NtDynamic("{}"));
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
          WebGet get = new WebGet(NtPrincipal.NO_ONE, skr.uri, headers, new NtDynamic(parametersJson));
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
          WebPut put = new WebPut(NtPrincipal.NO_ONE, new WebPutRaw(skr.uri, headers, new NtDynamic(parametersJson), body));
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

    Email email = new SES(awsConfig, awsMetrics);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    ExternNexus nexus = new ExternNexus(frontendConfig, email, s3, s3, database, finder, client, prometheusMetricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, masterKey, webBase);
    System.err.println("nexus constructed");
    ServiceBase serviceBase = BootstrapFrontend.make(nexus, http);
    // TODO: have some sense of health checking in the web package
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latchForHeartbeat = new CountDownLatch(1);
    engine.newApp("web", webConfig.port, (hb) -> {
      heartbeat.set(hb);
      latchForHeartbeat.countDown();
    });

    if (!latchForHeartbeat.await(10000, TimeUnit.MILLISECONDS)) {
      System.err.println("failed to register");
      return;
    }

    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(prometheusMetricsFactory), serviceBase, heartbeat.get());
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        alive.set(false);
        System.err.println("shutting down frontend");
        runnable.shutdown();
        databasePings.shutdown();
        webBase.shutdown();
      }
    }));
    System.err.println("running frontend");
    runnable.run();
    System.err.println("frontend finished");
  }

  public static void dashboards() throws Exception {
    PrometheusDashboard metricsFactory = new PrometheusDashboard();
    metricsFactory.page("aws", "AWS");
    new AWSMetrics(metricsFactory);
    metricsFactory.page("gossip", "Gossip");
    new GossipMetricsImpl(metricsFactory);
    metricsFactory.page("client", "Client to Adama");
    new ClientMetrics(metricsFactory);
    metricsFactory.page("server", "Adama Server");
    new ServerMetrics(metricsFactory);
    metricsFactory.page("adama", "Core Service");
    new CoreMetrics(metricsFactory);
    metricsFactory.page("web", "Web Proxy");
    new WebMetrics(metricsFactory);
    metricsFactory.page("overlord", "The Overlord");
    new OverlordMetrics(metricsFactory);
    metricsFactory.page("api", "The Public API");
    new ApiMetrics(metricsFactory);
    metricsFactory.page("database", "Database");
    new DataBaseMetrics(metricsFactory);
    metricsFactory.page("disk", "Disk");
    new DiskMetrics(metricsFactory);
    metricsFactory.finish(new File("./prometheus/consoles"));
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.*;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.extern.prometheus.PrometheusBase;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.Metrics;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.server.Server;
import org.adamalang.grpc.server.ServerNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.backend.BlockingDataService;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.overlord.Overlord;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.billing.Bill;
import org.adamalang.runtime.sys.billing.BillingPubSub;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.billing.DiskBillingBatchMaker;
import org.adamalang.runtime.threads.ThreadedDataService;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class Service {
  private static final Metrics GOSSIP_METRICS =
      new Metrics() {
        @Override
        public void wake() {

        }

        @Override
        public void bump_sad_return() {}

        @Override
        public void bump_client_slow_gossip() {}

        @Override
        public void bump_optimistic_return() {}

        @Override
        public void bump_turn_tables() {}

        @Override
        public void bump_start() {}

        @Override
        public void bump_found_reverse() {}

        @Override
        public void bump_quick_gossip() {}

        @Override
        public void bump_server_slow_gossip() {}

        @Override
        public void log_error(Throwable cause) {}
      };

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
      case "help":
        serviceHelp(next);
        return;
    }
  }

  public static void serviceAuto(Config config) throws Exception{
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

  public static void serviceHelp(String[] next) {
      System.out.println(Util.prefix("Spin up a service.", Util.ANSI.Green));
      System.out.println("");
      System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
      System.out.println("    " + Util.prefix("adama service", Util.ANSI.Green) + " " + Util.prefix("[SERVICESUBCOMMAND]", Util.ANSI.Magenta));
      System.out.println("");
      System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
      System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
      System.out.println("");
      System.out.println(Util.prefix("SERVICESUBCOMMAND:", Util.ANSI.Yellow));
      System.out.println("    " + Util.prefix("auto", Util.ANSI.Green) + "              The config will decide the role");
      System.out.println("    " + Util.prefix("backend", Util.ANSI.Green) + "           Spin up a gRPC back-end node");
      System.out.println("    " + Util.prefix("frontend", Util.ANSI.Green) + "          Spin up a WebSocket front-end node");
      System.out.println("    " + Util.prefix("overlord", Util.ANSI.Green) + "          Spin up the cluster overlord");
  }

  public static void serviceOverlord(Config config) throws Exception {
    int gossipPort = config.get_int("gossip_overlord_port", 8250);
    String identityFileName = config.get_string("identity_filename", "me.identity");
    File targetsPath = new File(config.get_string("targetsPath", "targets.json"));
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);
    Engine engine =
        new Engine(
            identity,
            TimeSource.REAL_TIME,
            new HashSet<>(config.get_str_list("bootstrap")),
            gossipPort,
            GOSSIP_METRICS);
    engine.start();
    Client client = new Client(identity);
    Overlord.execute(engine, client, targetsPath);
  }

  public static void serviceBackend(Config config) throws Exception {
    int port = config.get_int("adama_port", 3281);
    int gossipPort = config.get_int("gossip_backend_port", 8232);
    int monitoringPort = config.get_int("monitoring_port", 9111);
    int dataThreads = config.get_int("data_thread_count", 32);
    int coreThreads = config.get_int("service_thread_count", 4);
    String identityFileName = config.get_string("identity_filename", "me.identity");
    String billingRootPath = config.get_string("billing_path", "billing");
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);
    Engine engine =
        new Engine(
            identity,
            TimeSource.REAL_TIME,
            new HashSet<>(config.get_str_list("bootstrap")),
            gossipPort,
            GOSSIP_METRICS);
    engine.start();
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
    DataBase dataBaseBackend = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "backend"));
    DataBase dataBaseDeployments = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "deployed"));
    ThreadedDataService dataService =
        new ThreadedDataService(dataThreads, () -> new BlockingDataService(dataBaseBackend));
    BillingPubSub billingPubSub = new BillingPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreService service =
        new CoreService(
            deploymentFactoryBase,
            billingPubSub.publisher(),
            dataService,
            TimeSource.REAL_TIME,
            coreThreads);

    billingPubSub.subscribe(
        (bills) -> {
          // TODO: log to disk
          // TODO: once the disk has an hour of usage, summarize and send to S3
          // TODO: submit to billing service
          return true;
        });
    PrometheusBase prometheusBase = new PrometheusBase(monitoringPort);

    engine.newApp(
        "adama",
        port,
        monitoringPort,
        (hb) -> {
          billingPubSub.subscribe(
              (bills) -> {
                hb.run();
                return true;
              });
        });

    Consumer<String> scanForDeployments =
        (space) -> {
          try {
            if ("*".equals(space)) {
              ArrayList<Deployments.Deployment> deployments =
                  Deployments.list(dataBaseDeployments, identity.ip + ":" + port);
              for (Deployments.Deployment deployment : deployments) {
                try {
                  deploymentFactoryBase.deploy(
                      deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {}));
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            } else {
              Deployments.Deployment deployment =
                  Deployments.get(dataBaseDeployments, identity.ip + ":" + port, space);
              deploymentFactoryBase.deploy(
                  deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {}));
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        };
    // TODO: make not null
    File billingRoot = new File(billingRootPath);
    billingRoot.mkdir();
    DiskBillingBatchMaker billingBatchMaker = new DiskBillingBatchMaker(TimeSource.REAL_TIME, SimpleExecutor.create("billing-batch-maker"), billingRoot);
    billingPubSub.subscribe((bills) -> {
      for (Bill bill : bills) {
        billingBatchMaker.write(bill);
      }
      return true;
    });

    // prime the host with spaces
    scanForDeployments.accept("*");
    ServerNexus nexus =
        new ServerNexus(
            identity, service, deploymentFactoryBase, scanForDeployments, billingPubSub, billingBatchMaker, port, 4);

    Server server = new Server(nexus);
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        // billingPubSub.terminate();
        // This will send to all connections an empty list which will remove from the routing table. At this point, we should wait all connections migrate away

        // TODO: for each connection, remove from routing table, stop
        server.close();
      }
    })));
  }

  public static void serviceFrontend(Config config) throws Exception {
    DataBase dataBaseFront = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "frontend"));
    DataBase dataBaseDeployments = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "deployments"));
    DataBase dataBaseBackend = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "backend"));

    String identityFileName = config.get_string("identity_filename", "me.identity");
    int gossipPort = config.get_int("gossip_frontend_port", 8233);
    MachineIdentity identity = MachineIdentity.fromFile(identityFileName);
    Engine engine =
        new Engine(
            identity,
            TimeSource.REAL_TIME,
            new HashSet<>(config.get_str_list("bootstrap")),
            gossipPort,
            GOSSIP_METRICS);
    engine.start();
    WebConfig webConfig = new WebConfig(new ConfigObject(config.get_or_create_child("web")));

    // TODO: have some sense of health checking in the web package
    /*
    engine.newApp("web", webConfig.port, (hb) -> {

    });
    */

    Client client = new Client(identity);
    engine.subscribe("adama", client.getTargetPublisher());
    ExternNexus nexus =
        new ExternNexus(
            new Email() {
              @Override
              public void sendCode(String email, String code) {
                System.err.println("Email:" + email + " --> " + code);
              }
            },
            dataBaseFront,
            dataBaseDeployments,
            dataBaseBackend,
            client);
    ServiceBase serviceBase = BootstrapFrontend.make(nexus);
    final var runnable = new ServiceRunnable(webConfig, serviceBase);
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    thread.join();
  }
}

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
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.InstanceSetChain;
import org.adamalang.gossip.Metrics;
import org.adamalang.grpc.server.Server;
import org.adamalang.grpc.server.ServerNexus;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.backend.BlockingDataService;
import org.adamalang.mysql.backend.Deployments;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.BillingPubSub;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.threads.ThreadedDataService;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;

import java.util.ArrayList;
import java.util.HashSet;

public class Service {
    public static void execute(Config config, String[] args) throws Exception {
        if (args.length == 0) {
            serviceHelp(new String[0]);
            return;
        }
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "backend":
                serviceBackend(config);
                return;
            case "frontend":
                serviceFrontend(config);
                return;
            case "yolo":
                return;
            case "help":
                serviceHelp(next);
                return;
        }
    }

    public static void serviceYolo(Config config) {
    }



    public static void serviceBackend(Config config) throws Exception {
        // TODO: pull backend port from config
        int port = 3281;

        // TODO: embed the identity into the config?
        MachineIdentity identity = MachineIdentity.fromFile("me.identity");

        Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(), 20000, GOSSIP_METRICS);
        engine.start();

        DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
        DataBase dataBase = new DataBase(new DataBaseConfig(config.read().toString(), "backend"));

        // TODO: pull threads from config
        ThreadedDataService dataService = new ThreadedDataService(16, () -> new BlockingDataService(dataBase) );

        // TODO: transform the billing into a heat vector that is sent to all clients
        // TODO: record the billing into a database (pick a random client to record bill)

        BillingPubSub billingPubSub = new BillingPubSub(deploymentFactoryBase);

        // TODO: pull service threads from config
        CoreService service = new CoreService(deploymentFactoryBase, //
            billingPubSub.publisher(),
            dataService, //
            TimeSource.REAL_TIME, 2);

        billingPubSub.subscribe((bills) -> {
            // TODO: submit to billing service
            return true;
        });

        engine.newApp("adama", port, (hb) -> {
            billingPubSub.subscribe((bills) -> {
                hb.run();
                return true;
            });
        });

        Runnable scanForDeployments = () -> {
            try {
                ArrayList<Deployments.Deployment> deployments = Deployments.list(dataBase, identity.ip + ":" + port);
                for (Deployments.Deployment deployment : deployments) {
                    try {
                        deploymentFactoryBase.deploy(deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {}));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        // prime the host with spaces
        scanForDeployments.run();

        ServerNexus nexus = new ServerNexus(identity, service, deploymentFactoryBase, scanForDeployments, billingPubSub, port, 4);

        // TODO: hold onto the Server reference and kill on a signal, need signal listener
        new Server(nexus).start();
    }

    public static void serviceFrontend(Config config) throws Exception {
        DataBase dataBase = new DataBase(new DataBaseConfig(config.read().toString(), "frontend"));

        // TODO: embed the identity into the config?
        MachineIdentity identity = MachineIdentity.fromFile("me.identity");

        // TODO: pull gossip port from config
        Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(), 20001, GOSSIP_METRICS);
        engine.start();

        ExternNexus nexus = new ExternNexus(new Email() {
            @Override
            public void sendCode(String email, String code) {
                System.err.println("Email:" + email + " --> " + code);
            }
        }, dataBase);

        // TODO: link the gRPC stuff here for talking to Adama

        ServiceBase serviceBase = BootstrapFrontend.make(nexus);

        WebConfig webConfig = new WebConfig(config.get_or_create_child("web"));
        final var runnable = new ServiceRunnable(webConfig, serviceBase);
        final var thread = new Thread(runnable);
        thread.start();
        runnable.waitForReady(1000);
        thread.join();
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
        System.out.println("    " + Util.prefix("frontend", Util.ANSI.Green) + "          Spin up a WebSocket front-end node");
        System.out.println("    " + Util.prefix("backend", Util.ANSI.Green) + "           Spin up a gRPC back-end node");
        System.out.println("    " + Util.prefix("yolo", Util.ANSI.Green) + "              Spin up everything on a single node!");
    }

    private static final Metrics GOSSIP_METRICS = new Metrics() {
        @Override
        public void bump_sad_return() {

        }

        @Override
        public void bump_complement() {

        }

        @Override
        public void bump_optimistic_return() {

        }

        @Override
        public void bump_turn_tables() {

        }

        @Override
        public void bump_start() {

        }

        @Override
        public void bump_found_reverse() {

        }

        @Override
        public void bump_quick_gossip() {

        }

        @Override
        public void bump_slow_gossip() {

        }

        @Override
        public void log_error(Throwable cause) {

        }
    };
}

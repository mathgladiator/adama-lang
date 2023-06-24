/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands;

import org.adamalang.api.ApiMetrics;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.commands.services.distributed.Backend;
import org.adamalang.cli.commands.services.distributed.Frontend;
import org.adamalang.cli.commands.services.distributed.Overlord;
import org.adamalang.cli.commands.services.standalone.Solo;
import org.adamalang.frontend.FrontendMetrics;
import org.adamalang.ops.*;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.prometheus.PrometheusDashboard;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
        Backend.run(config);
        return;
      case "overlord":
        Overlord.run(config);
        return;
      case "frontend":
        Frontend.run(config);
        return;
      case "solo":
        Solo.run(config);
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
    System.out.println("    " + Util.prefix("solo", Util.ANSI.Green) + "              Spin up a solo machine");
    System.out.println("    " + Util.prefix("dashboards", Util.ANSI.Green) + "        Produce dashboards for prometheus.");
  }

  public static void serviceAuto(Config config) throws Exception {
    String role = config.get_string("role", "none");
    switch (role) {
      case "backend":
        Backend.run(config);
        return;
      case "overlord":
        Overlord.run(config);
        return;
      case "frontend":
        Frontend.run(config);
        return;
      case "solo":
        Solo.run(config);
        return;
      default:
        System.err.println("invalid role:" + role);
    }
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
    metricsFactory.page("fp", "First Party");
    new FirstPartyMetrics(metricsFactory);
    metricsFactory.finish(new File("./prometheus/consoles"));
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.probe;

import org.adamalang.cli.devbox.Command;
import org.adamalang.cli.devbox.TerminalIO;
import org.adamalang.cli.router.Arguments;
import org.adamalang.common.*;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.contracts.RoutingTarget;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProbeStart {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(ProbeStart.class);
  private final MetricsFactory metricsFactory;
  private final AtomicBoolean alive;
  private final TerminalIO terminal;
  private final MachineIdentity identity;
  private final SimpleExecutor executor;
  private final NetBase base;
  private final InstanceClient client;

  public ProbeStart(Arguments.ServicesProbeArgs args) throws Exception {
    this.metricsFactory = new NoOpMetricsFactory();
    this.alive = new AtomicBoolean(true);
    this.terminal = new TerminalIO();
    String identityFileName = args.config.get_string("identity-filename", "me.identity");
    this.identity = MachineIdentity.fromFile(identityFileName);
    this.base = new NetBase(new NetMetrics(metricsFactory), identity, 1, 1);
    ClientConfig clientConfig = new ClientConfig();
    LocalRegionClientMetrics localRegionClientMetrics = new LocalRegionClientMetrics(metricsFactory);
    HeatMonitor heatMonitor = new HeatMonitor() {
      @Override
      public void heat(String target, double cpu, double memory) {
        // terminal.info("heat|machine=" + target + " cpu=" + cpu + " mem=" + memory);
      }
    };
    RoutingTarget routingTarget = new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {
        // terminal.info("integrate:" + target);
      }
    };
    executor = SimpleExecutor.create("probe");
    client = new InstanceClient(base, clientConfig, localRegionClientMetrics, heatMonitor, routingTarget, args.target, executor);
  }

  public void run() {
    while (alive.get()) {
      try {
        Command command = Command.parse(terminal.readline().trim());
        if (command.is("kill", "exit", "quit", "q", "exut")) {
          terminal.notice("lowering alive");
          alive.set(false);
          executor.shutdown();
        } else if (command.is("help", "h", "?", "")) {
          terminal.info("Wouldn't it be great if there was some like... help here?");
        } else if (command.is("ping")) {
          terminal.info("ping:" + client.ping(2500));
        } else {
          terminal.notice("sending command '" + command.command + "' to adama, please wait...");
          client.probe(command.command, command.args, new Callback<InstanceClient.ProbeResponse>() {
            @Override
            public void success(InstanceClient.ProbeResponse value) {
              if (value.errors != null && value.errors.length > 0) {
                terminal.notice("response|" + value.json);
                for (String err : value.errors) {
                  terminal.error(err);
                }
              } else {
                terminal.info(value.json);
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              terminal.error("command failed:" + ex.code);
            }
          });
        }
      } catch (Exception ex) {
        terminal.error("error:" + ex.getMessage());
      }
    }
  }
}

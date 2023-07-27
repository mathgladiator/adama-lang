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
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.codec.ClientMessage;

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

  public ProbeStart(String target) throws Exception {
    this.metricsFactory = new NoOpMetricsFactory();
    this.alive = new AtomicBoolean(true);
    this.terminal = new TerminalIO();
    this.identity = null;
    this.base = new NetBase(new NetMetrics(metricsFactory), identity, 1, 1);
    ClientConfig clientConfig = new ClientConfig();
    ClientMetrics clientMetrics = new ClientMetrics(metricsFactory);
    HeatMonitor heatMonitor = new HeatMonitor() {
      @Override
      public void heat(String target, double cpu, double memory) {

      }
    };
    RoutingTarget routingTarget = new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {

      }
    };
    executor = SimpleExecutor.create("probe");
    client = new InstanceClient(base, clientConfig, clientMetrics, heatMonitor, routingTarget, target, executor, LOGGER);
  }

  public void run() {
    while (alive.get()) {
      try {
        Command command = Command.parse(terminal.readline().trim());
        if (command.is("kill", "exit", "quit", "q", "exut")) {
          terminal.notice("lowering alive");
          alive.set(false);
          executor.shutdown();
        }
        if (command.is("help", "h", "?")) {
          terminal.info("Wouldn't it be great if there was some like... help here?");
        }
        if (command.is("ping")) {
          terminal.info("ping:" + client.ping(1000));
        }
      } catch (Exception ex) {
        terminal.error("error:" + ex.getMessage());
      }
    }
  }
}

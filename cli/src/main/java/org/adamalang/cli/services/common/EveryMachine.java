/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.common;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.Role;
import org.adamalang.common.*;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.internal.InternalSigner;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicBoolean;

/** configuration for every machine */
public class EveryMachine {
  private static final Logger LOGGER = LoggerFactory.getLogger(EveryMachine.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  public final MachineIdentity identity;
  public final PrivateKey hostKey;
  public final int monitoringPort;
  public final PrometheusMetricsFactory metricsFactory;
  public final String publicKey;
  public final AtomicBoolean alive;
  public final String region;
  public final String role;
  public final int servicePort;
  public final WebConfig webConfig;
  public final WebClientBase webBase;
  public final String machine;
  public final String logsPrefix;
  public final NetBase netBase;
  public final SimpleExecutor system;
  public final Engine engine;

  public EveryMachine(Config config, Role role) throws Exception {
    MachineHeat.install();
    ConfigObject configObjectForWeb = new ConfigObject(config.get_or_create_child(role == Role.Overlord ? "overlord-web" : "web"));
    if (role == Role.Overlord) {
      configObjectForWeb.intOf("http-port", 8081);
    }
    String identityFileName = config.get_string("identity-filename", "me.identity");
    this.identity = MachineIdentity.fromFile(identityFileName);
    KeyPair keyPair = PublicKeyCodec.inventHostKey();
    this.hostKey = keyPair.getPrivate();
    this.publicKey = PublicKeyCodec.encodePublicKey(keyPair);
    this.monitoringPort = config.get_int("monitoring-" + role.name + "-port", role.monitoringPort);
    this.metricsFactory = new PrometheusMetricsFactory(monitoringPort);
    this.alive = new AtomicBoolean(true);
    this.region = config.get_string("region", null);
    this.role = role.name;
    this.webConfig = new WebConfig(configObjectForWeb);
    switch (role) {
      case Adama:
        this.servicePort = config.get_int("adama-port", 8001);
        break;
      default:
        this.servicePort = this.webConfig.port;
    }
    this.machine = this.identity.ip + ":" + servicePort;
    this.webBase = new WebClientBase(this.webConfig);
    this.logsPrefix = role.name + "/" + identity.ip + "/" + monitoringPort;
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      alive.set(false);
      try {
        webBase.shutdown();
      } catch (Exception ex) {
      }
    })));
    this.netBase = new NetBase(new NetMetrics(metricsFactory), identity, 1, 2);
    this.system = SimpleExecutor.create("system");;
    this.engine = netBase.startGossiping();
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      System.out.println("[EveryMachine-Shutdown]");
      alive.set(false);
      try {
        system.shutdown();
      } catch (Exception ex) {
      }
      try {
        netBase.shutdown();
      } catch (Exception ex) {
      }
    })));

    System.out.println("[EveryMachine-Setup]");
    System.out.println("         role:" + role.name);
    System.out.println("           ip: " + identity.ip);
    System.out.println(" service-port: " + servicePort);
    System.out.println(" monitor-port: " + monitoringPort);
    System.out.println("     identity: " + identity.ip);
    System.out.println("  logs-prefix: " + logsPrefix);
    System.out.println("[/EveryMachine-Setup]");
  }

  public void installServices(int publicKeyId) {
    SimpleExecutor services = SimpleExecutor.create("executor");
    FirstPartyServices.install(services, metricsFactory, webBase, new InternalSigner(publicKeyId, hostKey));

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      System.out.println("[Services-Shutdown]");
      alive.set(false);
      try {
        services.shutdown();
      } catch (Exception ex) {

      }
    })));
  }
}

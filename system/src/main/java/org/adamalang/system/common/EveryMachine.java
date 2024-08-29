/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.system.common;

import org.adamalang.CoreServicesNexus;
import org.adamalang.api.SelfClient;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.config.ProductionServiceConfigFactory;
import org.adamalang.system.Role;
import org.adamalang.common.*;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.internal.InternalSigner;
import org.adamalang.region.MeteringBatchSubmit;
import org.adamalang.region.MeteringBatchSubmitMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.metering.BillingDocumentFinder;
import org.adamalang.runtime.sys.metering.MeteringBatchReady;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.CoreServices;
import org.adamalang.system.contracts.JsonConfig;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** configuration for every machine */
public class EveryMachine {
  private static final Logger LOGGER = LoggerFactory.getLogger(EveryMachine.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  public final MachineIdentity identity;
  public final PrivateKey hostKey;
  public final int monitoringPort;
  public final MetricsFactory metricsFactory;
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
  public final SimpleExecutor regionClient;
  public final MultiWebClientRetryPool regionPool;
  public final Engine engine;
  public final SelfClient adamaCurrentRegionClient;
  public final String regionalIdentity;
  public final SimpleExecutor metrics;
  public final SimpleExecutor push;
  public final String environment;
  public final SimpleExecutor compileOffload;
  public final int rxhtmlMaxCacheAgeSeconds;

  public EveryMachine(JsonConfig config, Role role) throws Exception {
    MachineHeat.install();
    ConfigObject configObjectForWeb = new ConfigObject(config.get_or_create_child(role == Role.Overlord ? "overlord-web" : "web"));
    if (role == Role.Overlord) {
      configObjectForWeb.intOf("http-port", 8081);
    }
    String identityFileName = config.get_string("identity-filename", "me.identity");
    this.rxhtmlMaxCacheAgeSeconds = config.get_int("rxhtml-max-cache-age-seconds", 120);
    this.environment = config.get_string("environment", "prod").trim();
    this.regionalIdentity = config.get_string("regional-identity", null);
    this.identity = MachineIdentity.fromFile(identityFileName);
    KeyPair keyPair = PublicKeyCodec.inventHostKey();
    this.hostKey = keyPair.getPrivate();
    this.publicKey = PublicKeyCodec.encodePublicKey(keyPair);
    this.monitoringPort = config.get_int("monitoring-" + role.name + "-port", role.monitoringPort);
    String monitor = config.get_string("monitoring-system", "prometheus");
    if ("prometheus".equals(monitor)) {
      this.metricsFactory = new PrometheusMetricsFactory(monitoringPort);
    } else {
      this.metricsFactory = new NoOpMetricsFactory();
    }
    this.alive = new AtomicBoolean(true);
    this.region = config.get_string("region", null);
    this.role = role.name;
    this.webConfig = new WebConfig(configObjectForWeb);
    this.webConfig.validateForServerUse();
    if (role == Role.Adama) {
      this.servicePort = config.get_int("adama-port", 8001);
    } else {
      this.servicePort = this.webConfig.port;
    }
    this.machine = this.identity.ip + ":" + servicePort;
    this.webBase = new WebClientBase(new WebClientBaseMetrics(metricsFactory), this.webConfig);

    this.regionClient = SimpleExecutor.create("region-client");
    String selfEndpoint = config.get_string("region-endpoint", "wss://aws-us-east-2.adama-platform.com/~s");
    this.regionPool =  new MultiWebClientRetryPool(this.regionClient, webBase, new MultiWebClientRetryPoolMetrics(metricsFactory), new MultiWebClientRetryPoolConfig(new ConfigObject(config.get_or_create_child("http-web"))), ConnectionReady.TRIVIAL, selfEndpoint);
    this.adamaCurrentRegionClient = new SelfClient(regionPool);

    this.logsPrefix = role.name + "/" + identity.ip + "/" + monitoringPort;
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      alive.set(false);
      try {
        webBase.shutdown();
      } catch (Exception ex) {
      }
    })));
    this.netBase = new NetBase(new NetMetrics(metricsFactory), identity, 1, 2);
    this.system = SimpleExecutor.create("system");
    this.engine = netBase.startGossiping();
    this.metrics = SimpleExecutor.create("metrics");
    this.push = SimpleExecutor.create("push");
    this.compileOffload = SimpleExecutor.create("compile");

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      System.out.println("[EveryMachine-Shutdown]");
      alive.set(false);
      try {
        system.shutdown().await(250, TimeUnit.MILLISECONDS);
      } catch (Exception ex) {
      }
      try {
        netBase.shutdown();
      } catch (Exception ex) {
      }
      try {
        regionPool.shutdown();
      } catch (Exception ex) {
      }
      try {
        regionClient.shutdown().await(250, TimeUnit.MILLISECONDS);
      } catch (Exception ex) {
      }
      try {
        metrics.shutdown().await(250, TimeUnit.MILLISECONDS);
      } catch (Exception ex) {
      }
      try {
        push.shutdown().await(250, TimeUnit.MILLISECONDS);
      } catch (Exception ex) {
      }
      try {
        compileOffload.shutdown().await(500, TimeUnit.MILLISECONDS);
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

  public FirstPartyMetrics installServices(int publicKeyId) {
    SimpleExecutor services = SimpleExecutor.create("services-executor-prime");
    SimpleExecutor offload = SimpleExecutor.create("services-executor-offload");
    CoreServicesNexus coreServicesNexus = new CoreServicesNexus(services, offload, metricsFactory, webBase, adamaCurrentRegionClient, new InternalSigner(publicKeyId, hostKey), new ProductionServiceConfigFactory());
    CoreServices.install(coreServicesNexus);
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      System.out.println("[Services-Shutdown]");
      alive.set(false);
      try {
        services.shutdown();
      } catch (Exception ex) {
      }
      try {
        offload.shutdown();
      } catch (Exception ex) {
      }
    })));
    return coreServicesNexus.fpMetrics;
  }

  public MeteringBatchReady makeMeteringBatchReady(BillingDocumentFinder billingDocumentFinder, int publicKeyId) {
    final String identity = new InternalSigner(publicKeyId, hostKey).toIdentity(new NtPrincipal(region + "/" + machine, "region"));
    return new MeteringBatchSubmit(new MeteringBatchSubmitMetrics(metricsFactory), identity, region, machine, billingDocumentFinder, adamaCurrentRegionClient);
  }
}

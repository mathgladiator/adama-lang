/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.cli.Config;
import org.adamalang.cli.services.common.CloudBoot;
import org.adamalang.cli.services.common.EveryMachine;
import org.adamalang.common.*;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.S3;
import org.adamalang.extern.aws.SQS;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.internal.InternalSigner;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.impl.GlobalFinder;
import org.adamalang.mysql.model.*;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.TargetsQuorum;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.runtime.sys.capacity.MachinePicker;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.PrivateKey;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** Common service initialization */
public class CommonServiceInit {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceInit.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);

  public final int monitoringPort;
  public final MachineIdentity identity;
  public final PrivateKey hostKey;
  public final PrometheusMetricsFactory metricsFactory;
  public final DataBase database;
  public final NetBase netBase;
  public final GlobalFinder globalFinder;
  public final SimpleExecutor system;
  public final SimpleExecutor picker;
  public final S3 s3;
  public final AWSConfig awsConfig;
  public final AWSMetrics awsMetrics;
  public final SQS sqs;
  public final String machine;
  public final org.adamalang.common.gossip.Engine engine;
  public final int publicKeyId;
  public final WebConfig webConfig;
  public final WebClientBase webBase;
  public final String masterKey;

  public final SimpleExecutor services;
  public final AtomicBoolean alive;
  public final String region;
  public final String role;
  public final int servicePort;

  public CommonServiceInit(Config config, Role role) throws Exception {
    EveryMachine em = new EveryMachine(config, role);
    this.identity = em.identity;
    this.hostKey = em.hostKey;
    this.metricsFactory = em.metricsFactory;
    this.monitoringPort = em.monitoringPort;
    this.alive = em.alive;
    this.role = em.role;
    this.region = em.region;
    this.machine = em.machine;
    this.webConfig = em.webConfig;
    this.webBase = em.webBase;
    this.servicePort = em.servicePort;
    this.netBase = em.netBase;
    this.system = em.system;

    // only available
    this.masterKey = config.get_string("master-key", null);

    this.database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(metricsFactory));
    this.globalFinder = new GlobalFinder(database, region);
    this.picker = SimpleExecutor.create("picker");

    this.publicKeyId = Hosts.initializeHost(database, this.region, this.machine, role.name, em.publicKey);

    system.schedule(new NamedRunnable("database-ping") {
      @Override
      public void execute() throws Exception {
        try {
          Health.pingDataBase(database);
        } catch (Exception ex) {
          LOGGER.error("health-check-failure-database", ex);
        }
        if (alive.get()) {
          system.schedule(this, (int) (30000 + 30000 * Math.random()));
        }
      }
    }, 5000);

    CloudBoot cb = new CloudBoot(em.alive, em.metricsFactory, em.webBase, config.get_or_create_child("aws"), em.logsPrefix, system);
    this.awsConfig = cb.awsConfig;
    this.awsMetrics = cb.awsMetrics;
    this.s3 = cb.s3;
    this.sqs = cb.sqs;

    engine = netBase.startGossiping();
    // TODO: promote the concept of the multi-region client as "everyone needs a client"
    services = SimpleExecutor.create("executor");
    FirstPartyServices.install(services, metricsFactory, webBase, new InternalSigner(publicKeyId, hostKey));

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      alive.set(false);
      try {
        picker.shutdown();
      } catch (Exception ex) {
      }
      try {
        services.shutdown();
      } catch (Exception ex) {

      }
    })));
  }

  public MultiRegionClient makeGlobalClient(LocalRegionClient client) {
    return new MultiRegionClient(client, region, hostKey, publicKeyId, globalFinder, new TreeMap<>());
  }

  public LocalRegionClient makeLocalClient(HeatMonitor heat) {
    ClientConfig clientConfig = new ClientConfig();
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(metricsFactory);
    MachinePicker fallback = (key, callback) -> {
      try {
        String machine = Hosts.pickStableHostFromRegion(database, region, "adama", key.space);
        if (machine == null) {
          throw new NullPointerException("no capacity available");
        }
        callback.success(machine);
      } catch (Exception ex) {
        callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.NET_FINDER_ROUTER_NULL_MACHINE, ex, EXLOGGER));
      }
    };
    ClientRouter router = ClientRouter.FINDER(metrics, globalFinder, fallback, region);
    LocalRegionClient client = new LocalRegionClient(netBase, clientConfig, metrics, router, heat);

    TargetsQuorum targetsQuorum = new TargetsQuorum(metrics, client.getTargetPublisher());
    system.schedule(new NamedRunnable("list-hosts-database") {
      @Override
      public void execute() throws Exception {
        targetsQuorum.deliverDatabase(Hosts.listHosts(database, region, "adama"));
      }
    }, 50);
    this.engine.subscribe("adama", targetsQuorum::deliverGossip);
    return client;
  }
}

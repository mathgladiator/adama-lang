/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services;

import org.adamalang.ErrorCodes;
import org.adamalang.cli.Config;
import org.adamalang.cli.services.common.CloudBoot;
import org.adamalang.cli.services.common.EveryMachine;
import org.adamalang.cli.services.global.DataBaseBoot;
import org.adamalang.common.*;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.net.NetBase;
import org.adamalang.extern.aws.*;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.impl.GlobalFinder;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.TargetsQuorum;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.runtime.sys.capacity.MachinePicker;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public final S3 s3;
  public final AWSConfig awsConfig;
  public final AWSMetrics awsMetrics;
  public final SQS sqs;
  public final String machine;
  public final Engine engine;
  public final int publicKeyId;
  public final WebConfig webConfig;
  public final WebClientBase webBase;
  public final String masterKey;
  public final AtomicBoolean alive;
  public final String region;
  public final String role;
  public final int servicePort;
  public final SES ses;

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
    this.engine = em.engine;

    // only available
    this.masterKey = config.get_string("master-key", null);
    DataBaseBoot db = new DataBaseBoot(em.alive, config, em.metricsFactory, em.system);
    this.database = db.database;

    this.globalFinder = new GlobalFinder(database, region, machine);
    this.publicKeyId = Hosts.initializeHost(database, this.region, this.machine, role.name, em.publicKey);

    CloudBoot cb = new CloudBoot(em.alive, em.metricsFactory, em.webBase, config.get_or_create_child("aws"), em.logsPrefix, system);
    this.awsConfig = cb.awsConfig;
    this.awsMetrics = cb.awsMetrics;
    this.s3 = cb.s3;
    this.sqs = cb.sqs;
    this.ses = cb.ses;

    em.installServices(publicKeyId);
  }

  public MultiRegionClient makeGlobalClient(LocalRegionClient client) {
    return new MultiRegionClient(client, region, hostKey, publicKeyId, new TreeMap<>());
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

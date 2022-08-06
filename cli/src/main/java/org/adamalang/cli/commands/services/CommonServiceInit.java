/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.services;

import org.adamalang.cli.Config;
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.S3;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Finder;
import org.adamalang.mysql.model.Health;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.TargetsQuorum;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.transforms.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicBoolean;

/** Common service initialization */
public class CommonServiceInit {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceInit.class);
  public final AtomicBoolean alive;
  public final String region;
  public final String role;

  public final int servicePort;
  public final int monitoringPort;
  public final MachineIdentity identity;
  public final PrivateKey hostKey;
  public final PrometheusMetricsFactory metricsFactory;
  public final DataBase database;
  public final NetBase netBase;
  public final Finder finder;
  public final SimpleExecutor system;
  public final S3 s3;
  public final AWSConfig awsConfig;
  public final AWSMetrics awsMetrics;
  public final String machine;
  public final org.adamalang.common.gossip.Engine engine;
  public final int publicKeyId;

  public CommonServiceInit(Config config, Role role, int servicePort) throws Exception {
    MachineHeat.install();
    String identityFileName = config.get_string("identity_filename", "me.identity");
    KeyPair keyPair = Authenticator.inventHostKey();
    this.alive = new AtomicBoolean(true);
    this.region = config.get_string("region", null);
    this.role = role.name;
    this.servicePort = servicePort;
    this.monitoringPort = config.get_int("monitoring_" + role.name + "_port", role.monitoringPort);
    this.identity = MachineIdentity.fromFile(identityFileName);
    this.hostKey = keyPair.getPrivate();
    this.metricsFactory = new PrometheusMetricsFactory(monitoringPort);
    this.database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(metricsFactory));
    this.netBase = new NetBase(new NetMetrics(metricsFactory), identity, 1, 2);
    this.finder = new Finder(database, region);
    this.system = SimpleExecutor.create("system");
    this.machine = this.identity.ip + ":" + servicePort;
    this.publicKeyId = Hosts.initializeHost(database, this.region, this.machine, role.name, Authenticator.encodePublicKey(keyPair));

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

    this.awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    this.awsMetrics = new AWSMetrics(metricsFactory);
    this.s3 = new S3(awsConfig, awsMetrics);
    String prefix = "logs/" + role.name + "/" + identity.ip + "/" + monitoringPort;
    system.schedule(new NamedRunnable("archive-s3") {
      @Override
      public void execute() throws Exception {
        try {
          s3.uploadLogs(new File("logs"), prefix);
        } catch (Exception ex) {
          LOGGER.error("error-uploading-logs", ex);
        } finally {
          if (alive.get()) {
            system.schedule(this, 60000);
          } else {
            system.shutdown();
          }
        }
      }
    }, 5000);
    engine = netBase.startGossiping();

    System.out.println("[Setup]");
    System.out.println("         role:" + role.name);
    System.out.println("           ip: " + identity.ip);
    System.out.println(" service-port: " + servicePort);
    System.out.println(" monitor-port: " + monitoringPort);
    System.out.println("     database: " + database.databaseName);
    System.out.println("     identity: " + identity.ip);
    System.out.println("  logs-prefix: " + prefix);
    System.out.println("[/Setup]");

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        alive.set(false);
        system.shutdown();
        netBase.shutdown();
      }
    })));
  }

  public Client makeClient() {
    ClientConfig clientConfig = new ClientConfig();
    ClientMetrics metrics = new ClientMetrics(metricsFactory);
    ClientRouter router = ClientRouter.FINDER(metrics, finder, region);
    Client client = new Client(netBase, clientConfig, metrics, router, null);

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

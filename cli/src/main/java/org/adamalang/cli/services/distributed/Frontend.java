/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.distributed;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.CommonServiceInit;
import org.adamalang.cli.services.FrontendHttpHandler;
import org.adamalang.cli.services.Role;
import org.adamalang.common.ConfigObject;
import org.adamalang.extern.AssetSystemImpl;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.extern.aws.SES;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.net.client.Client;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.RedirectAndWellknownServiceRunnable;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Frontend {

  public final MultiRegionClient adama;

  public Frontend(Config config, CommonServiceInit init, Client client) throws Exception {
    FrontendHttpHandler http = new FrontendHttpHandler(init, client);
    Email email = new SES(init.webBase, init.awsConfig, init.awsMetrics);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    this.adama = init.makeGlobalClient(client);
    AssetSystemImpl assets = new AssetSystemImpl(init.database, init.masterKey, adama, init.s3);
    ArrayList<String> superKeys = config.get_str_list("super-public-keys");

    ExternNexus nexus = new ExternNexus(frontendConfig, email, init.database, adama, assets, init.metricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, init.masterKey, init.webBase, init.region, init.hostKey, init.publicKeyId, superKeys.toArray(new String[superKeys.size()]), init.sqs);
    System.err.println("ExternNexus constructed");
    ServiceBase serviceBase = BootstrapFrontend.make(nexus, http);
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latchForHeartbeat = new CountDownLatch(1);
    init.engine.createLocalApplicationHeartbeat("web", init.webConfig.port, init.monitoringPort, (hb) -> {
      // TODO: have some sense of health checking in the web package
      heartbeat.set(hb);
      latchForHeartbeat.countDown();
    });
    if (!latchForHeartbeat.await(10000, TimeUnit.MILLISECONDS)) {
      throw new Exception("Failed to Register as Application");
    }
    WebMetrics webMetrics = new WebMetrics(init.metricsFactory);
    final var redirect = new RedirectAndWellknownServiceRunnable(init.webConfig, webMetrics, init.s3, () -> {});
    Thread redirectThread = new Thread(redirect);
    redirectThread.start();
    final var runnable = new ServiceRunnable(init.webConfig, webMetrics, serviceBase, init.makeCertificateFinder(), heartbeat.get());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("shutting down frontend");
      try {
        runnable.shutdown();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        redirect.shutdown();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        nexus.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }));
    System.err.println("running frontend");
    runnable.run();
    System.err.println("frontend finished");
  }

  public static void run(Config config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Web);
    Client client = init.makeClient(null);
    new Frontend(config, init, client);
  }
}

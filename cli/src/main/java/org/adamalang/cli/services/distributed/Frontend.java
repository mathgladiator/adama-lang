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
import org.adamalang.common.TimeSource;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.frontend.global.BootstrapGlobalServiceBase;
import org.adamalang.frontend.global.GlobalAssetSystem;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.impl.GlobalDomainFinder;
import org.adamalang.mysql.impl.GlobalRxHtmlFetcher;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.runtime.sys.domains.CachedDomainFinder;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.rxhtml.CachedRxHtmlFetcher;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlFetcher;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.service.CertificateBoot;
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

  public Frontend(Config config, CommonServiceInit init, LocalRegionClient client) throws Exception {
    this.adama = init.makeGlobalClient(client);
    DomainFinder domainFinder = new CachedDomainFinder(TimeSource.REAL_TIME, 1000, 5 * 60 * 1000, init.system, new GlobalDomainFinder(init.database, init.masterKey));
    RxHtmlFetcher rxHtmlFetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 60 * 1000, init.system, new GlobalRxHtmlFetcher(init.database));
    FrontendHttpHandler http = new FrontendHttpHandler(init.webConfig, domainFinder, rxHtmlFetcher, adama, new PrivateKeyWithId(init.publicKeyId, init.hostKey));
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    GlobalAssetSystem assets = new GlobalAssetSystem(init.database, init.masterKey, adama, init.s3);
    ArrayList<String> superKeys = config.get_str_list("super-public-keys");

    GlobalExternNexus nexus = new GlobalExternNexus(frontendConfig, init.ses, init.database, adama, assets, init.metricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, init.masterKey, init.webBase, init.region, init.hostKey, init.publicKeyId, superKeys.toArray(new String[superKeys.size()]), init.sqs, init.globalFinder, new PrivateKeyWithId(init.publicKeyId, init.hostKey));
    System.err.println("ExternNexus constructed");

    ServiceBase serviceBase = BootstrapGlobalServiceBase.make(nexus, http);
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
    final var redirect = new RedirectAndWellknownServiceRunnable(init.webConfig, webMetrics, init.s3, () -> {
    });
    Thread redirectThread = new Thread(redirect);
    redirectThread.start();
    CertificateFinder certificateFinder = CertificateBoot.make(init.webConfig, domainFinder, init.system);
    final var runnable = new ServiceRunnable(init.webConfig, webMetrics, serviceBase, certificateFinder, heartbeat.get());
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
    LocalRegionClient client = init.makeLocalClient(null);
    new Frontend(config, init, client);
  }
}

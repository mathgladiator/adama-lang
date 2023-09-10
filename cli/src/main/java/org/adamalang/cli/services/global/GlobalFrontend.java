/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.global;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.FrontendHttpHandler;
import org.adamalang.cli.services.Role;
import org.adamalang.cli.services.common.CloudBoot;
import org.adamalang.cli.services.common.EveryMachine;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.TimeSource;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.frontend.global.BootstrapGlobalServiceBase;
import org.adamalang.frontend.global.GlobalAssetSystem;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.impl.GlobalDomainFinder;
import org.adamalang.mysql.impl.GlobalFinder;
import org.adamalang.mysql.impl.GlobalRxHtmlFetcher;
import org.adamalang.mysql.model.Hosts;
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

public class GlobalFrontend {
  public static void run(Config config) throws Exception {
    EveryMachine em = new EveryMachine(config, Role.Web);
    DataBaseBoot db = new DataBaseBoot(em.alive, config, em.metricsFactory, em.system);
    CloudBoot cb = new CloudBoot(em.alive, em.metricsFactory, em.webBase, config.get_or_create_child("aws"), em.logsPrefix, em.system);
    String masterKey = config.get_string("master-key", null);
    // TODO: REGION-SYNC HERE
    DomainFinder domainFinder = new CachedDomainFinder(TimeSource.REAL_TIME, 1000, 5 * 60 * 1000, em.system, new GlobalDomainFinder(db.database, masterKey));
    RxHtmlFetcher rxHtmlFetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 60 * 1000, em.system, new GlobalRxHtmlFetcher(db.database));
    int publicKeyId = Hosts.initializeHost(db.database, em.region, em.machine, "web", em.publicKey);
    GlobalFinder globalFinder = new GlobalFinder(db.database, em.region, em.machine);

    // public MultiRegionClient(LocalRegionClient local, String region, PrivateKey privateKey, int keyId, SimpleFinderService finder, TreeMap<String, SelfClient> remoteRegions) {
    MultiRegionClient adama = null; // TODO: create the multi-region client

    FrontendHttpHandler http = new FrontendHttpHandler(em.alive, em.system, em.webConfig, domainFinder, rxHtmlFetcher, adama, new PrivateKeyWithId(publicKeyId, em.hostKey));
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    Logger accessLog = LoggerFactory.getLogger("access");
    GlobalAssetSystem assets = new GlobalAssetSystem(db.database, masterKey, adama, cb.s3);
    ArrayList<String> superKeys = config.get_str_list("super-public-keys");
    ArrayList<String> regionalKeys = config.get_str_list("regional-public-keys");

    GlobalExternNexus nexus = new GlobalExternNexus(frontendConfig, cb.ses, db.database, adama, assets, em.metricsFactory, new File("inflight"), (item) -> {
      accessLog.debug(item.toString());
    }, masterKey, em.webBase, em.region, em.hostKey, publicKeyId, superKeys.toArray(new String[superKeys.size()]), regionalKeys.toArray(new String[superKeys.size()]), cb.sqs, globalFinder, new PrivateKeyWithId(publicKeyId, em.hostKey));
    System.out.println("[GlobalFrontend:ExternNexus constructed]");


    ServiceBase serviceBase = BootstrapGlobalServiceBase.make(nexus, http);
    AtomicReference<Runnable> heartbeat = new AtomicReference<>();
    CountDownLatch latchForHeartbeat = new CountDownLatch(1);
    em.engine.createLocalApplicationHeartbeat("web", em.webConfig.port, em.monitoringPort, (hb) -> {
      // TODO: have some sense of health checking in the web package
      heartbeat.set(hb);
      latchForHeartbeat.countDown();
    });
    if (!latchForHeartbeat.await(10000, TimeUnit.MILLISECONDS)) {
      throw new Exception("Failed to Register as Application");
    }
    WebMetrics webMetrics = new WebMetrics(em.metricsFactory);
    final var redirect = new RedirectAndWellknownServiceRunnable(em.webConfig, webMetrics, cb.s3, () -> {
    });
    Thread redirectThread = new Thread(redirect);
    redirectThread.start();
    CertificateFinder certificateFinder = CertificateBoot.make(em.webConfig, domainFinder, em.system);
    final var runnable = new ServiceRunnable(em.webConfig, webMetrics, serviceBase, certificateFinder, domainFinder, heartbeat.get());
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
    System.out.println("[GlobalFrontend:Running]");
    runnable.run();
    System.out.println("[GlobalFrontend:Finished]");
  }
}

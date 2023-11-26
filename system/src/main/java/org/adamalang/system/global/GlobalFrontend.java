/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.system.global;

import org.adamalang.auth.CachedAuthenticator;
import org.adamalang.auth.GlobalAuthenticator;
import org.adamalang.system.FrontendHttpHandler;
import org.adamalang.system.Role;
import org.adamalang.system.common.CloudBoot;
import org.adamalang.system.common.EveryMachine;
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
import org.adamalang.system.contracts.JsonConfig;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.io.JsonLogger;
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
  public static void run(JsonConfig config) throws Exception {
    EveryMachine em = new EveryMachine(config, Role.Web);
    DataBaseBoot db = new DataBaseBoot(em.alive, config, em.metricsFactory, em.system);
    CloudBoot cb = new CloudBoot(em.alive, em.metricsFactory, em.webBase, config.get_or_create_child("aws"), em.logsPrefix, em.system);
    String masterKey = config.get_string("master-key", null);
    // TODO: REGION-SYNC HERE
    DomainFinder domainFinder = new CachedDomainFinder(TimeSource.REAL_TIME, 1000, 5 * 60 * 1000, em.system, new GlobalDomainFinder(db.database, masterKey));
    RxHtmlFetcher rxHtmlFetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 60 * 1000, em.system, new GlobalRxHtmlFetcher(db.database, em.environment));
    int publicKeyId = Hosts.initializeHost(db.database, em.region, em.machine, "web", em.publicKey);
    GlobalFinder globalFinder = new GlobalFinder(db.database, em.region, em.machine);
    GlobalAuthenticator globalAuthenticator = new GlobalAuthenticator(db.database, em.system);
    CachedAuthenticator cachedAuthenticator = new CachedAuthenticator(TimeSource.REAL_TIME, 4096, 120 * 1000, em.system, globalAuthenticator);
    cachedAuthenticator.startSweeping(em.alive, 10000, 20000);

    // public MultiRegionClient(LocalRegionClient local, String region, PrivateKey privateKey, int keyId, SimpleFinderService finder, TreeMap<String, SelfClient> remoteRegions) {
    MultiRegionClient adama = null; // TODO: create the multi-region client
    Logger accessLog = LoggerFactory.getLogger("access");
    JsonLogger accessLogger = (item) -> {
      accessLog.debug(item.toString());
    };

    FrontendHttpHandler http = new FrontendHttpHandler(em.alive, em.system, em.webConfig, domainFinder, rxHtmlFetcher, cachedAuthenticator, adama, new PrivateKeyWithId(publicKeyId, em.hostKey), accessLogger);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    GlobalAssetSystem assets = new GlobalAssetSystem(db.database, masterKey, cachedAuthenticator, adama, cb.s3);
    ArrayList<String> superKeys = config.get_str_list("super-public-keys");
    ArrayList<String> regionalKeys = config.get_str_list("regional-public-keys");

    GlobalExternNexus nexus = new GlobalExternNexus(frontendConfig, cb.ses, db.database, adama, cachedAuthenticator, assets, em.metricsFactory, new File("inflight"), accessLogger, masterKey, em.webBase, em.region, em.machine, em.hostKey, publicKeyId, superKeys.toArray(new String[superKeys.size()]), regionalKeys.toArray(new String[superKeys.size()]), cb.sqs, globalFinder, new PrivateKeyWithId(publicKeyId, em.hostKey));
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
    CertificateFinder certificateFinder = CertificateBoot.make(em.alive, em.webConfig, domainFinder, em.system);
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

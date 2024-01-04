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
package org.adamalang.system.distributed;

import org.adamalang.auth.CachedAuthenticator;
import org.adamalang.auth.GlobalAuthenticator;
import org.adamalang.runtime.deploy.DeploymentMetrics;
import org.adamalang.runtime.deploy.ManagedAsyncByteCodeCache;
import org.adamalang.system.CommonServiceInit;
import org.adamalang.system.FrontendHttpHandler;
import org.adamalang.system.Role;
import org.adamalang.common.*;
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
import org.adamalang.runtime.sys.web.rxhtml.CachedRxHtmlFetcher;
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

public class Frontend {
  private static final Logger LOGGER = LoggerFactory.getLogger(Frontend.class);
  public final MultiRegionClient adama;
  public final LocalRegionClient local;
  private final ServiceRunnable service;

  public Frontend(JsonConfig config, CommonServiceInit init, LocalRegionClient client) throws Exception {
    this.local = client;
    this.adama = init.makeGlobalClient(client);
    CachedDomainFinder domainFinder = new CachedDomainFinder(TimeSource.REAL_TIME, 1000, 5 * 60 * 1000, init.system, new GlobalDomainFinder(init.database, init.masterKey));
    domainFinder.startSweeping(init.alive, 1500, 3000);
    CachedRxHtmlFetcher rxHtmlFetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 60 * 1000, init.system, new GlobalRxHtmlFetcher(init.database, init.em.environment));
    rxHtmlFetcher.startSweeping(init.alive, 1500, 3000);
    GlobalAuthenticator globalAuthenticator = new GlobalAuthenticator(init.database, init.em.system);
    CachedAuthenticator cachedAuthenticator = new CachedAuthenticator(TimeSource.REAL_TIME, 4096, 120 * 1000, init.em.system, globalAuthenticator);
    cachedAuthenticator.startSweeping(init.em.alive, 10000, 20000);
    Logger accessLog = LoggerFactory.getLogger("access");
    JsonLogger accessLogger = (item) -> {
      accessLog.debug(item.toString());
    };
    FrontendHttpHandler http = new FrontendHttpHandler(init.alive, init.system, init.webConfig, domainFinder, rxHtmlFetcher, cachedAuthenticator, adama, new PrivateKeyWithId(init.publicKeyId, init.hostKey), accessLogger);
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(config.get_or_create_child("saas")));
    GlobalAssetSystem assets = new GlobalAssetSystem(init.database, init.masterKey, cachedAuthenticator, adama, init.s3);
    ArrayList<String> superKeys = config.get_str_list("super-public-keys");
    ArrayList<String> regionalKeys = config.get_str_list("regional-public-keys");
    ManagedAsyncByteCodeCache byteCodeCache = new ManagedAsyncByteCodeCache(init.s3, init.em.compileOffload, init.deploymentMetrics);
    GlobalExternNexus nexus = new GlobalExternNexus(frontendConfig, init.ses, init.database, adama, cachedAuthenticator, assets, init.metricsFactory, new File("inflight"), accessLogger, init.masterKey, init.webBase, init.region, init.machine, init.hostKey, init.publicKeyId, superKeys.toArray(new String[superKeys.size()]), regionalKeys.toArray(new String[superKeys.size()]), init.sqs, init.globalFinder, new PrivateKeyWithId(init.publicKeyId, init.hostKey), byteCodeCache, init.s3);
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
    CertificateFinder certificateFinder = CertificateBoot.make(init.alive, init.webConfig, domainFinder, init.system);
    this.service = new ServiceRunnable(init.webConfig, webMetrics, serviceBase, certificateFinder, domainFinder, heartbeat.get());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("shutting down frontend");
      try {
        service.shutdown();
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
  }

  public void run() {
    System.err.println("running frontend");
    LOGGER.error("Started");
    service.run();
    System.err.println("frontend finished");
  }

  public static Frontend run(JsonConfig config) throws Exception {
    CommonServiceInit init = new CommonServiceInit(config, Role.Web);
    LocalRegionClient client = init.makeLocalClient(null);
    return new Frontend(config, init, client);
  }
}

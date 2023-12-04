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
package org.adamalang.frontend.global;

import org.adamalang.api.GlobalApiMetrics;
import org.adamalang.api.RegionApiMetrics;
import org.adamalang.auth.Authenticator;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.extern.Email;
import org.adamalang.extern.SignalControl;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.frontend.FrontendMetrics;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.impl.*;
import org.adamalang.runtime.deploy.AsyncByteCodeCache;
import org.adamalang.runtime.sys.capacity.CachedCapacityPlanFetcher;
import org.adamalang.runtime.sys.capacity.CapacityPlanFetcher;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.io.JsonLogger;

import java.io.File;
import java.security.PrivateKey;
import java.security.SecureRandom;

public class GlobalExternNexus {
  public final FrontendConfig config;
  public final Email email;
  public final DataBase database;
  public final GlobalApiMetrics globalApiMetrics;
  public final RegionApiMetrics regionApiMetrics;
  public final File attachmentRoot;
  public final JsonLogger accessLogger;
  public final String masterKey;
  public final MultiRegionClient adama;
  public final WebClientBase webBase;
  public final String region;
  public final PrivateKey webHostKey;
  public final int publicKeyId;
  public final AssetSystem assets;
  public final FrontendMetrics frontendMetrics;
  public final String[] superPublicKeys;
  public final String[] regionalPublicKeys;
  public final SignalControl signalControl;
  public final GlobalFinder finder;
  public final MySQLFinderCore finderCore;
  public final PrivateKeyWithId signingKey;
  public final GlobalMetricsReporter metricsReporter;
  public final SimpleExecutor metrics;
  public final String machine;
  public final VAPIDFactory vapidFactory;
  public final Authenticator authenticator;
  public final SimpleExecutor crypto;
  public final AsyncByteCodeCache byteCodeCache;

  public final SimpleExecutor capacity;
  public final GlobalCapacityOverseer overseer;
  public final CapacityPlanFetcher capacityPlanFetcher;

  public GlobalExternNexus(FrontendConfig config, Email email, DataBase database, MultiRegionClient adama, Authenticator authenticator, AssetSystem assets, MetricsFactory metricsFactory, File attachmentRoot, JsonLogger accessLogger, String masterKey, WebClientBase webBase, String region, String machine, PrivateKey webHostKey, int publicKeyId, String[] superPublicKeys,  String[] regionalPublicKeys, SignalControl signalControl, GlobalFinder finder, PrivateKeyWithId signingKey, AsyncByteCodeCache byteCodeCache) {
    this.config = config;
    this.email = email;
    this.database = database;
    this.authenticator = authenticator;
    this.globalApiMetrics = new GlobalApiMetrics(metricsFactory);
    this.regionApiMetrics = new RegionApiMetrics(metricsFactory);
    this.frontendMetrics = new FrontendMetrics(metricsFactory);
    this.attachmentRoot = attachmentRoot;
    this.accessLogger = accessLogger;
    this.masterKey = masterKey;
    this.adama = adama;
    this.assets = assets;
    this.webBase = webBase;
    this.region = region;
    this.machine = machine;
    this.webHostKey = webHostKey;
    this.publicKeyId = publicKeyId;
    this.superPublicKeys = superPublicKeys;
    this.regionalPublicKeys = regionalPublicKeys;
    this.signalControl = signalControl;
    this.finder = finder;
    this.finderCore = finder.core;
    this.capacity = SimpleExecutor.create("capacity");
    this.overseer = new GlobalCapacityOverseer(database);
    this.signingKey = signingKey;
    this.metrics = SimpleExecutor.create("metrics-report");
    this.metricsReporter = new GlobalMetricsReporter(database, metrics);
    attachmentRoot.mkdir();
    this.vapidFactory = new VAPIDFactory(new SecureRandom());
    this.crypto = SimpleExecutor.create("crypto");
    this.byteCodeCache = byteCodeCache;
    this.capacityPlanFetcher = new CachedCapacityPlanFetcher(TimeSource.REAL_TIME, 1024, 60000, capacity, new GlobalCapacityPlanFetcher(database));
  }

  public void close() throws Exception {
    database.close();
    adama.shutdown();
    webBase.shutdown();
    metrics.shutdown();
    crypto.shutdown();
    capacity.shutdown();
  }
}

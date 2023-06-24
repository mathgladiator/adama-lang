/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.extern;

import org.adamalang.api.ApiMetrics;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.frontend.FrontendMetrics;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.io.JsonLogger;

import java.io.File;
import java.security.PrivateKey;

public class ExternNexus {
  public final FrontendConfig config;
  public final Email email;
  public final DataBase database;
  public final ApiMetrics metrics;
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
  public final SignalControl signalControl;

  public ExternNexus(FrontendConfig config, Email email, DataBase database, MultiRegionClient adama, AssetSystem assets, MetricsFactory metricsFactory, File attachmentRoot, JsonLogger accessLogger, String masterKey, WebClientBase webBase, String region, PrivateKey webHostKey, int publicKeyId, String[] superPublicKeys, SignalControl signalControl) {
    this.config = config;
    this.email = email;
    this.database = database;
    this.metrics = new ApiMetrics(metricsFactory);
    this.frontendMetrics = new FrontendMetrics(metricsFactory);
    this.attachmentRoot = attachmentRoot;
    this.accessLogger = accessLogger;
    this.masterKey = masterKey;
    this.adama = adama;
    this.assets = assets;
    this.webBase = webBase;
    this.region = region;
    this.webHostKey = webHostKey;
    this.publicKeyId = publicKeyId;
    this.superPublicKeys = superPublicKeys;
    this.signalControl = signalControl;
    attachmentRoot.mkdir();
  }
  public void close() throws Exception {
    database.close();
    adama.shutdown();
    webBase.shutdown();
  }
}

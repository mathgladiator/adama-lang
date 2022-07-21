/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern;

import org.adamalang.api.ApiMetrics;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Finder;
import org.adamalang.net.client.Client;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.io.JsonLogger;

import java.io.File;

public class ExternNexus {
  public final FrontendConfig config;
  public final Email email;
  public final AssetUploader uploader;
  public final DataBase dataBase;
  // public final Client client;
  public final ApiMetrics metrics;
  public final File attachmentRoot;
  public final Finder finder;
  public final JsonLogger accessLogger;
  public final AssetDownloader downloader;
  public final String masterKey;
  public final MultiRegionClient adama;

  public ExternNexus(FrontendConfig config, Email email, AssetUploader uploader, AssetDownloader downloader, DataBase dataBase, Finder finder, Client client, MetricsFactory metricsFactory, File attachmentRoot, JsonLogger accessLogger, String masterKey) {
    this.config = config;
    this.email = email;
    this.uploader = uploader;
    this.downloader = downloader;
    this.dataBase = dataBase;
    this.finder = finder;
    // this.client = client;
    this.metrics = new ApiMetrics(metricsFactory);
    this.attachmentRoot = attachmentRoot;
    this.accessLogger = accessLogger;
    this.masterKey = masterKey;
    this.adama = new MultiRegionClient(dataBase, client, finder);
    attachmentRoot.mkdir();
  }

  public void close() throws Exception {
    dataBase.close();
    adama.shutdown();
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern;

import org.adamalang.api.ApiMetrics;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.net.client.Client;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.io.JsonLogger;

import java.io.File;

public class ExternNexus {
  public final FrontendConfig config;
  public final Email email;
  public final AssetUploader uploader;
  public final DataBase dataBaseManagement;
  public final DataBase dataBaseDeployments;
  public final DataBase dataBaseBackend;
  public final Client client;
  public final ApiMetrics metrics;
  public final File attachmentRoot;
  public final JsonLogger accessLogger;
  public final AssetDownloader downloader;

  public ExternNexus(FrontendConfig config, Email email, AssetUploader uploader, AssetDownloader downloader, DataBase dataBaseManagement, DataBase dataBaseDeployments, DataBase dataBaseBackend, Client client, MetricsFactory metricsFactory, File attachmentRoot, JsonLogger accessLogger) {
    this.config = config;
    this.email = email;
    this.uploader = uploader;
    this.downloader = downloader;
    this.dataBaseManagement = dataBaseManagement;
    this.dataBaseDeployments = dataBaseDeployments;
    this.dataBaseBackend = dataBaseBackend;
    this.client = client;
    this.metrics = new ApiMetrics(metricsFactory);
    this.attachmentRoot = attachmentRoot;
    this.accessLogger = accessLogger;
    attachmentRoot.mkdir();
  }

  public void close() throws Exception {
    dataBaseDeployments.close();
    dataBaseManagement.close();
    client.shutdown();
  }
}

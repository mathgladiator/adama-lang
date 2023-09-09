/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Metrics;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.remote.MetricsReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalMetricsReporter implements MetricsReporter {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalMetricsReporter.class);
  private final DataBase database;
  private final SimpleExecutor executor;

  public GlobalMetricsReporter(DataBase database, SimpleExecutor executor) {
    this.database = database;
    this.executor = executor;
  }

  @Override
  public void emitMetrics(Key key, String metricsPayload) {
    executor.execute(new NamedRunnable("mysql-emit-metrics") {
      @Override
      public void execute() throws Exception {
        try {
          Metrics.putOrUpdateDocumentMetrics(database, key.space, key.key, metricsPayload);
          database.metrics.metrics_success.run();
        } catch (Exception ex) {
          LOGGER.error("failed-submit-metrics", ex);
          database.metrics.metrics_failure.run();
        }
      }
    });
  }
}

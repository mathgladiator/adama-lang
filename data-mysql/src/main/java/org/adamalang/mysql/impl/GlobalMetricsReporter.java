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

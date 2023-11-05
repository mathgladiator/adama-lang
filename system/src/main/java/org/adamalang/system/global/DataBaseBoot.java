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

import org.adamalang.common.ConfigObject;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Health;
import org.adamalang.system.contracts.JsonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DataBaseBoot {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseBoot.class);

  public final DataBase database;

  public DataBaseBoot(AtomicBoolean alive, JsonConfig config, MetricsFactory metricsFactory, SimpleExecutor system) throws Exception {
    this.database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(metricsFactory));
    AtomicReference<Runnable> cancel = new AtomicReference<>();
    cancel.set(system.schedule(new NamedRunnable("database-ping") {
      @Override
      public void execute() throws Exception {
        try {
          Health.pingDataBase(database);
        } catch (Exception ex) {
          LOGGER.error("health-check-failure-database", ex);
        }
        if (alive.get()) {
          cancel.set(system.schedule(this, (int) (30000 + 30000 * Math.random())));
        }
      }
    }, 5000));
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      System.out.println("[DataBaseBoot-Shutdown]");
      alive.set(false);
      cancel.get().run();
    })));
  }
}

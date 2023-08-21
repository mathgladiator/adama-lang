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
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Health;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DataBaseBoot {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseBoot.class);

  public final DataBase database;

  public DataBaseBoot(AtomicBoolean alive, Config config, MetricsFactory metricsFactory, SimpleExecutor system) throws Exception {
    this.database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(metricsFactory));
    AtomicReference<Runnable> cancel = new AtomicReference<>();
    cancel.set(system.schedule(new NamedRunnable("database-ping") {
      @Override
      public void execute() throws Exception {
        System.out.println("[DataBaseBoot-Shutdown]");
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
      alive.set(false);
      cancel.get().run();
    })));
  }
}

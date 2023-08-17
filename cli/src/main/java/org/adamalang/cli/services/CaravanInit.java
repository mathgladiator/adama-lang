/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services;

import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.cli.Config;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.ManagedDataService;
import org.adamalang.runtime.data.managed.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class CaravanInit {
  private static final Logger LOGGER = LoggerFactory.getLogger(CaravanInit.class);
  private final SimpleExecutor caravanExecutor;
  private final SimpleExecutor managedExecutor;
  public final ManagedDataService service;
  public final CaravanDataService caravanDataService;
  private final Thread flusher;
  
  public CaravanInit(CommonServiceInit init, Config config) throws Exception {
    String caravanRoot = config.get_string("caravan-root", "caravan");
    this.caravanExecutor = SimpleExecutor.create("caravan");
    this.managedExecutor = SimpleExecutor.create("managed-base");
    File caravanPath = new File(caravanRoot);
    caravanPath.mkdir();
    File walRoot = new File(caravanPath, "wal");
    File dataRoot = new File(caravanPath, "data");
    walRoot.mkdir();
    dataRoot.mkdir();
    File storePath = new File(dataRoot, "store");
    DurableListStore store = new DurableListStore(new DiskMetrics(init.metricsFactory), storePath, walRoot, 4L * 1024 * 1024 * 1024, 16 * 1024 * 1024, 64 * 1024 * 1024);
    this.caravanDataService = new CaravanDataService(new CaravanMetrics(init.metricsFactory), init.s3, store, caravanExecutor);
    Base managedBase = new Base(init.globalFinder, caravanDataService, init.s3, init.region, init.machine, managedExecutor, 2 * 60 * 1000);
    this.service = new ManagedDataService(managedBase);
    this.flusher = new Thread(() -> {
      int diagnostics = 0;
      while (init.alive.get()) {
        try {
          if (diagnostics-- < 0) {
            diagnostics = 1000 * 60 * 2;
            caravanDataService.diagnostics(new Callback<String>() {
              @Override
              public void success(String value) {
                LOGGER.error("caravan-dump: " + value);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOGGER.error("failed-caravan-dump:" + ex.code);
              }
            });
          }
          Thread.sleep(0, 800000);
          caravanDataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
          return;
        }
      }
    });
    flusher.start();
  }

  public void shutdown() {
    this.flusher.interrupt();
    this.caravanExecutor.shutdown();
    this.managedExecutor.shutdown();
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.ManagedDataService;
import org.adamalang.runtime.data.PostDocumentDelete;
import org.adamalang.runtime.data.managed.Base;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** booting a production caravan data service */
public class CaravanBoot {
  private final SimpleExecutor caravanExecutor;
  private final SimpleExecutor managedExecutor;
  public final ManagedDataService service;
  public final CaravanDataService caravanDataService;
  private final Thread flusher;

  public CaravanBoot(AtomicBoolean alive, String caravanRoot, MetricsFactory metricsFactory, String region, String machine, FinderService finder, Cloud cloud, PostDocumentDelete delete) throws Exception {
    this.caravanExecutor = SimpleExecutor.create("caravan");
    this.managedExecutor = SimpleExecutor.create("managed-base");
    File caravanPath = new File(caravanRoot);
    caravanPath.mkdir();
    File walRoot = new File(caravanPath, "wal");
    File dataRoot = new File(caravanPath, "data");
    walRoot.mkdir();
    dataRoot.mkdir();
    File storePath = new File(dataRoot, "store");
    DurableListStore store = new DurableListStore(new DiskMetrics(metricsFactory), storePath, walRoot, 4L * 1024 * 1024 * 1024, 16 * 1024 * 1024, 64 * 1024 * 1024);
    this.caravanDataService = new CaravanDataService(new CaravanMetrics(metricsFactory), cloud, store, caravanExecutor);
    Base managedBase = new Base(finder, caravanDataService, delete, region, machine, managedExecutor, 2 * 60 * 1000);
    this.service = new ManagedDataService(managedBase);
    this.flusher = new Thread(() -> {
      while (alive.get()) {
        try {
          Thread.sleep(0, 800000);
          caravanDataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
          return;
        }
      }
    });
    flusher.start();

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        System.err.println("[caravan shutting down: started]");
        alive.set(false);
        flusher.join();
        caravanDataService.shutdown().await(2500, TimeUnit.MILLISECONDS);
        caravanExecutor.shutdown().await(2500, TimeUnit.MILLISECONDS);
        managedExecutor.shutdown();
        System.err.println("[caravan shutting down: clean]");
      }
    })));
  }
}

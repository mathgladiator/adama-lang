/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.services;

import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.events.FinderServiceToKeyToIdService;
import org.adamalang.cli.Config;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.ManagedDataService;
import org.adamalang.runtime.data.managed.Base;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class CaravanInit {
  private final SimpleExecutor caravanExecutor;
  private final SimpleExecutor managedExecutor;
  public final ManagedDataService service;
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
    CaravanDataService caravanDataService = new CaravanDataService(new CaravanMetrics(init.metricsFactory), init.s3, new FinderServiceToKeyToIdService(init.finder), store, caravanExecutor);
    Base managedBase = new Base(init.finder, caravanDataService, init.s3, init.region, init.machine, managedExecutor, 2 * 60 * 1000);
    this.service = new ManagedDataService(managedBase);
    this.flusher = new Thread(new Runnable() {
      @Override
      public void run() {
        while (init.alive.get()) {
          try {
            Thread.sleep(0, 800000);
            caravanDataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
          } catch (InterruptedException ie) {
            return;
          }
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

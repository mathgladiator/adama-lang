/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.common.Callback;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.remote.MetricsReporter;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DevCoreServiceFactory {
  private static final Logger LOG = LoggerFactory.getLogger(DevCoreServiceFactory.class);
  private final AtomicBoolean alive;
  private final SimpleExecutor caravanExecutor;
  private final Thread flusher;
  public final CaravanDataService dataService;
  public final DeploymentFactoryBase base;
  public final CoreService service;

  public DevCoreServiceFactory(TerminalIO io, AtomicBoolean alive, File caravanPath, File cloudPath, MetricsFactory metricsFactory) throws Exception {
    this.alive = alive;
    this.caravanExecutor = SimpleExecutor.create("caravan");
    File walRoot = new File(caravanPath, "wal");
    File dataRoot = new File(caravanPath, "data");
    File storePath = new File(dataRoot, "store");
    walRoot.mkdir();
    dataRoot.mkdir();
    io.info("caravan|loading store");
    DurableListStore store = new DurableListStore(new DiskMetrics(metricsFactory), storePath, walRoot, 4L * 1024 * 1024 * 1024, 16 * 1024 * 1024, 64 * 1024 * 1024);
    int count = 0;
    for (Map.Entry<Key, Integer> entry : store.map().entrySet()) {
      io.info("caravan|has '" + entry.getKey().space + "/" + entry.getKey().key + "` at " + entry.getValue());
      count++;
    }
    io.info("caravan|stored loaded " + count + " document(s)");
    Cloud cloud = new Cloud() {
      @Override
      public File path() {
        return cloudPath;
      }

      @Override
      public void exists(Key key, String archiveKey, Callback<Void> callback) {

      }

      @Override
      public void restore(Key key, String archiveKey, Callback<File> callback) {

      }

      @Override
      public void backup(Key key, File archiveFile, Callback<Void> callback) {

      }

      @Override
      public void delete(Key key, String archiveKey, Callback<Void> callback) {

      }
    };
    dataService = new CaravanDataService(new CaravanMetrics(metricsFactory), cloud, store, caravanExecutor);
    this.flusher = new Thread(() -> {
      while (alive.get()) {
        try {
          Thread.sleep(0, 800000);
          dataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
          LOG.error("caravan-flushing", ie);
          io.error("caravan|flushing stopped");
          return;
        }
      }
    });
    flusher.start();
    this.base = new DeploymentFactoryBase();
    this.service = new CoreService(new CoreMetrics(metricsFactory), base, (samples) -> {
    }, new MetricsReporter() {
      @Override
      public void emitMetrics(Key key, String metricsPayload) {
        io.info("metrics:" + metricsPayload);
      }
    }, dataService, TimeSource.REAL_TIME, 2);
    base.attachDeliverer(service);
  }

  public void shutdown() throws Exception {
    alive.set(false);
    flusher.join();
    dataService.shutdown().await(1000, TimeUnit.MILLISECONDS);
    caravanExecutor.shutdown().await(1000, TimeUnit.MILLISECONDS);
  }
}

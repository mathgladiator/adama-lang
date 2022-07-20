/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.canary.agents.local;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DurableListStoreMetrics;
import org.adamalang.caravan.events.FinderServiceToKeyToIdService;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalDrive {
  public static void go(LocalCanaryConfig config) throws Exception {
    ExceptionLogger logger = new ExceptionLogger() {
      @Override
      public void convertedToErrorCode(Throwable t, int errorCode) {
        System.exit(100);
      }
    };
    DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();

    String singleScript = Files.readString(new File(config.source).toPath());
    ObjectNode planNode = Json.newJsonObject();
    planNode.putObject("versions").put("file", singleScript);
    planNode.put("default", "file");
    planNode.putArray("plan");
    DeploymentPlan plan = new DeploymentPlan(planNode.toString(), logger);
    deploymentFactoryBase.deploy(config.space, plan);
    final DataService dataService;
    if (config.data.equals("caravan")) {
      SimpleExecutor executor = SimpleExecutor.create("wal");
      File storageDirectory = new File("./canary_caravan");
      storageDirectory.mkdirs();
      DurableListStore dls = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(storageDirectory, "STORE"), storageDirectory, 1800 * 1024 * 1024, 32768, 1024 * 1024 * 1024);
      FinderService finder = new FinderService() {
        @Override
        public void find(Key key, Callback<Result> callback) {
          callback.success(new Result(key.hashCode(), null, "region", null, null));
        }

        @Override
        public void bind(Key key, String machine, Callback<Void> callback) {

        }

        @Override
        public void free(Key key, String machineOn, Callback<Void> callback) {

        }

        @Override
        public void backup(Key key, BackupResult result, String machineOn, Callback<Void> callback) {

        }

        @Override
        public void delete(Key key, String machineOn, Callback<Void> callback) {
        }

        @Override
        public void list(String machine, Callback<List<Key>> callback) {

        }
      };
      Cloud cloud = new Cloud() {
        @Override
        public File path() {
          return null;
        }

        @Override
        public void restore(Key key, String archiveKey, Callback<File> callback) {

        }

        @Override
        public void backup(Key key, File archiveFile, Callback<Void> callback) {

        }

        @Override
        public void delete(Key key, String archiveKey) {
        }
      };
      CaravanDataService service = new CaravanDataService(cloud, new FinderServiceToKeyToIdService(finder), dls, executor);
      dataService = service;
      Thread flusher = new Thread(new Runnable() {
        @Override
        public void run() {
          long last = System.nanoTime();
          while(true) {
            try {
              long now = System.nanoTime();
              int delta = (int) (now - last);
              Thread.sleep(0, delta >= 1000000 ? Math.max(250000, 1750000 - delta) : 750000);
              service.flush(false).await(1, TimeUnit.MILLISECONDS);
              last = now;
            } catch (InterruptedException ie) {
              return;
            }
          }
        }
      });
      flusher.start();
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          flusher.interrupt();
        }
      }));

    } else {
      dataService = new InMemoryDataService(Executors.newSingleThreadExecutor(), TimeSource.REAL_TIME);
    }
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    CoreMetrics coreMetrics = new CoreMetrics(new NoOpMetricsFactory());
    CoreService service = new CoreService(coreMetrics, deploymentFactoryBase, meteringPubSub.publisher(), dataService, TimeSource.REAL_TIME, config.coreThreads);
    LocalAgent[] agents = new LocalAgent[config.agents];
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    for (int k = 0; k < agents.length; k++) {
      agents[k] = new LocalAgent(service, config, k, executor);
    }
    for (int k = 0; k < agents.length; k++) {
      agents[k].kickOff();
    }
    config.blockUntilQuit();
  }
}

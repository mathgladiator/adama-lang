/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary.agents.local;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.disk.demo.DiskMetrics;
import org.adamalang.disk.demo.SingleThreadDiskDataService;
import org.adamalang.runtime.data.DataService;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.MeteringPubSub;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    if (config.data.equals("disk")) {
      dataService = new SingleThreadDiskDataService(new File("./canary_data"), new DiskMetrics(new NoOpMetricsFactory()));
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

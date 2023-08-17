/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.ops;

import org.adamalang.common.*;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Capacity;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.net.server.LocalCapacityRequestor;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** agent of deployments */
public class DeploymentAgent implements LocalCapacityRequestor, DeploymentMonitor, ExceptionLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentAgent.class);
  private final SimpleExecutor executor;
  public final DataBase database;
  public final DeploymentMetrics metrics;
  public final String region;
  public final String machine;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final CoreService service;

  public DeploymentAgent(SimpleExecutor executor, DataBase database, DeploymentMetrics metrics, String region, String machine, DeploymentFactoryBase deploymentFactoryBase, CoreService service) {
    this.executor = executor;
    this.database = database;
    this.metrics = metrics;
    this.region = region;
    this.machine = machine;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.service = service;
  }

  public void optimisticScanAll() throws Exception {
    metrics.deploy_sweep.run();
    for (CapacityInstance instance : Capacity.listAllOnMachine(database, region, machine)) {
      requestCodeDeployment(instance.space, Callback.DONT_CARE_VOID);
    }
  }

  public void bind(String space) {
    executor.execute(new NamedRunnable("bind-space") {
      @Override
      public void execute() throws Exception {
        try {
          metrics.deploy_bind.run();
          Capacity.add(database, space, region, machine);
        } catch (Exception ex) {
          LOGGER.error("failed-bind-space", ex);
        }
      }
    });
  }

  @Override
  public void requestCodeDeployment(String space, Callback<Void> callback) {
    try {
      SpaceInfo info = Spaces.getSpaceInfo(database, space);
      String plan = Spaces.getPlan(database, info.id);
      if ("".equals(plan)) {
        metrics.deploy_empty.run();
        callback.failure(new ErrorCodeException(-1));
        return;
      }
      metrics.deploy_started.run();
      deploymentFactoryBase.deploy(space, new DeploymentPlan(plan, this));
      service.deploy(this);
      callback.success(null);
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        ErrorCodeException ece = (ErrorCodeException) ex;
        if (ece.code == 115788 || ece.code == 117823) {
          metrics.deploy_bad_plan.run();
          return;
        }
      } else {
        LOGGER.error("failed-scan-" + space, ex);
      }
      callback.failure(ErrorCodeException.detectOrWrap(-1, ex, this));
    }
  }

  @Override
  public void bumpDocument(boolean changed) {
    if (changed) {
      metrics.deploy_document_upgraded.run();
    }
  }

  @Override
  public void witnessException(ErrorCodeException ex) {
    metrics.deploy_witness_exception.run();
  }

  @Override
  public void finished(int ms) {
    metrics.deploy_finished.run();
  }

  @Override
  public void convertedToErrorCode(Throwable t, int errorCode) {
    metrics.deploy_feedback.run();
  }
}

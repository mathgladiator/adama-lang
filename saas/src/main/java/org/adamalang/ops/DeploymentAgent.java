/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.ops;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.Deployment;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Consumer;

/** agent of deployments */
public class DeploymentAgent implements Consumer<String>, DeploymentMonitor, ExceptionLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentAgent.class);
  public final DataBase database;
  public final DeploymentMetrics metrics;
  public final String machine;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final CoreService service;

  public DeploymentAgent(DataBase database, DeploymentMetrics metrics, String machine, DeploymentFactoryBase deploymentFactoryBase, CoreService service) {
    this.database = database;
    this.metrics = metrics;
    this.machine = machine;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.service = service;
  }

  public void deploy(Deployment deployment) {
    if ("".equals(deployment.plan)) {
      metrics.deploy_empty.run();
      return;
    }
    try {
      metrics.deploy_started.run();
      deploymentFactoryBase.deploy(deployment.space, new DeploymentPlan(deployment.plan, this));
      service.deploy(this);
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        ErrorCodeException ece = (ErrorCodeException) ex;
        if (ece.code == 115788 || ece.code == 117823) {
          metrics.deploy_bad_plan.run();
          return;
        }
      }
      LOGGER.error("failed-scan-" + deployment.space, ex);
    }
  }

  @Override
  public void accept(String space) {
    try {
      if ("*".equals(space)) {
        metrics.deploy_sweep.run();
        ArrayList<Deployment> deployments = Deployments.listSpacesOnTarget(database, machine);
        for (Deployment deployment : deployments) {
          deploy(deployment);
        }
      } else {
        Deployment deployment = Deployments.get(database, machine, space);
        deploy(deployment);
      }
    } catch (Exception ex) {
      metrics.deploy_hardfail.run();
      LOGGER.error("failed-deployment-" + space, ex);
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

package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** encode the callback to trigger a deployment */
public class TriggerDeployment implements Callback<Void>, DeploymentMonitor {
  private static Logger LOG = LoggerFactory.getLogger(TriggerDeployment.class);
  private final CoreService service;
  private final RequestResponseMonitor.RequestResponseMonitorInstance instance;
  private int errors;

  public TriggerDeployment(CoreService service) {
    this.service = service;
    instance = service.metrics.deployment.start();
    this.errors = 0;
  }

  @Override
  public void success(Void value) {
    this.service.deploy(this);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    witnessException(ex);
  }

  @Override
  public void bumpDocument(boolean changed) {
    service.metrics.trigger_deployment.run();
  }

  @Override
  public synchronized void witnessException(ErrorCodeException ex) {
    LOG.error("witness-exception-deployment: {}", ex.code);
    if (errors == 0) {
      instance.failure(ex.code);
    }
    errors++;
  }

  @Override
  public synchronized void finished(int ms) {
    if (errors == 0) {
      instance.success();
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** encode the callback to trigger a deployment */
public class TriggerDeployment implements Callback<Void>, DeploymentMonitor {
  private static Logger LOG = LoggerFactory.getLogger(TriggerDeployment.class);
  private final CoreService service;
  private final Callback<Void> other;

  public TriggerDeployment(CoreService service, Callback<Void> other) {
    this.service = service;
    this.other = other;
  }

  @Override
  public void success(Void value) {
    this.service.deploy(this);
    this.other.success(value);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    witnessException(ex);
    other.failure(ex);
  }

  @Override
  public void bumpDocument(boolean changed) {
    if (changed) {
      service.metrics.trigger_deployment.run();
    }
  }

  @Override
  public void witnessException(ErrorCodeException ex) {
    LOG.error("witness-exception-deployment: {}", ex.code);
  }

  @Override
  public void finished(int ms) {
  }
}

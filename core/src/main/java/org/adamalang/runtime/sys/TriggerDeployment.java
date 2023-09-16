/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

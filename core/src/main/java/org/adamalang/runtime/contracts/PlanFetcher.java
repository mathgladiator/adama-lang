package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.deploy.DeploymentPlan;

/** fetch a plan */
public interface PlanFetcher {
  public void find(String space, Callback<DeploymentPlan> callback);
}

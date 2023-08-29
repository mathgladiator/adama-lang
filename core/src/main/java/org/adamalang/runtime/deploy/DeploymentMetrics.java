/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the deployment agent */
public class DeploymentMetrics {
  public final Runnable deploy_cache_hit;
  public final Runnable deploy_cache_miss;
  public final CallbackMonitor deploy_plan_fetch;
  public final CallbackMonitor deploy_plan_push;
  public final Runnable deploy_undo;

  public DeploymentMetrics(MetricsFactory factory) {
    this.deploy_cache_hit = factory.counter("deploy_cache_hit");
    this.deploy_cache_miss = factory.counter("deploy_cache_miss");
    this.deploy_plan_fetch = factory.makeCallbackMonitor("deploy_plan_fetch");
    this.deploy_plan_push = factory.makeCallbackMonitor("deploy_plan_push");
    this.deploy_undo = factory.counter("deploy_undo");
  }
}

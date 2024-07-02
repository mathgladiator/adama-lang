/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

  public final Runnable deploy_bytecode_found;
  public final Runnable deploy_bytecode_compiled;
  public final Runnable deploy_bytecode_stored;
  public final Runnable deploy_bytecode_compile_failed;

  public DeploymentMetrics(MetricsFactory factory) {
    this.deploy_cache_hit = factory.counter("deploy_cache_hit");
    this.deploy_cache_miss = factory.counter("deploy_cache_miss");
    this.deploy_plan_fetch = factory.makeCallbackMonitor("deploy_plan_fetch");
    this.deploy_plan_push = factory.makeCallbackMonitor("deploy_plan_push");
    this.deploy_undo = factory.counter("deploy_undo");
    this.deploy_bytecode_found = factory.counter("deploy_bytecode_found");
    this.deploy_bytecode_compiled = factory.counter("deploy_bytecode_compiled");
    this.deploy_bytecode_stored = factory.counter("deploy_bytecode_stored");
    this.deploy_bytecode_compile_failed = factory.counter("deploy_bytecode_compile_failed");
  }
}

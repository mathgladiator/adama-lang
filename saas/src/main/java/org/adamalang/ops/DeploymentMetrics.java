/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.ops;

import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the deployment agent */
public class DeploymentMetrics {
  public final Runnable deploy_empty;
  public final Runnable deploy_started;
  public final Runnable deploy_feedback;
  public final Runnable deploy_document_upgraded;
  public final Runnable deploy_witness_exception;
  public final Runnable deploy_sweep;
  public final Runnable deploy_hardfail;
  public final Runnable deploy_bad_plan;
  public final Runnable deploy_finished;
  public final Runnable deploy_bind;

  public DeploymentMetrics(MetricsFactory factory) {
    this.deploy_empty = factory.counter("deploy_empty");
    this.deploy_started = factory.counter("deploy_started");
    this.deploy_feedback = factory.counter("deploy_feedback");
    this.deploy_document_upgraded = factory.counter("deploy_document_upgraded");
    this.deploy_witness_exception = factory.counter("deploy_witness_exception");
    this.deploy_sweep = factory.counter("deploy_sweep");
    this.deploy_hardfail = factory.counter("deploy_hardfail");
    this.deploy_bad_plan = factory.counter("deploy_bad_plan");
    this.deploy_finished = factory.counter("deploy_finished");
    this.deploy_bind = factory.counter("deploy_bind");
  }
}

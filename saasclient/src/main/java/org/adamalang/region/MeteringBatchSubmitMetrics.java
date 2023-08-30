/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for converting a metering batch to a message to an Adama document */
public class MeteringBatchSubmitMetrics {

  public final CallbackMonitor metering_batch_submit_find;
  public final CallbackMonitor metering_batch_submit_send;
  public final Runnable metering_batch_happy;
  public final Runnable metering_batch_lost;
  public final Runnable metering_batch_exception;

  public MeteringBatchSubmitMetrics(MetricsFactory factory) {
    this.metering_batch_submit_find = factory.makeCallbackMonitor("metering_batch_submit_find");
    this.metering_batch_submit_send = factory.makeCallbackMonitor("metering_batch_submit_send");
    this.metering_batch_lost = factory.counter("metering_batch_lost");
    this.metering_batch_happy = factory.counter("metering_batch_happy");
    this.metering_batch_exception = factory.counter("metering_batch_exception");
  }
}

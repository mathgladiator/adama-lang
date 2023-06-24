/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model.metrics;

import org.adamalang.common.metrics.MetricsFactory;

public class MeteringMetrics {
  public final Runnable metering_batch_found;
  public final Runnable metering_batch_late;
  public final Runnable metering_batch_just_right;
  public final Runnable metering_batch_early;

  public MeteringMetrics(MetricsFactory factory) {
    metering_batch_found = factory.counter("metering_batch_found");
    metering_batch_late = factory.counter("metering_batch_late");
    metering_batch_just_right = factory.counter("metering_batch_just_right");
    metering_batch_early = factory.counter("metering_batch_early");
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class CaravanMetrics {
  public Runnable caravan_waste;
  public Runnable caravan_seq_off;
  public Inflight caravan_datalog_loss;

  public CaravanMetrics(MetricsFactory factory) {
    this.caravan_waste = factory.counter("caravan_waste");
    this.caravan_seq_off = factory.counter("caravan_seq_off");
    this.caravan_datalog_loss = factory.inflight("alarm_caravan_datalog_loss");
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.ops;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the capacity agent */
public class CapacityMetrics {
  public final Inflight shield_active_new_documents;
  public final Inflight shield_active_existing_connections;
  public final Inflight shield_active_messages;

  public final Inflight shield_count_hosts;
  public final Inflight shield_count_metering;
  public final Runnable shield_heat;

  public CapacityMetrics(MetricsFactory factory) {
    this.shield_active_new_documents = factory.inflight("alarm_shield_active_new_documents");
    this.shield_active_existing_connections = factory.inflight("alarm_shield_active_existing_connections");
    this.shield_active_messages = factory.inflight("alarm_shield_active_messages");
    this.shield_count_hosts = factory.inflight("shield_count_hosts");
    this.shield_count_metering = factory.inflight("shield_count_metering");
    this.shield_heat = factory.counter("shield_heat");
  }
}

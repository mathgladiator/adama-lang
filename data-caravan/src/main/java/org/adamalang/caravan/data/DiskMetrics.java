/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.data;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class DiskMetrics {
  public final Runnable flush;
  public final Inflight total_storage_allocated;
  public final Inflight free_storage_available;
  public final Inflight alarm_storage_over_80_percent;
  public final Inflight active_entries;
  public final Inflight items_over_tenk;
  public final Inflight items_over_twentyk;
  public final Inflight items_over_fiftyk;
  public final Inflight items_over_onehundredk;
  public final Runnable items_trimmed;
  public final Runnable appends;
  public final Runnable failed_append;
  public final Runnable reads;

  public DiskMetrics(MetricsFactory factory) {
    this.flush = factory.counter("disk_flush");
    this.total_storage_allocated = factory.inflight("disk_total_storage_allocated_mb");
    this.free_storage_available = factory.inflight("disk_free_storage_available_mb");
    this.alarm_storage_over_80_percent = factory.inflight("alarm_storage_over_80_percent");
    this.active_entries = factory.inflight("disk_active_entries");
    this.items_over_tenk = factory.inflight("storage_items_over_tenk");
    this.items_over_twentyk = factory.inflight("storage_items_over_twentyk");
    this.items_over_fiftyk = factory.inflight("storage_items_over_fiftyk");
    this.items_over_onehundredk = factory.inflight("alarm_storage_items_over_onehundredk");
    this.items_trimmed = factory.counter("storage_items_trimmed");
    this.failed_append = factory.counter("alarm_failed_append");
    this.appends = factory.counter("storage_appends");
    this.reads = factory.counter("storage_reads");
  }
}

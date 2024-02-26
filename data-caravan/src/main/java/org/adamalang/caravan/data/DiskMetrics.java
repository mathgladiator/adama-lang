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
package org.adamalang.caravan.data;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class DiskMetrics {
  public final Runnable flush;
  public final Inflight total_storage_allocated;
  public final Inflight free_storage_available;
  public final Inflight alarm_storage_over_80_percent;
  public final Inflight active_entries;
  public final Inflight items_total;
  public final Inflight items_over_tenk;
  public final Inflight items_over_twentyk;
  public final Inflight items_over_fiftyk;
  public final Inflight items_over_onehundredk;
  public final Inflight items_over_onemega;
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
    this.items_total = factory.inflight("storage_items_total");
    this.items_over_tenk = factory.inflight("storage_items_over_tenk");
    this.items_over_twentyk = factory.inflight("storage_items_over_twentyk");
    this.items_over_fiftyk = factory.inflight("storage_items_over_fiftyk");
    this.items_over_onehundredk = factory.inflight("storage_items_over_onehundredk");
    this.items_over_onemega = factory.inflight("storage_items_over_onemega");
    this.items_trimmed = factory.counter("storage_items_trimmed");
    this.failed_append = factory.counter("alarm_failed_append");
    this.appends = factory.counter("storage_appends");
    this.reads = factory.counter("storage_reads");
  }
}

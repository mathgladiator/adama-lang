/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.mysql.model.metrics.MeteringMetrics;

public class OverlordMetrics {
  public final Runnable targets_watcher_fired;
  public final Runnable targets_made;
  public final Runnable targets_skipped;

  public final Runnable reconcile_start;
  public final Runnable reconcile_stability_check_init;
  public final Runnable reconcile_stability_check_success;
  public final Runnable reconcile_stability_check_failed;

  public final Runnable reconcile_abort_listing;
  public final Runnable reconcile_begin_listing;
  public final Runnable reconcile_consider_target;
  public final Runnable reconcile_evict_target;
  public final Runnable reconcile_end_listing;
  public final Runnable reconcile_failed_listing;

  public final Runnable capacity_monitor_sweep;
  public final Runnable capacity_monitor_sweep_failed;
  public final Runnable capacity_monitor_sweep_finished;
  public final Runnable capacity_monitor_queue_space;
  public final Runnable capacity_monitor_dequeue_space;
  public final Runnable capacity_monitor_failed_space;
  public final Runnable capacity_monitor_retry_space;

  public final Runnable capacity_monitor_found_inconsistent_deployment;
  public final Runnable capacity_monitor_fixed_inconsistent_deployment;
  public final Runnable capacity_monitor_found_weak_space;

  public final Runnable metering_fetch_found;
  public final Runnable metering_fetch_saved;
  public final Runnable metering_fetch_empty;
  public final Runnable metering_fetch_failed;
  public final Runnable metering_fetch_finished;

  public final Runnable gossip_dump;

  public final Runnable accountant_task;

  public final MeteringMetrics metering_metrics;

  public final Runnable delete_bot_wake;
  public final Runnable delete_bot_found;
  public final CallbackMonitor delete_bot_delete_document;
  public final CallbackMonitor delete_bot_delete_ide;
  public final Runnable delete_bot_delete_space;
  public final Inflight sentinel_behind;

  public final Inflight garbage_collector_behind;
  public final Runnable garbage_collector_found_task;

  public OverlordMetrics(MetricsFactory factory) {
    targets_watcher_fired = factory.counter("overlord_targets_watcher_fired");
    targets_made = factory.counter("overlord_targets_made");
    targets_skipped = factory.counter("overlord_targets_skipped");

    reconcile_start = factory.counter("overlord_reconcile_start");
    reconcile_stability_check_init = factory.counter("overlord_reconcile_stability_check_init");
    reconcile_stability_check_success = factory.counter("overlord_reconcile_stability_check_success");
    reconcile_stability_check_failed = factory.counter("overlord_reconcile_stability_check_failed");
    reconcile_abort_listing = factory.counter("overlord_reconcile_abort_listing");
    reconcile_begin_listing = factory.counter("overlord_reconcile_begin_listing");
    reconcile_consider_target = factory.counter("overlord_reconcile_consider_target");
    reconcile_evict_target = factory.counter("overlord_reconcile_evict_target");
    reconcile_end_listing = factory.counter("overlord_reconcile_end_listing");
    reconcile_failed_listing = factory.counter("overlord_reconcile_failed_listing");

    capacity_monitor_sweep = factory.counter("capacity_monitor_sweep");
    capacity_monitor_sweep_failed = factory.counter("capacity_monitor_sweep_failed");
    capacity_monitor_sweep_finished = factory.counter("capacity_monitor_sweep_finished");
    capacity_monitor_queue_space = factory.counter("capacity_monitor_queue_space");
    capacity_monitor_dequeue_space = factory.counter("capacity_monitor_dequeue_space");
    capacity_monitor_retry_space = factory.counter("capacity_monitor_retry_space");
    capacity_monitor_failed_space = factory.counter("capacity_monitor_failed_space");
    capacity_monitor_found_inconsistent_deployment = factory.counter("capacity_monitor_found_inconsistent_deployment");
    capacity_monitor_fixed_inconsistent_deployment = factory.counter("capacity_monitor_fixed_inconsistent_deployment");
    capacity_monitor_found_weak_space = factory.counter("capacity_monitor_found_weak_space");

    metering_fetch_found = factory.counter("metering_fetch_found");
    metering_fetch_saved = factory.counter("metering_fetch_saved");
    metering_fetch_empty = factory.counter("metering_fetch_empty");
    metering_fetch_failed = factory.counter("metering_fetch_failed");
    metering_fetch_finished = factory.counter("metering_fetch_finished");

    gossip_dump = factory.counter("gossip_dump");

    accountant_task = factory.counter("accountant_task");

    metering_metrics = new MeteringMetrics(factory);

    delete_bot_wake = factory.counter("delete_bot_wake");
    delete_bot_found = factory.counter("delete_bot_found");
    delete_bot_delete_document = factory.makeCallbackMonitor("delete_bot_delete_document");
    delete_bot_delete_ide = factory.makeCallbackMonitor("delete_bot_delete_ide");
    delete_bot_delete_space = factory.counter("delete_bot_delete_space");
    sentinel_behind = factory.inflight("alarm_sentinel_behind");
    garbage_collector_behind = factory.inflight("garbage_collector_behind");
    garbage_collector_found_task = factory.counter("garbage_collector_found_task");
  }
}

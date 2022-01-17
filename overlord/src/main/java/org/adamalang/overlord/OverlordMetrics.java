/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord;

import org.adamalang.common.metrics.MetricsFactory;

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

  public final Runnable billing_fetch_found;
  public final Runnable billing_fetch_saved;
  public final Runnable billing_fetch_failed;
  public final Runnable billing_fetch_finished;

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

    billing_fetch_found = factory.counter("billing_fetch_found");
    billing_fetch_saved = factory.counter("billing_fetch_saved");
    billing_fetch_failed = factory.counter("billing_fetch_failed");
    billing_fetch_finished = factory.counter("billing_fetch_finished");

  }
}

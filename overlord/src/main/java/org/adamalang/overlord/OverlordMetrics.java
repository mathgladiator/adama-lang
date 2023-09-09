/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
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

  public final Runnable gossip_dump;

  public final Runnable delete_bot_wake;
  public final Runnable delete_bot_found;
  public final CallbackMonitor delete_bot_delete_document;
  public final CallbackMonitor delete_bot_delete_ide;
  public final Runnable delete_bot_delete_space;
  public final Inflight sentinel_behind;

  public final Inflight garbage_collector_behind;
  public final Runnable garbage_collector_found_task;
  public final Inflight targets_scan_zero_backend;

  public final Inflight found_missing_document;

  public final Inflight users;
  public final CallbackMonitor storage_record_sent;
  public final CallbackMonitor system_usage_record_sent;

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

    gossip_dump = factory.counter("gossip_dump");

    delete_bot_wake = factory.counter("delete_bot_wake");
    delete_bot_found = factory.counter("delete_bot_found");
    delete_bot_delete_document = factory.makeCallbackMonitor("delete_bot_delete_document");
    delete_bot_delete_ide = factory.makeCallbackMonitor("delete_bot_delete_ide");
    delete_bot_delete_space = factory.counter("delete_bot_delete_space");
    sentinel_behind = factory.inflight("alarm_sentinel_behind");
    garbage_collector_behind = factory.inflight("garbage_collector_behind");
    garbage_collector_found_task = factory.counter("garbage_collector_found_task");
    found_missing_document = factory.inflight("alarm_missing_document");
    targets_scan_zero_backend = factory.inflight("alertm_targets_scan_zero_backend");

    users = factory.inflight("current_users");
    storage_record_sent = factory.makeCallbackMonitor("storage_record_sent");
    system_usage_record_sent = factory.makeCallbackMonitor("system_usage_record_sent");
  }
}

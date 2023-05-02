/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class AWSMetrics {
  public final RequestResponseMonitor restore_document;
  public final RequestResponseMonitor backup_document;
  public final RequestResponseMonitor delete_document;
  public final RequestResponseMonitor list_assets;
  public final RequestResponseMonitor well_known_get;
  public final RequestResponseMonitor well_known_put;
  public final RequestResponseMonitor upload_file;
  public final RequestResponseMonitor download_file;
  public final RequestResponseMonitor send_email;
  public final Inflight alarm_send_failures;
  public final RequestResponseMonitor upload_log_document;
  public final RequestResponseMonitor delete_asset;
  public final RequestResponseMonitor enqueue;
  public final CallbackMonitor signal_control_domain;

  public AWSMetrics(MetricsFactory factory) {
    restore_document = factory.makeRequestResponseMonitor("aws_restore_document");
    backup_document = factory.makeRequestResponseMonitor("aws_backup_document");
    delete_document = factory.makeRequestResponseMonitor("aws_delete_document");
    well_known_get = factory.makeRequestResponseMonitor("aws_well_known_get");
    well_known_put = factory.makeRequestResponseMonitor("aws_well_known_put");
    list_assets = factory.makeRequestResponseMonitor("aws_list_assets");
    upload_file = factory.makeRequestResponseMonitor("aws_upload_file");
    download_file = factory.makeRequestResponseMonitor("aws_download_file");
    delete_asset = factory.makeRequestResponseMonitor("aws_delete_asset");
    send_email = factory.makeRequestResponseMonitor("aws_send_email");
    alarm_send_failures = factory.inflight("alarm_send_failures");
    upload_log_document = factory.makeRequestResponseMonitor("aws_upload_log_document");
    enqueue = factory.makeRequestResponseMonitor("aws_enqueue");
    signal_control_domain = factory.makeCallbackMonitor("aws_signal_control_domain");
  }
}

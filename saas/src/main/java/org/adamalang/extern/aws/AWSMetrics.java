/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class AWSMetrics {
  public final RequestResponseMonitor restore_document;
  public final RequestResponseMonitor backup_document;
  public final RequestResponseMonitor delete_document;
  public final RequestResponseMonitor well_known_get;
  public final RequestResponseMonitor well_known_put;

  public final RequestResponseMonitor upload_file;
  public final RequestResponseMonitor download_file;
  public final RequestResponseMonitor send_email;
  public final Inflight alarm_send_failures;
  public final RequestResponseMonitor upload_log_document;
  public final RequestResponseMonitor delete_asset;

  public AWSMetrics(MetricsFactory factory) {
    restore_document = factory.makeRequestResponseMonitor("aws_restore_document");
    backup_document = factory.makeRequestResponseMonitor("aws_backup_document");
    delete_document = factory.makeRequestResponseMonitor("aws_delete_document");
    well_known_get = factory.makeRequestResponseMonitor("aws_well_known_get");
    well_known_put = factory.makeRequestResponseMonitor("aws_well_known_put");

    upload_file = factory.makeRequestResponseMonitor("aws_upload_file");
    download_file = factory.makeRequestResponseMonitor("aws_download_file");
    delete_asset = factory.makeRequestResponseMonitor("aws_delete_asset");
    send_email = factory.makeRequestResponseMonitor("aws_send_email");
    alarm_send_failures = factory.inflight("alarm_send_failures");

    upload_log_document = factory.makeRequestResponseMonitor("aws_upload_log_document");
  }
}

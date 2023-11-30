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
package org.adamalang.extern.aws;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

/** metrics for AWS */
public class AWSMetrics {
  public final RequestResponseMonitor restore_document;
  public final RequestResponseMonitor exists_document;
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
  public final RequestResponseMonitor fetch_byte_code;
  public final RequestResponseMonitor store_byte_code;
  public final RequestResponseMonitor enqueue;
  public final CallbackMonitor signal_control_domain;
  public final Inflight alarm_file_not_found;

  public AWSMetrics(MetricsFactory factory) {
    restore_document = factory.makeRequestResponseMonitor("aws_restore_document");
    exists_document = factory.makeRequestResponseMonitor("aws_exists_document");
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
    alarm_file_not_found = factory.inflight("alarm_file_not_found");
    fetch_byte_code = factory.makeRequestResponseMonitor("aws_fetch_byte_code");
    store_byte_code = factory.makeRequestResponseMonitor("aws_store_byte_code");
  }
}

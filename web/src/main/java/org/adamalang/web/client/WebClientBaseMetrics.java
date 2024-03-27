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
package org.adamalang.web.client;

import org.adamalang.common.Callback;
import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class WebClientBaseMetrics {
  public final Inflight alarm_web_client_null_responder;
  public final Runnable web_client_instant_fail;
  public final Runnable web_client_200_or_204;
  public final Runnable web_client_403;
  public final Runnable web_client_404;
  public final Runnable web_client_410;
  public final Runnable web_client_400;
  public final Runnable web_client_500_plus;
  public final Runnable web_client_code_unknown;
  public final Runnable web_client_request_start;
  public final Runnable web_client_request_sent_small_full;
  public final Runnable web_client_request_send_large_started;
  public final Runnable web_client_request_send_large_finished;
  public final Runnable web_client_request_failed_send;
  public final Inflight inflight_web_requests;
  public final CallbackMonitor web_execute_find_pool_item;
  public final CallbackMonitor web_create_shared;

  public WebClientBaseMetrics(MetricsFactory factory) {
    this.alarm_web_client_null_responder = factory.inflight("alarm_web_client_null_responder");
    this.web_client_instant_fail = factory.counter("web_client_instant_fail");
    this.web_create_shared = factory.makeCallbackMonitor("web_create_shared");
    this.web_client_200_or_204 = factory.counter("web_client_200_or_204");
    this.web_client_400 = factory.counter("web_client_400");
    this.web_client_403 = factory.counter("web_client_403");
    this.web_client_404 = factory.counter("web_client_404");
    this.web_client_410 = factory.counter("web_client_410");
    this.web_client_500_plus = factory.counter("web_client_500_plus");
    this.web_client_code_unknown = factory.counter("web_client_code_unknown");
    this.web_client_request_start = factory.counter("web_client_request_start");
    this.web_client_request_sent_small_full = factory.counter("web_client_request_sent_small_full");
    this.web_client_request_send_large_started = factory.counter("web_client_request_send_large_started");
    this.web_client_request_send_large_finished = factory.counter("web_client_request_send_large_finished");
    this.web_client_request_failed_send = factory.counter("web_client_request_failed_send");
    this.inflight_web_requests = factory.inflight("web_inflight_web_requests");
    this.web_execute_find_pool_item = factory.makeCallbackMonitor("web_execute_find_pool_item");
  }
}

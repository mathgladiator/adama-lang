/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class WebClientBaseMetrics {
  public final Inflight alarm_web_client_null_responder;
  public final Runnable web_client_200_or_204;
  public final Runnable web_client_403;
  public final Runnable web_client_404;
  public final Runnable web_client_400;
  public final Runnable web_client_500_plus;
  public final Runnable web_client_code_unknown;

  public final Runnable web_client_request_start;
  public final Runnable web_client_request_sent_small_full;
  public final Runnable web_client_request_send_large_started;
  public final Runnable web_client_request_send_large_finished;
  public final Runnable web_client_request_failed_send;

  public WebClientBaseMetrics(MetricsFactory factory) {
    this.alarm_web_client_null_responder = factory.inflight("alarm_web_client_null_responder");
    this.web_client_200_or_204 = factory.counter("web_client_200_or_204");
    this.web_client_403 = factory.counter("web_client_403");
    this.web_client_404 = factory.counter("web_client_404");
    this.web_client_400 = factory.counter("web_client_400");
    this.web_client_500_plus = factory.counter("web_client_500_plus");
    this.web_client_code_unknown = factory.counter("web_client_code_unknown");

    this.web_client_request_start = factory.counter("web_client_request_start");
    this.web_client_request_sent_small_full = factory.counter("web_client_request_sent_small_full");
    this.web_client_request_send_large_started = factory.counter("web_client_request_send_large_started");
    this.web_client_request_send_large_finished = factory.counter("web_client_request_send_large_finished");
    this.web_client_request_failed_send = factory.counter("web_client_request_failed_send");
  }
}

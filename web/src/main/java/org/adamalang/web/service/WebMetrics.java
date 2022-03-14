/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class WebMetrics {
  public final Inflight websockets_active;
  public final Inflight websockets_active_child_connections;
  public final Runnable websockets_send_heartbeat;
  public final Runnable websockets_heartbeat_failure;
  public final Runnable websockets_server_heartbeat;
  public final Runnable websockets_uncaught_exception;
  public final Runnable websockets_end_exception;
  public final Runnable webhandler_get;
  public final Runnable webhandler_post;
  public final Runnable webhandler_exception;
  public final Runnable webhandler_found;
  public final Runnable webhandler_healthcheck;
  public final Runnable webhandler_client_download;

  public final Runnable websockets_start;
  public final Runnable websockets_end;


  public WebMetrics(MetricsFactory factory) {
    this.websockets_active = factory.inflight("websockets_active");
    this.websockets_active_child_connections = factory.inflight("websockets_active_child_connections");
    this.websockets_send_heartbeat = factory.counter("websockets_send_heartbeat");
    this.websockets_heartbeat_failure = factory.counter("websockets_heartbeat_failure");
    this.websockets_server_heartbeat = factory.counter("websockets_server_heartbeat");
    this.websockets_start = factory.counter("websockets_start");
    this.websockets_end = factory.counter("websockets_end");
    this.websockets_uncaught_exception = factory.counter("websockets_uncaught_exception");
    this.websockets_end_exception = factory.counter("websockets_end_exception");
    this.webhandler_client_download = factory.counter("webhandler_client_download");

    this.webhandler_get = factory.counter("webhandler_get");
    this.webhandler_post = factory.counter("webhandler_post");
    this.webhandler_exception = factory.counter("webhandler_exception");
    this.webhandler_found = factory.counter("webhandler_found");
    this.webhandler_healthcheck = factory.counter("webhandler_healthcheck");
  }
}

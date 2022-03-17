/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.server;

import org.adamalang.common.metrics.*;

public class ServerMetrics {
  public final Inflight server_handlers_active;

  public final CallbackMonitor server_create;
  public final CallbackMonitor server_reflect;
  public final CallbackMonitor server_stream_ask;
  public final CallbackMonitor server_stream_attach;
  public final CallbackMonitor server_stream_send;
  public final Runnable server_stream_update;
  public final Runnable server_stream_disconnect;
  public final StreamMonitor server_stream;

  public final Runnable server_metering_begin;
  public final Runnable server_metering_delete_batch;
  public final Runnable server_scan_deployment;
  public final Runnable server_channel_error;

  public ServerMetrics(MetricsFactory factory) {
    server_handlers_active = factory.inflight("server_handlers_active");
    server_create = factory.makeCallbackMonitor("server_create");
    server_reflect = factory.makeCallbackMonitor("server_reflect");

    server_stream_ask = factory.makeCallbackMonitor("server_stream_ask");
    server_stream_attach = factory.makeCallbackMonitor("server_stream_attach");
    server_stream_send = factory.makeCallbackMonitor("server_stream_send");
    server_stream_update = factory.counter("server_stream_update");
    server_stream_disconnect = factory.counter("server_stream_disconnect");

    server_stream = factory.makeStreamMonitor("server_stream");

    server_metering_begin = factory.counter("server_metering_begin");
    server_metering_delete_batch = factory.counter("server_metering_delete_batch");
    server_scan_deployment = factory.counter("server_scan_deployment");
    server_channel_error = factory.counter("server_channel_error");
  }
}

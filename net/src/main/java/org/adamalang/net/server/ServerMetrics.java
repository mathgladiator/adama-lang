/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.server;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.StreamMonitor;

public class ServerMetrics {
  public final Inflight server_handlers_active;

  public final CallbackMonitor server_create;
  public final CallbackMonitor server_delete;
  public final CallbackMonitor server_reflect;
  public final CallbackMonitor server_stream_ask;
  public final CallbackMonitor server_stream_attach;
  public final CallbackMonitor server_stream_send;
  public final CallbackMonitor server_stream_password;
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
    server_delete = factory.makeCallbackMonitor("server_delete");
    server_reflect = factory.makeCallbackMonitor("server_reflect");

    server_stream_ask = factory.makeCallbackMonitor("server_stream_ask");
    server_stream_attach = factory.makeCallbackMonitor("server_stream_attach");
    server_stream_send = factory.makeCallbackMonitor("server_stream_send");
    server_stream_password = factory.makeCallbackMonitor("server_stream_password");
    server_stream_update = factory.counter("server_stream_update");
    server_stream_disconnect = factory.counter("server_stream_disconnect");

    server_stream = factory.makeStreamMonitor("server_stream");

    server_metering_begin = factory.counter("server_metering_begin");
    server_metering_delete_batch = factory.counter("server_metering_delete_batch");
    server_scan_deployment = factory.counter("server_scan_deployment");
    server_channel_error = factory.counter("server_channel_error");
  }
}

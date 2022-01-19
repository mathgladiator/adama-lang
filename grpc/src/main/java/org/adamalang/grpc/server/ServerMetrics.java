/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.server;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.MetricsFactory;

public class ServerMetrics {
  public final Inflight server_handlers_active;
  public final Runnable server_witness_packet_connect;
  public final CallbackMonitor server_create;
  public final CallbackMonitor server_reflect;
  public final ItemActionMonitor server_stream_ask;
  public final ItemActionMonitor server_stream_attach;
  public final ItemActionMonitor server_stream_send;
  public final ItemActionMonitor server_stream_disconnect;

  public ServerMetrics(MetricsFactory factory) {
    server_handlers_active = factory.inflight("server_handlers_active");
    server_witness_packet_connect = factory.counter("server_witness_packet_connect");

    server_create = factory.makeCallbackMonitor("server_create");
    server_reflect = factory.makeCallbackMonitor("server_reflect");

    server_stream_ask = factory.makeItemActionMonitor("server_stream_ask");
    server_stream_attach = factory.makeItemActionMonitor("server_stream_attach");
    server_stream_send = factory.makeItemActionMonitor("server_stream_send");
    server_stream_disconnect = factory.makeItemActionMonitor("server_stream_disconnect");
  }
}

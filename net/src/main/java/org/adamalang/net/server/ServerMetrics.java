/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
  public final CallbackMonitor server_stream_update;
  public final CallbackMonitor server_stream_password;
  public final Runnable server_stream_disconnect;
  public final Runnable server_observe_disconnect;
  public final StreamMonitor server_stream;
  public final StreamMonitor observe_stream;

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
    server_stream_update = factory.makeCallbackMonitor("server_stream_update");
    server_stream_disconnect = factory.counter("server_stream_disconnect");
    server_observe_disconnect = factory.counter("server_observe_disconnect");

    server_stream = factory.makeStreamMonitor("server_stream");
    observe_stream = factory.makeStreamMonitor("observe_stream");

    server_metering_begin = factory.counter("server_metering_begin");
    server_metering_delete_batch = factory.counter("server_metering_delete_batch");
    server_scan_deployment = factory.counter("server_scan_deployment");
    server_channel_error = factory.counter("server_channel_error");
  }
}

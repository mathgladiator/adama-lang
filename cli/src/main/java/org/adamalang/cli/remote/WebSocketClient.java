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
package org.adamalang.cli.remote;

import org.adamalang.common.ANSI;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Platform;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.WebClientConnection;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** a very simple websocket client for usage within only the CLI (due to blocking nature) */
public class WebSocketClient implements AutoCloseable {
  public final WebClientBase base;
  private final String endpoint;

  public WebSocketClient(Config config) throws Exception {
    this.base = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(config.read())));
    this.endpoint = config.get_string("endpoint", "wss://aws-us-east-2.adama-platform.com/~s");
  }

  @Override
  public void close() throws Exception {
    base.shutdown();
  }

  public Connection open() throws Exception {
    AtomicReference<WebClientConnection> connectionRef = new AtomicReference<>();
    CountDownLatch gotConnectionRef = new CountDownLatch(1);
    base.open(endpoint, new WebLifecycle() {
      @Override
      public void connected(WebClientConnection connection, String version) {
        if (!Platform.VERSION.equals(version)) {
          System.err.println(Util.prefix("Remote platform has a different version of your jar; yours=" + Platform.VERSION + "; remote=" + version, ANSI.Yellow));
        }
        connectionRef.set(connection);
        gotConnectionRef.countDown();
      }

      @Override
      public void ping(int latency) {

      }

      @Override
      public void failure(Throwable t) {

      }

      @Override
      public void disconnected() {

      }
    });
    if (gotConnectionRef.await(30000, TimeUnit.MILLISECONDS)) {
      return new Connection(connectionRef.get());
    } else {
      throw new Exception("Failed to connect");
    }
  }
}

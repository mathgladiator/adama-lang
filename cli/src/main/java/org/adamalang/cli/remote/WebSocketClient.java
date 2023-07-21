/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.remote;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Platform;
import org.adamalang.web.client.WebClientBase;
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
    this.base = new WebClientBase(new WebConfig(new ConfigObject(config.read())));
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
          System.err.println(Util.prefix("Remote platform has a different version of your jar; yours=" + Platform.VERSION + "; remote=" + version, Util.ANSI.Yellow));
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
    if (gotConnectionRef.await(2500, TimeUnit.MILLISECONDS)) {
      return new Connection(connectionRef.get());
    } else {
      throw new Exception("Failed to connect");
    }
  }
}

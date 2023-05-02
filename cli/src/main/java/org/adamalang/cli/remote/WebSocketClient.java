/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.remote;

import org.adamalang.cli.Config;
import org.adamalang.common.ConfigObject;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
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
      public void connected(WebClientConnection connection) {
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

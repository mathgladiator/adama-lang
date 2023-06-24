/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.adamalang.web.service.mocks.MockWellKnownHandler;
import org.junit.Assert;
import org.junit.Test;

public class RedirectHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ProdScope);
    final var runnable = new RedirectAndWellknownServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), new MockWellKnownHandler(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    try {
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", 52000)
            .get("/x")
            .execute(callback);
        callback.awaitFailedToConnect();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.redirectPort)
            .get(webConfig.healthCheckPath)
            .execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("HEALTHY:");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.redirectPort)
            .get("/.well-known/xyz")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("Howdy");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.redirectPort)
            .get("/ok-get-me")
            .header("Origin", "my-origin")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("");
        Assert.assertEquals("https://localhost/ok-get-me", callback.headers.get("location"));
      }

    } finally {
      runnable.shutdown();
      thread.join();
      group.shutdownGracefully();
    }
  }
}

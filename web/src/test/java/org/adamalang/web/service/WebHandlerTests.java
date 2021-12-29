/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.junit.Test;

public class WebHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ProdScope);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, base);
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    try {
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", 52000).get("/x").execute(callback);
        callback.awaitFailedToConnect();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).get("/x").execute(callback);
        callback.awaitFirst();
        callback.assertData("Bad Request");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).get("/demo.html").execute(callback);
        callback.awaitFirst();
        callback.assertData("Bad Request");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).header("origin", "http://localhost").get("/demo.html").execute(callback);
        callback.awaitFirst();
        callback.assertData("Bad Request");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).get(webConfig.healthCheckPath).execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("HEALTHY:");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).get("/ex_500").execute(callback);
        callback.awaitFirst();
        callback.assertData("Bad Request");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", webConfig.port).header("origin", "http://localhost").junk().get("/demo.html").execute(callback);
        callback.awaitFailure();
      }
    } finally {
      runnable.shutdown();
      thread.join();
      group.shutdownGracefully();
    }
  }
}

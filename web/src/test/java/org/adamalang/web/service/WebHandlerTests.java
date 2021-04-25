/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.junit.Test;

public class WebHandlerTests {
  @Test
  public void flow() throws Exception {
    final var nexus = NexusTests.mockNexus(NexusTests.Scenario.Dev);
    final var runnable = new ServiceRunnable(nexus);
    final var thread = new Thread(runnable);
    thread.start();
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).get("/x").execute(callback);
        callback.awaitFirst();
        callback.assertData("Invalid Host header");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", 52000).get("/x").execute(callback);
        callback.awaitFailedToConnect();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).get("/x").execute(callback);
        callback.awaitFirst();
        callback.assertData("WOAH, Not Found");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).get("/demo.html").execute(callback);
        callback.awaitFirst();
        callback.assertData("This is a DEMO");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).header("origin", "http://localhost").get("/demo.html").execute(callback);
        callback.awaitFirst();
        callback.assertData("This is a DEMO");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).get(nexus.config.healthCheckPath).execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("HEALTHY:");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).get("/ex_500").execute(callback);
        callback.awaitFirst();
        callback.assertData("Error:500");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).header("origin", "http://localhost").junk().get("/demo.html").execute(callback);
        callback.awaitFailure();
      }
    } finally {
      group.shutdownGracefully();
    }
  }
}

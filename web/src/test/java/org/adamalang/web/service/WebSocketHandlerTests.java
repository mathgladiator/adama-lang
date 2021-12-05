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
import org.adamalang.web.service.mocks.MockServiceBase;
import org.junit.Test;

public class WebSocketHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    Config config = ConfigTests.mockConfig(ConfigTests.Scenario.DevScope);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(config, base);
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    try {
      runnable.waitForReady(1000);
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", config.port).get("/s").withWebSocket().execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"signal\":\"setup\",\"status\":\"connected\"}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", config.port).auth("yoke").get("/s").withWebSocket().execute(callback);
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
      }

      /*
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/s").withWebSocket().execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("{\"signal\":\"setup\",\"status\":\"connected\",\"session_id\":");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("bad").get("/").withWebSocket().execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"signal\":\"setup\",\"status\":\"failed_auth\"}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("crash").get("/").withWebSocket().execute(callback);
        callback.awaitClosed();
        callback.assertData("");
      }


      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        b.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer("{\"pong\":500}".getBytes(StandardCharsets.UTF_8))));
        callback.awaitClosed();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"pong\":500,\"ping\":"+(System.currentTimeMillis() - 500)+"}"));
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        b.channel().writeAndFlush(new TextWebSocketFrame("{}"));
        callback.awaitClosed();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("null").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        TestClientCallback.Mailbox box1 = callback.getOrCreate(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1}"));
        box1.awaitFirst();
        box1.assertData(0, "{\"failure\":1,\"reason\":40000}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        TestClientCallback.Mailbox box1 = callback.getOrCreate(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"impersonate\":{\"agent\":\"foo\",\"authority\":\"nope\"}}"));
        box1.awaitFirst();
        box1.assertData(0, "{\"failure\":1,\"reason\":45000}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        TestClientCallback.Mailbox box1 = callback.getOrCreate(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"impersonate\":{\"agent\":\"crash\",\"authority\":\"nope\"}}"));
        box1.awaitFirst();
        box1.assertData(0, "{\"failure\":1,\"reason\":5501}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        TestClientCallback.Mailbox box1 = callback.getOrCreate(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"impersonate\":{\"agent\":\"free\",\"authority\":\"free\"},\"method\":\"single\"}"));
        box1.awaitFirst();
        box1.assertData(0, "{\"deliver\":1,\"done\":true,\"response\":{\"once\":1}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("yoke").get("/").withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        TestClientCallback.Mailbox box1 = callback.getOrCreate(1);
        CountDownLatch latch = box1.latch(3);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":1,\"method\":\"stream\"}"));
        latch.await(1000, TimeUnit.MILLISECONDS);
        box1.assertData(0, "{\"deliver\":1,\"done\":false,\"response\":{\"s\":1}}");
        box1.assertData(1, "{\"deliver\":1,\"done\":false,\"response\":{\"s\":2}}");
        box1.assertData(2, "{\"deliver\":1,\"done\":true,\"response\":{\"s\":3}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b = TestClientRequestBuilder.start(group).server("localhost", nexus.config.port).auth("slow").get("/").withWebSocket();
        b.execute(callback);
        Thread.sleep(250); // HOPE
        b.channel().close().sync();
        callback.awaitClosed();
        Thread.sleep(1500);
      }

       */
    } finally {
      runnable.shutdown();
      thread.join();
      group.shutdownGracefully();
    }
  }
}

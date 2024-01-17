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
package org.adamalang.web.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.common.Platform;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.adamalang.web.service.mocks.MockDomainFinder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.DevScope);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    try {
      runnable.waitForReady(1000);
      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/~s")
            .header("X-Forwarded-For", "4.3.2.1")
            .withWebSocket()
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/~s")
            .header("Cookie", ClientCookieEncoder.STRICT.encode(new DefaultCookie("sak", "123")))
            .withWebSocket()
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");

        TestClientCallback.Mailbox box = callback.getOrCreate(500);
        CountDownLatch latch = box.latch(2);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":500,\"method\":\"cake\"}"));
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        box.assertData(0, "{\"deliver\":500,\"done\":false,\"response\":{\"boss\":1}}");
        box.assertData(1, "{\"deliver\":500,\"done\":true,\"response\":{\"boss\":2}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");

        TestClientCallback.Mailbox box = callback.getOrCreate(500);
        CountDownLatch latch = box.latch(2);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":500,\"method\":\"cake2\"}"));
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        box.assertData(0, "{\"deliver\":500,\"done\":false,\"response\":{\"boss\":1}}");
        box.assertData(1, "{\"deliver\":500,\"done\":true}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");

        TestClientCallback.Mailbox box = callback.getOrCreate(500);
        CountDownLatch latch = box.latch(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":500,\"method\":\"ex\"}"));
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        box.assertData(0, "{\"failure\":500,\"reason\":1234,\"retry\":false}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");

        TestClientCallback.Mailbox box = callback.getOrCreate(500);
        CountDownLatch latch = box.latch(1);
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":500,\"method\":\"kill\"}"));
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        box.assertData(0, "{\"deliver\":500,\"done\":false,\"response\":{\"death\":1}}");
        callback.awaitDisconnect();
        callback.assertData(
            "{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}{\"deliver\":500,\"done\":false,\"response\":{\"death\":1}}{\"status\":\"disconnected\",\"reason\":\"keepalive-failure\"}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
        b.channel().writeAndFlush(new TextWebSocketFrame("{}"));
        callback.awaitDisconnect();
        callback.assertData(
            "{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}{\"status\":\"disconnected\",\"reason\":233120}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"pong\":42}"));
        callback.awaitDisconnect();
        callback.assertData(
            "{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}{\"status\":\"disconnected\",\"reason\":295116}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"pong\":42,\"ping\":80}"));
        b.channel().writeAndFlush(new TextWebSocketFrame("{\"id\":500,\"method\":\"kill\"}"));
        callback.awaitDisconnect();
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder b =
            TestClientRequestBuilder.start(group)
                .server("localhost", webConfig.port)
                .get("/~s")
                .withWebSocket();
        b.execute(callback);
        callback.awaitFirst();
        callback.assertData("{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}");
        callback.awaitPing();
        callback.assertDataPrefix(1, "{\"ping\":");
        b.channel()
            .writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(new byte[] {0x42})));
        callback.awaitDisconnect();
        callback.assertData(
            "{\"status\":\"connected\",\"version\":\"" + Platform.VERSION + "\",\"identities\":{}}{\"status\":\"disconnected\",\"reason\":213711}");
      }
    } finally {
      runnable.shutdown();
      thread.join();
      group.shutdownGracefully();
    }
  }
}

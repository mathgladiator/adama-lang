/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

public class WebHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ProdScope);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), () -> {});
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
            .server("localhost", webConfig.port)
            .get("/x")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Bad Request; Not Found</title></head><body>Sorry, the request was not found within our handler space.</body></html>");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/~assets/space/key/id=123")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Bad Request</title></head><body>Asset cookie was not set.</body></html>");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Cookie", ClientCookieEncoder.STRICT.encode("SAK", SecureAssetUtil.makeAssetKeyHeader()))
            .get("/~assets/space/key/id=123")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Asset Failure</title></head><body>Failure to initiate asset attachment.</body></html>");
      }

      {
        String keyHeader = SecureAssetUtil.makeAssetKeyHeader();
        SecretKey key = SecureAssetUtil.secretKeyOf(keyHeader);
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Cookie", ClientCookieEncoder.STRICT.encode("SAK", keyHeader))
            .get("/~assets/space/fail/id=" + SecureAssetUtil.encryptToBase64(key, "1"))
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("Download asset failure:1234");
      }

      {
        String keyHeader = SecureAssetUtil.makeAssetKeyHeader();
        SecretKey key = SecureAssetUtil.secretKeyOf(keyHeader);
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Cookie", ClientCookieEncoder.STRICT.encode("SAK", keyHeader))
            .get("/~assets/space/incomplete/id=" + SecureAssetUtil.encryptToBase64(key, "1"))
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("Chunk");
      }

      {
        String keyHeader = SecureAssetUtil.makeAssetKeyHeader();
        SecretKey key = SecureAssetUtil.secretKeyOf(keyHeader);
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Cookie", ClientCookieEncoder.STRICT.encode("SAK", keyHeader))
            .get("/~assets/space/1/id=" + SecureAssetUtil.encryptToBase64(key, "1"))
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("ChunkAndDone");
      }

      {
        String keyHeader = SecureAssetUtil.makeAssetKeyHeader();
        SecretKey key = SecureAssetUtil.secretKeyOf(keyHeader);
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Cookie", ClientCookieEncoder.STRICT.encode("SAK", keyHeader))
            .get("/~assets/space/3/id=" + SecureAssetUtil.encryptToBase64(key, "1"))
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("Chunk1Chunk2Chunk3");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/crash")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Bad Request; Not Found</title></head><body>Sorry, the request was not found within our handler space.</body></html>");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        callback.keepPings = true; // Hack since the libadama.js HAS "ping" in it
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/libadama.js")
            .execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("function AdamaTree(");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .post("/crash", "{}")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Bad Request; Not Found</title></head><body>Sorry, the request was not found within our handler space.</body></html>");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .post("/body", "{\"x\":{}}")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("body:{\"x\":{}}");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .options("/nope")
            .header("Origin", "my-origin")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("");
        System.err.println("HERE");
        System.err.println(callback.headers);
        Assert.assertNull(callback.headers.get("access-control-allow-origin"));
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .options("/ok-cors")
            .header("Origin", "my-origin")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("");
        Assert.assertEquals("my-origin", callback.headers.get("access-control-allow-origin"));
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/foo")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("goo");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("Origin", "FOO")
            .get("/~p123")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("OK");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get(webConfig.healthCheckPath)
            .execute(callback);
        callback.awaitFirst();
        callback.assertDataPrefix("HEALTHY:");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("origin", "http://localhost")
            .junk()
            .get("/demo.html")
            .execute(callback);
        callback.awaitFailure();
      }

    } finally {
      runnable.shutdown();
      thread.join();
      group.shutdownGracefully();
    }
  }
}

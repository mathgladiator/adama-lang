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

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.TestClientCallback;
import org.adamalang.web.client.TestClientRequestBuilder;
import org.adamalang.web.service.mocks.MockDomainFinder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;

public class WebHandlerTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ProdScope);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
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
            .get("/301")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("");
        Assert.assertEquals("/loc1", callback.headers.get("location"));
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/302")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("");
        Assert.assertEquals("/loc2", callback.headers.get("location"));
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .get("/~set/key/value")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("OK");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .put("/~stash/fooyo", "{\"name\":\"def\",\"identity\":\"id\",\"max-age\":100000}")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("<html><head><title>Bad Request; Failed to set cookie</title></head><body>Sorry, the request was incomplete.</body></html>");
      }

      {
        TestClientCallback callback = new TestClientCallback();
        TestClientRequestBuilder.start(group)
            .server("localhost", webConfig.port)
            .header("origin", "https://Oooooo.com")
            .put("/~stash/fooyo", "{\"name\":\"def\",\"identity\":\"id\",\"max-age\":100000}")
            .execute(callback);
        callback.awaitFirst();
        callback.assertData("OK");
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

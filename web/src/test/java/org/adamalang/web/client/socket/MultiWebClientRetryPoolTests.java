/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client.socket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebConfigTests;
import org.adamalang.web.service.WebMetrics;
import org.adamalang.web.service.mocks.MockDomainFinder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MultiWebClientRetryPoolTests {
  @Test
  public void flow() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Pool);
    MockServiceBase base = new MockServiceBase();
    var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    SimpleExecutor executor = SimpleExecutor.create("simple");
    WebClientBase webbase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    try {
      runnable.waitForReady(1000);
      MultiWebClientRetryPoolMetrics metrics = new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory());
      MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.newJsonObject()));
      MultiWebClientRetryPool pool = new MultiWebClientRetryPool(executor, webbase, metrics, config, (connection, callback) -> connection.requestResponse(Json.parseJsonObject("{\"method\":\"auth\"}"), (r) -> (Boolean) (r.get("result").asBoolean()), new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          System.out.println("auth check: " + value);
          callback.success(null);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      }), "http://localhost:16000/~s");
      try {
        CountDownLatch latch = new CountDownLatch(2);
        CountDownLatch failure = new CountDownLatch(1);

        pool.get(new Callback<WebClientConnection>() {
          @Override
          public void success(WebClientConnection conn) {
            latch.countDown();
            conn.execute(Json.parseJsonObject("{\"method\":\"open\"}"), new WebJsonStream() {
              @Override
              public void data(int connection, ObjectNode node) {
                latch.countDown();
              }

              @Override
              public void complete() {

              }

              @Override
              public void failure(int code) {
                System.err.println("Failure-1:" + code);
                Assert.assertEquals(787632, code);
                failure.countDown();
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });

        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        runnable.shutdown();
        thread.join();
        Assert.assertTrue(failure.await(10000, TimeUnit.MILLISECONDS));
        CountDownLatch cant_connect = new CountDownLatch(2);
        pool.get(new Callback<WebClientConnection>() {
          @Override
          public void success(WebClientConnection conn) {
            cant_connect.countDown();
            conn.execute(Json.parseJsonObject("{\"method\":\"empty\"}"), new WebJsonStream() {
              @Override
              public void data(int connection, ObjectNode node) {
              }

              @Override
              public void complete() {

              }

              @Override
              public void failure(int code) {
                System.err.println("Failure-2:" + code);
                Assert.assertEquals(770224, code);
                cant_connect.countDown();
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            cant_connect.countDown();
            cant_connect.countDown();
          }
        });
        Assert.assertTrue(cant_connect.await(10000, TimeUnit.MILLISECONDS));

        runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
        thread = new Thread(runnable);
        thread.start();
        runnable.waitForReady(1000);
        CountDownLatch redo = new CountDownLatch(2);
        pool.get(new Callback<WebClientConnection>() {
          @Override
          public void success(WebClientConnection conn) {
            redo.countDown();
            conn.execute(Json.parseJsonObject("{\"method\":\"empty\"}"), new WebJsonStream() {
              @Override
              public void data(int connection, ObjectNode node) {
              }

              @Override
              public void complete() {
                redo.countDown();
              }

              @Override
              public void failure(int code) {
                System.err.println("Failure-3:" + code);
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(redo.await(5000, TimeUnit.MILLISECONDS));

      } finally {
        pool.shutdown();
      }

    } finally {
      if (runnable != null) {
        runnable.shutdown();
      }
      thread.join();
      group.shutdownGracefully();
      executor.shutdown();
      webbase.shutdown();
    }
  }
}

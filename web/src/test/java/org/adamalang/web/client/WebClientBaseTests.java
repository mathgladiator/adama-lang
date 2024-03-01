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
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.socket.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebConfigTests;
import org.adamalang.web.service.WebMetrics;
import org.adamalang.web.service.mocks.MockDomainFinder;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WebClientBaseTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebClientBaseTests.class);

  @Test
  public void happy() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch firstPing = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      LatchedWebJsonStream streamCake = new LatchedWebJsonStream();
      LatchedWebJsonStream streamEx = new LatchedWebJsonStream();
      LatchedWebJsonStream streamEmpty = new LatchedWebJsonStream();
      Runnable cakeFin = streamCake.latchAt(3);
      Runnable exFin = streamEx.latchAt(1);
      Runnable emptyFin = streamEmpty.latchAt(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection, String version) {
              connRef.set(connection);
              connection.execute(Json.parseJsonObject("{\"method\":\"cake\"}"), streamCake);
              connection.execute(Json.parseJsonObject("{\"method\":\"ex\"}"), streamEx);
              connection.execute(Json.parseJsonObject("{\"method\":\"empty\"}"), streamEmpty);
              connectedLatch.countDown();
            }

            @Override
            public void ping(int latency) {
              firstPing.countDown();
            }

            @Override
            public void failure(Throwable t) {}

            @Override
            public void disconnected() {
              disconnected.countDown();
            }
          });
      Assert.assertTrue(connectedLatch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(firstPing.await(5000, TimeUnit.MILLISECONDS));
      cakeFin.run();
      exFin.run();
      emptyFin.run();
      streamCake.assertLine(0, "DATA:{\"boss\":1}");
      streamCake.assertLine(1, "DATA:{\"boss\":2}");
      streamCake.assertLine(2, "COMPLETE");
      streamEx.assertLine(0, "FAILURE:1234");
      streamEmpty.assertLine(0, "COMPLETE");
      runnable.shutdown();
      Assert.assertTrue(disconnected.await(5000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }

  @Test
  public void http_simple() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.HttpExecute1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      SimpleHttpRequest request = new SimpleHttpRequest("GET", "http://localhost:" + webConfig.port + "/foo", new TreeMap<>(), SimpleHttpRequestBody.EMPTY);
      CountDownLatch done = new CountDownLatch(1);
      clientBase.executeShared(request, new StringCallbackHttpResponder(LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("simple").start(), new Callback<String>() {
        @Override
        public void success(String value) {
          Assert.assertEquals("goo", value);
          done.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          done.countDown();
        }
      }));
      Assert.assertTrue(done.await(10000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }

  /*
  @Test
  public void http_timeout() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.HttpExecute1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      SimpleHttpRequest request = new SimpleHttpRequest("GET", "http://localhost:" + webConfig.port + "/timeout", new TreeMap<>(), SimpleHttpRequestBody.EMPTY);
      CountDownLatch done = new CountDownLatch(1);
      clientBase.executeShared(request, new StringCallbackHttpResponder(LOGGER, new NoOpMetricsFactory().makeRequestResponseMonitor("simple").start(), new Callback<String>() {
        @Override
        public void success(String value) {
          System.err.println("SUCCESS");
          Assert.assertEquals("goo", value);
          done.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("FAILURE");
          ex.printStackTrace();
          done.countDown();
        }
      }));
      Assert.assertTrue(done.await(1000000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }
  */

  @Test
  public void quickclose() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection, String version) {
              connection.close();
              connectedLatch.countDown();
            }

            @Override
            public void ping(int latency) {

            }

            @Override
            public void failure(Throwable t) {}

            @Override
            public void disconnected() {
              disconnected.countDown();
            }
          });
      Assert.assertTrue(connectedLatch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(disconnected.await(5000, TimeUnit.MILLISECONDS));
      runnable.shutdown();
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }

  @Test
  public void remoteCrash() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest2);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch firstPing = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection, String version) {
              connRef.set(connection);
              connection.execute(Json.parseJsonObject("{\"method\":\"crash\"}"), new LatchedWebJsonStream());
              connectedLatch.countDown();
            }

            @Override
            public void ping(int latency) {
              firstPing.countDown();
            }

            @Override
            public void failure(Throwable t) {
            }

            @Override
            public void disconnected() {
              disconnected.countDown();
            }
          });
      Assert.assertTrue(connectedLatch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(disconnected.await(5000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }

  @Test
  public void localCrash() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest3);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), new MockDomainFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch firstPing = new CountDownLatch(1);
      CountDownLatch failure = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection, String version) {
              connRef.set(connection);
              connection.execute(Json.parseJsonObject("{\"method\":\"cake\"}"), new WebJsonStream() {
                @Override
                public void data(int cId, ObjectNode node) {
                  throw new NullPointerException();
                }

                @Override
                public void complete() {

                }

                @Override
                public void failure(int code) {

                }
              });
              connectedLatch.countDown();
            }

            @Override
            public void ping(int latency) {
              firstPing.countDown();
            }

            @Override
            public void failure(Throwable t) {
              failure.countDown();
            }

            @Override
            public void disconnected() {
              disconnected.countDown();
            }
          });
      Assert.assertTrue(connectedLatch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(failure.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(disconnected.await(5000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }

  @Test
  public void nope() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest4);
    WebClientBase clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch failure = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://xyz.localhost.not.found:9999/s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection, String version) {
            }

            @Override
            public void ping(int latency) {}

            @Override
            public void failure(Throwable t) {
              failure.countDown();
            }

            @Override
            public void disconnected() {
              disconnected.countDown();
            }
          });
      Assert.assertTrue(failure.await(15000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(disconnected.await(15000, TimeUnit.MILLISECONDS));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally{
      clientBase.shutdown();
    }
  }
}

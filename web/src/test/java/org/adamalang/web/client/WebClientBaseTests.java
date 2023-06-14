/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.socket.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebConfigTests;
import org.adamalang.web.service.WebMetrics;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WebClientBaseTests {
  @Test
  public void happy() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(webConfig);
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
            public void connected(WebClientConnection connection) {
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
  public void quickclose() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.ClientTest1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection) {
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
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch connectedLatch = new CountDownLatch(1);
      CountDownLatch firstPing = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://localhost:" + webConfig.port + "/~s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection) {
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
    final var runnable = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, new NullCertificateFinder(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    runnable.waitForReady(1000);
    WebClientBase clientBase = new WebClientBase(webConfig);
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
            public void connected(WebClientConnection connection) {
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
    WebClientBase clientBase = new WebClientBase(webConfig);
    try {
      AtomicReference<WebClientConnection> connRef = new AtomicReference<>();
      CountDownLatch failure = new CountDownLatch(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      clientBase.open(
          "http://xyz.localhost.not.found:9999/s",
          new WebLifecycle() {
            @Override
            public void connected(WebClientConnection connection) {
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

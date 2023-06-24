/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.sm;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClientFinder;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.routing.cache.AggregatedCacheRouter;
import org.adamalang.net.mocks.LatchedSeqCallback;
import org.adamalang.net.mocks.MockSimpleEvents;
import org.junit.Test;

import java.util.ArrayList;

public class ConnectionTests {

  @Test
  public void happy() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor executor = SimpleExecutor.create("executor");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, engine, logger);
      try {
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, executor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable gotConnected = events.latchAt(1);
        Runnable gotData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, 1000, events);
        ArrayList<LatchedSeqCallback> callbacks = new ArrayList<>();
        for (int k = 0; k < 2; k++) {
          connection.update("{\"k\":" + k + "}");
          {
            LatchedSeqCallback callbackCan = new LatchedSeqCallback();
            callbacks.add(callbackCan);
            connection.canAttach(new Callback<Boolean>() {
              @Override
              public void success(Boolean value) {
                callbackCan.success(100);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callbackCan.failure(ex);
              }
            });
          }
          {
            LatchedSeqCallback callbackAttach = new LatchedSeqCallback();
            callbacks.add(callbackAttach);
            connection.attach("id", "name", "text/plan", 100, "md5", "sha", callbackAttach);
          }

          {
            LatchedSeqCallback callbackSend = new LatchedSeqCallback();
            callbacks.add(callbackSend);
            connection.send("foo", null, "{}", callbackSend);
          }
        }
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        connection.open();
        gotConnected.run();
        gotData.run();
        for (LatchedSeqCallback callback : callbacks) {
          callback.assertJustSuccess();
        }
        connection.close();
      } finally {
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
    }
  }
  
  @Test
  public void sad() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor executor = SimpleExecutor.create("executor");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.BAD_CODE);
      AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, engine, logger);
      try {
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, executor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable gotConnected = events.latchAt(1);
        Runnable gotData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, 1000, events);
        ArrayList<LatchedSeqCallback> callbacks = new ArrayList<>();
        for (int k = 0; k < 20; k++) {
          connection.update("{\"k\":" + k + "}");
          {
            LatchedSeqCallback callbackCan = new LatchedSeqCallback();
            callbacks.add(callbackCan);
            connection.canAttach(new Callback<Boolean>() {
              @Override
              public void success(Boolean value) {
                callbackCan.success(100);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callbackCan.failure(ex);
              }
            });
          }
          {
            LatchedSeqCallback callbackAttach = new LatchedSeqCallback();
            callbacks.add(callbackAttach);
            connection.attach("id", "name", "text/plan", 100, "md5", "sha", callbackAttach);
          }

          {
            LatchedSeqCallback callbackSend = new LatchedSeqCallback();
            callbacks.add(callbackSend);
            connection.send("foo", null, "{}", callbackSend);
          }
        }
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        connection.open();
        gotConnected.run();
        gotData.run();
        for (LatchedSeqCallback callback : callbacks) {
          callback.assertJustFail();
        }
        connection.close();
      } finally {
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.sm;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.net.client.routing.cache.AggregatedCacheRouter;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ComplexHelper {
  public static final String SIMPLE = "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; } @can_attach { return true; } @attached(what) {} ";
  public static final String VIEW_MIRROR = "@static { create { return true; } } @connected { return true; } view int z; bubble zz = 1000 + @viewer.z; public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; } @can_attach { return true; } @attached(what) {} ";
  public static final String BAD_CODE = "@can_attach { int x = 1; while(true) { x++; } return true; } @attached(what) { while(true) {} } @static { create { return true; } } @connected { return true; } message M {} channel foo(M y) { while(true) {} }  ";

  public static void spinUpCapacity(TestBed[] servers, boolean start, String code) throws Exception {
    for (int k = 0; k < servers.length; k++) {
      servers[k] = new TestBed(20005 + k, code);
      CountDownLatch latchMade = new CountDownLatch(1);
      servers[k].coreService.create(
          new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", "key"),
          new Key("space", "key"),
          "{}",
          null,
          new Callback<Void>() {
            @Override
            public void success(Void value) {
              latchMade.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {}
          });
      Assert.assertTrue(latchMade.await(1000, TimeUnit.MILLISECONDS));
      if (start) {
        servers[k].startServer();
      }
    }
  }

  public static void waitForRoutingToCatch(AggregatedCacheRouter engine, String space, String key, String target) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    int timeout = 10000;
    do {
      timeout -= 50;
      if (timeout < 0) {
        throw new RuntimeException("timed out");
      }
      engine.get(new Key(space, key), new RoutingCallback() {
        @Override
        public void onRegion(String region) {
        }
        @Override
        public void failure(ErrorCodeException ex) {
        }
        @Override
        public void onMachine(String machine) {
          if (target == null && machine == null) {
            latch.countDown();
          }
          if (target != null && target.equals(machine)) {
            latch.countDown();
          }
        }
      });
    } while (!latch.await(50, TimeUnit.MILLISECONDS));
  }

  public static void startCapacity(TestBed[] servers) throws Exception{
    for (int k = 0; k < servers.length; k++) {
        servers[k].startServer();
    }
    System.err.println("START WENT GREAT");
  }

  public static void stopCapacity(TestBed[] servers) {
    try {
      for (int k = 0; k < servers.length; k++) {
        if (servers[k] != null) {
          servers[k].close();
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("SHUTDOWN WENT POORLY");
    }
  }
}

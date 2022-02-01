/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.StreamMonitor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class JsonResponderHashMapCleanupProxyTests {

  public static class MockStreamMetrics implements StreamMonitor.StreamMonitorInstance {
    public int progress_count = 0;
    public int finish_count = 0;
    public int failure_count = 0;
    public int failure_last_code = -1;
    @Override
    public void progress() {
      progress_count++;
    }

    @Override
    public void finish() {
      finish_count++;
    }

    @Override
    public void failure(int code) {
      failure_count++;
      failure_last_code = code;
    }
  }

  @Test
  public void streamPass() {
    MockStreamMetrics metrics = new MockStreamMetrics();
    HashMap<Integer, Integer> map = new HashMap<>();
    map.put(42, 1);
    MockJsonResponder responder = new MockJsonResponder();
    JsonResponderHashMapCleanupProxy proxy =
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder);
    Assert.assertTrue(map.containsKey(42));
    proxy.stream("X");
    Assert.assertEquals("STREAM:X", responder.events.get(0));
    Assert.assertTrue(map.containsKey(42));
    Assert.assertEquals(1, metrics.progress_count);
    Assert.assertEquals(0, metrics.finish_count);
    Assert.assertEquals(0, metrics.failure_count);
    Assert.assertEquals(-1, metrics.failure_last_code);
  }

  @Test
  public void finishRemoves() {
    MockStreamMetrics metrics = new MockStreamMetrics();
    HashMap<Long, Integer> map = new HashMap<>();
    map.put(42L, 1);
    MockJsonResponder responder = new MockJsonResponder();
    JsonResponderHashMapCleanupProxy proxy =
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder);
    Assert.assertTrue(map.containsKey(42L));
    proxy.finish("X");
    Assert.assertEquals("FINISH:X", responder.events.get(0));
    Assert.assertFalse(map.containsKey(42L));
    Assert.assertEquals(0, metrics.progress_count);
    Assert.assertEquals(1, metrics.finish_count);
    Assert.assertEquals(0, metrics.failure_count);
    Assert.assertEquals(-1, metrics.failure_last_code);
  }

  @Test
  public void errorRemoves() {
    MockStreamMetrics metrics = new MockStreamMetrics();
    HashMap<Long, Integer> map = new HashMap<>();
    map.put(42L, 1);
    MockJsonResponder responder = new MockJsonResponder();
    JsonResponderHashMapCleanupProxy proxy =
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder);
    Assert.assertTrue(map.containsKey(42L));
    proxy.error(new ErrorCodeException(123));
    Assert.assertEquals("ERROR:123", responder.events.get(0));
    Assert.assertFalse(map.containsKey(42L));
    Assert.assertEquals(0, metrics.progress_count);
    Assert.assertEquals(0, metrics.finish_count);
    Assert.assertEquals(1, metrics.failure_count);
    Assert.assertEquals(123, metrics.failure_last_code);
  }
}

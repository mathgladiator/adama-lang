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
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
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
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder, Json.newJsonObject(), JsonLogger.NoOp);
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
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder, Json.newJsonObject(), JsonLogger.NoOp);
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
        new JsonResponderHashMapCleanupProxy(metrics, SimpleExecutor.NOW, map, 42, responder, Json.newJsonObject(), JsonLogger.NoOp);
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

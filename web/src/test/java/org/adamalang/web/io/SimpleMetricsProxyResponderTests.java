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
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.junit.Assert;
import org.junit.Test;

public class SimpleMetricsProxyResponderTests {
  public static class MockMetricsRR implements RequestResponseMonitor.RequestResponseMonitorInstance {

    public int success_count = 0;
    public int extra_count = 0;
    public int failure_count = 0;
    public int failure_last_code = -1;
    @Override
    public void success() {
      success_count++;
    }

    @Override
    public void extra() {
      extra_count++;
    }

    @Override
    public void failure(int code) {
      failure_count++;
      failure_last_code = code;
    }
  }

  @Test
  public void flow() {
    MockMetricsRR metrics = new MockMetricsRR();
    MockJsonResponder responder = new MockJsonResponder();
    SimpleMetricsProxyResponder proxy = new SimpleMetricsProxyResponder(metrics, responder, Json.newJsonObject(), JsonLogger.NoOp);
    proxy.stream("X");
    proxy.stream("X");
    proxy.stream("X");
    Assert.assertEquals(0, metrics.success_count);
    Assert.assertEquals(3, metrics.extra_count);
    Assert.assertEquals(0, metrics.failure_count);
    Assert.assertEquals(-1, metrics.failure_last_code);
    proxy.finish("Y");
    proxy.finish("Y");
    Assert.assertEquals(2, metrics.success_count);
    Assert.assertEquals(3, metrics.extra_count);
    Assert.assertEquals(0, metrics.failure_count);
    Assert.assertEquals(-1, metrics.failure_last_code);
    proxy.error(new ErrorCodeException(42));
    Assert.assertEquals(2, metrics.success_count);
    Assert.assertEquals(3, metrics.extra_count);
    Assert.assertEquals(1, metrics.failure_count);
    Assert.assertEquals(42, metrics.failure_last_code);
    Assert.assertEquals(6, responder.events.size());
    Assert.assertEquals("STREAM:X", responder.events.get(0));
    Assert.assertEquals("STREAM:X", responder.events.get(1));
    Assert.assertEquals("STREAM:X", responder.events.get(2));
    Assert.assertEquals("FINISH:Y", responder.events.get(3));
    Assert.assertEquals("FINISH:Y", responder.events.get(4));
    Assert.assertEquals("ERROR:42", responder.events.get(5));
  }
}

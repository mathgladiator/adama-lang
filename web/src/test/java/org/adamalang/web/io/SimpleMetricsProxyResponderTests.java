/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

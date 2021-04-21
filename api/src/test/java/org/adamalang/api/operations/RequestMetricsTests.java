package org.adamalang.api.operations;

import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.mocks.MockResponder;
import org.adamalang.api.util.Json;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class RequestMetricsTests {
  @Test
  public void flow() {
    CounterFactory cf = new CounterFactory();
    RequestMetrics rm = new RequestMetrics(cf, "req");
    MockResponder mock = new MockResponder();
    CommandResponder r = RequestMetrics.wrap(mock, rm);

    r.stream("blah");
    r.finish("zoop");
    Assert.assertEquals("blah", mock.data.get(0));
    Assert.assertEquals("zoop", mock.data.get(1));
  }

  @Test
  public void rm_error() {
    CounterFactory cf = new CounterFactory();
    RequestMetrics rm = new RequestMetrics(cf, "req");
    MockResponder mock = new MockResponder();
    CommandResponder r = RequestMetrics.wrap(mock, rm);
    r.error(new ErrorCodeException(100));
    Assert.assertEquals(100, mock.ex.code);
  }

  @Test
  public void rm_finish() {
    CounterFactory cf = new CounterFactory();
    RequestMetrics rm = new RequestMetrics(cf, "req");
    MockResponder mock = new MockResponder();
    CommandResponder r = RequestMetrics.wrap(mock, rm);
    r.stream("{}");
    r.finish("[]");
  }
}

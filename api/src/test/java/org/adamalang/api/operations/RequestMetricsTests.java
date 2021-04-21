/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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

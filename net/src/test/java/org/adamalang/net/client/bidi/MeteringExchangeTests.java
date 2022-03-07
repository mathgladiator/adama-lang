package org.adamalang.net.client.bidi;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.net.mocks.MockMeteringFlow;
import org.junit.Test;

public class MeteringExchangeTests {
  @Test
  public void coverage() {
    MockMeteringFlow proxy = new MockMeteringFlow();
    Runnable got = proxy.latchAt(1);
    MeteringExchange exchange = new MeteringExchange("target", proxy);
    exchange.failure(new ErrorCodeException(-123));
    got.run();
    proxy.assertWrite(0, "ERROR:-123");
  }
}

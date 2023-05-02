/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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

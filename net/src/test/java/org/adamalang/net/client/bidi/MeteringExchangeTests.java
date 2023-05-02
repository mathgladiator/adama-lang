/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
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

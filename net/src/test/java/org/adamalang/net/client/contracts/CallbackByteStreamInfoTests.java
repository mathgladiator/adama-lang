/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.impl.CallbackByteStreamInfo;
import org.junit.Test;

public class CallbackByteStreamInfoTests {
  @Test
  public void hacky() {
    new CallbackByteStreamInfo(null, new ClientMetrics(new NoOpMetricsFactory())).failure(new ErrorCodeException(-1));
  }
}

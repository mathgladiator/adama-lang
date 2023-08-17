/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.contracts.impl.CallbackByteStreamInfo;
import org.junit.Test;

public class CallbackByteStreamInfoTests {
  @Test
  public void hacky() {
    new CallbackByteStreamInfo(null, new LocalRegionClientMetrics(new NoOpMetricsFactory())).failure(new ErrorCodeException(-1));
  }
}

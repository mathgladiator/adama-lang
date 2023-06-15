/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client.socket;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class MultiWebClientRetryPoolConfigTests {

  @Test
  public void defaults() {
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{}")));
    Assert.assertEquals(2, config.connectionCount);
    Assert.assertEquals(50, config.maxInflight);
    Assert.assertEquals(1500, config.findTimeout);
    Assert.assertEquals(5000, config.maxBackoff);
  }

  @Test
  public void coverage() {
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{\"multi-connection-count\":5,\"multi-inflight-limit\":77,\"multi-timeout-find\":9998,\"multi-max-backoff-milliseconds\":1234}")));
    Assert.assertEquals(5, config.connectionCount);
    Assert.assertEquals(77, config.maxInflight);
    Assert.assertEquals(9998, config.findTimeout);
    Assert.assertEquals(1234, config.maxBackoff);
  }
}

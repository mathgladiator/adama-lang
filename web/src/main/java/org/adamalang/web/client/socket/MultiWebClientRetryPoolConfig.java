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

/** the configuration for the behavior of maintaining a connection to an endpoint */
public class MultiWebClientRetryPoolConfig {
  public final int connectionCount;
  public final int maxInflight;
  public final int findTimeout;
  public final int maxBackoff;

  public MultiWebClientRetryPoolConfig(ConfigObject config) {
    this.connectionCount = config.intOf("multi-connection-count", 2);
    this.maxInflight = config.intOf("multi-inflight-limit", 50);
    this.findTimeout = config.intOf("multi-timeout-find", 1500);
    this.maxBackoff = config.intOf("multi-max-backoff-milliseconds", 5000);
  }
}

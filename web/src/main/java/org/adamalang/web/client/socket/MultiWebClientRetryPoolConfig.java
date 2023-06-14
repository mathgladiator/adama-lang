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

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import org.adamalang.common.ConfigObject;

public class WebConfig {
  public final String healthCheckPath;
  public final String deepHealthCheckPath;
  public final int maxContentLengthSize;
  public final int maxWebSocketFrameSize;
  public final int port;
  public final int redirectPort;
  public final int timeoutWebsocketHandshake;
  public final int heartbeatTimeMilliseconds;
  public final int readTimeoutSeconds;
  public final int writeTimeoutSeconds;
  public final int idleReadSeconds;
  public final int idleWriteSeconds;
  public final int idleAllSeconds;
  public final int bossThreads;
  public final int workerThreads;
  public final String regionalDomain;
  public final String[] globalDomains;
  public final int sharedConnectionPoolMaxLifetimeMilliseconds;
  public final int sharedConnectionPoolMaxUsageCount;
  public final int sharedConnectionPoolMaxPoolSize;

  public WebConfig(ConfigObject config) {
    // HTTP properties
    this.port = config.intOf("http_port", 8080);
    this.redirectPort = config.intOf("http_redirect_port", 8085);
    this.maxContentLengthSize = config.intOf("http_max_content_length_size", 4194304);
    this.healthCheckPath = config.strOf("http_health_check_path", "/~health_check_lb");
    this.deepHealthCheckPath = config.strOf("http_deep_health_check_path", "/~deep_health_check_status_page");
    // WebSocket properties
    this.timeoutWebsocketHandshake = config.intOf("websocket_handshake_timeout_ms", 2500);
    this.readTimeoutSeconds = config.intOf("websocket_read_timeout_sec", 10);
    this.writeTimeoutSeconds = config.intOf("websocket_write_timeout_sec", 5);
    this.idleReadSeconds = config.intOf("websocket_read_idle_sec", 0);
    this.idleWriteSeconds = config.intOf("websocket_write_idle_sec", 0);
    this.idleAllSeconds = config.intOf("websocket_all_idle_sec", 15);
    this.maxWebSocketFrameSize = config.intOf("websocket_max_frame_size", 1048576);
    this.heartbeatTimeMilliseconds = config.intOf("websocket_heart_beat_ms", 1000);
    this.bossThreads = config.intOf("http_boss_threads", 2);
    this.workerThreads = config.intOf("http_worker_threads", 16);
    this.regionalDomain = config.strOf("regional_domain", "adama-platform.com");
    this.globalDomains = config.stringsOf("global_domains", new String[] { "adama.games" });
    this.sharedConnectionPoolMaxLifetimeMilliseconds = config.intOf("shared-connection-max-lifetime-ms", 10000);
    this.sharedConnectionPoolMaxUsageCount = config.intOf("shared-connection-max-usage-count", 50);
    this.sharedConnectionPoolMaxPoolSize = config.intOf("shared-connection-max-pool-size", 25);
  }
}

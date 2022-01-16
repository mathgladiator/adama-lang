/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import org.adamalang.common.ConfigObject;

public class WebConfig {
  public final String healthCheckPath;
  public final int maxContentLengthSize;
  public final int maxWebSocketFrameSize;
  public final int port;
  public final int timeoutWebsocketHandshake;
  public final int heartbeatTimeMilliseconds;

  public WebConfig(ConfigObject config) {
    // HTTP properties
    this.port = config.intOf("http_port", 8080);
    this.maxContentLengthSize = config.intOf("http_max_content_length_size", 4194304);
    this.healthCheckPath = config.strOf("http_health_check_path", "/~health_check_lb");
    // WebSocket properties
    this.timeoutWebsocketHandshake = config.intOf("websocket_handshake_timeout_ms", 2500);
    this.maxWebSocketFrameSize = config.intOf("websocket_max_frame_size", 1048576);
    this.heartbeatTimeMilliseconds = config.intOf("websocket_heart_beat_ms", 1000);
  }
}

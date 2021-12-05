/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Config {
  public final String healthCheckPath;
  public final int maxContentLengthSize;
  public final int maxWebSocketFrameSize;
  public final int port;
  public final int timeoutWebsocketHandshake;
  public final int heartbeatTimeMilliseconds;

  public Config(ObjectNode node) {
    // HTTP properties
    this.port = intOf(node, "http_port", 8080);
    this.maxContentLengthSize = intOf(node, "http_max_content_length_size", 4194304);
    this.healthCheckPath = strOf(node, "http_health_check_path", "/~health_check_lb");

    // WebSocket properties
    this.timeoutWebsocketHandshake = intOf(node, "websocket_handshake_timeout_ms", 2500);
    this.maxWebSocketFrameSize = intOf(node, "websocket_max_frame_size", 1048576);
    this.heartbeatTimeMilliseconds = intOf(node, "websocket_heart_beat_ms", 1000);
  }

  private static int intOf(ObjectNode node, String key, int defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isInt()) {
      return defaultValue;
    } else {
      return v.intValue();
    }
  }

  private static String strOf(ObjectNode node, String key, String defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isTextual()) {
      return defaultValue;
    } else {
      return v.textValue();
    }
  }
}

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
  public final boolean production;
  public final int timeoutWebsocketHandshake;
  public final String websocketPath;
  public final Set<String> allowedOrigins;

  public final boolean allowAssetUploads;
  public final String assetUploadPath;
  public final String authenticationPath;

  public final String staticSitePath;
  public final String websocketAuthCookieName;
  public final int heartbeatTimeMilliseconds;

  public Config(ObjectNode node) {
    // system properties
    this.production = boolOf(node, "production", false);

    // HTTP properties
    this.port = intOf(node, "http_port", 8080);
    this.maxContentLengthSize = intOf(node, "http_max_content_length_size", 4194304);
    this.healthCheckPath = strOf(node, "http_health_check_path", "/~health_check_lb");
    this.allowAssetUploads = boolOf(node, "http_allow_asset_uploads", false);
    this.assetUploadPath = strOf(node, "http_asset_upload_path", "/~asset");
    this.authenticationPath = strOf(node, "http_authentication_path", "/~auth");
    this.staticSitePath = strOf(node, "http_static_file_path", "./static");

    // CORS
    HashSet originsToAllow = new HashSet<>();
    if (!this.production) {
      originsToAllow.add("http://localhost");
      originsToAllow.add("http://localhost:" + port);
      originsToAllow.add("http://127.0.0.1");
      originsToAllow.add("http://127.0.0.1:" + port);
    }
    JsonNode httpOriginsNode = node.get("http_origins");
    if (httpOriginsNode != null && httpOriginsNode.isArray()) {
      ArrayNode httpOrigins = (ArrayNode) httpOriginsNode;
      for (int k = 0; k < httpOrigins.size(); k++) {
        JsonNode elementNode = httpOrigins.get(k);
        if (elementNode != null && elementNode.isTextual()) {
          originsToAllow.add(elementNode.textValue());
        }
      }
    }
    this.allowedOrigins = Collections.unmodifiableSet(originsToAllow);

    // WebSocket properties
    this.timeoutWebsocketHandshake = intOf(node, "websocket_handshake_timeout_ms", 2500);
    this.maxWebSocketFrameSize = intOf(node, "websocket_max_frame_size", 1048576);
    this.websocketPath = strOf(node, "websocket_path", "/~socket");
    this.websocketAuthCookieName = strOf(node, "websocket_auth_cookie_name", "ADAMA");
    this.heartbeatTimeMilliseconds = intOf(node, "websocket_heart_beat_ms", 1000);
  }

  public UriHandler produceStaticHandler() throws Exception {
    File file = new File(staticSitePath);
    if (production) {
      return new ProductionStaticSiteHandler(file);
    } else {
      return new DevelopmentStaticSiteHandler(file);
    }
  }

  private static int intOf(ObjectNode node, String key, int defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isInt()) {
      return defaultValue;
    } else {
      return v.intValue();
    }
  }
  private static boolean boolOf(ObjectNode node, String key, boolean defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isBoolean()) {
      return defaultValue;
    } else {
      return v.booleanValue();
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

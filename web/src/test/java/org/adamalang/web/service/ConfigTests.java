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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.util.Json;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTests {
  @Test
  public void defaults() {
    Config config = new Config(Json.newJsonObject());
    Assert.assertEquals("/~health_check_lb", config.healthCheckPath);
    Assert.assertEquals("/~asset", config.assetUploadPath);
    Assert.assertEquals("/~auth", config.authenticationPath);
    Assert.assertEquals("/~socket", config.websocketPath);
    Assert.assertEquals("./static", config.staticSitePath);
    Assert.assertEquals("ADAMA", config.websocketAuthCookieName);
    Assert.assertEquals(1048576, config.maxWebSocketFrameSize);
    Assert.assertEquals(2500, config.timeoutWebsocketHandshake);
    Assert.assertEquals(4194304, config.maxContentLengthSize);
    Assert.assertEquals(1000, config.heartbeatTimeMilliseconds);
    Assert.assertEquals(8080, config.port);
    Assert.assertFalse(config.production);
    Assert.assertFalse(config.allowAssetUploads);
    Assert.assertTrue(config.allowedOrigins.contains("http://localhost"));
    Assert.assertTrue(config.allowedOrigins.contains("http://localhost:8080"));
  }

  @Test
  public void override() {
    ObjectNode node = Json.newJsonObject();
    node.put("http_port", 9000);
    node.put("http_max_content_length_size", 5000);
    node.put("websocket_max_frame_size", 7000);
    node.put("websocket_handshake_timeout_ms", 123);
    node.put("http_health_check_path", "HEALTH");
    node.put("http_asset_upload_path", "ASSET");
    node.put("http_authentication_path", "AUTH");
    node.put("http_static_file_path", "PATH");
    node.put("websocket_path", "SOCKET");
    node.put("websocket_auth_cookie_name", "COOKIE");
    node.put("production", true);
    node.put("http_allow_asset_uploads", true);
    node.put("websocket_heart_beat_ms", 666);
    Config config = new Config(node);
    Assert.assertEquals(666, config.heartbeatTimeMilliseconds);
    Assert.assertEquals("HEALTH", config.healthCheckPath);
    Assert.assertEquals("ASSET", config.assetUploadPath);
    Assert.assertEquals("AUTH", config.authenticationPath);
    Assert.assertEquals("SOCKET", config.websocketPath);
    Assert.assertEquals("PATH", config.staticSitePath);
    Assert.assertEquals(7000, config.maxWebSocketFrameSize);
    Assert.assertEquals(123, config.timeoutWebsocketHandshake);
    Assert.assertEquals(5000, config.maxContentLengthSize);
    Assert.assertEquals("COOKIE", config.websocketAuthCookieName);
    Assert.assertEquals(9000, config.port);
    Assert.assertTrue(config.production);
    Assert.assertTrue(config.allowAssetUploads);
    Assert.assertFalse(config.allowedOrigins.contains("http://localhost"));
    Assert.assertFalse(config.allowedOrigins.contains("http://localhost:8080"));
  }

  @Test
  public void origins() {
    ObjectNode node = Json.newJsonObject();
    node.put("production", true);
    ArrayNode origins = node.putArray("http_origins");
    origins.add("yo");
    Config config = new Config(node);
    Assert.assertTrue(config.allowedOrigins.contains("yo"));
  }
}

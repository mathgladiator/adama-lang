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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.web.io.Json;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTests {
  @Test
  public void defaults() {
    Config config = new Config(Json.newJsonObject());
    Assert.assertEquals("/~health_check_lb", config.healthCheckPath);
    Assert.assertEquals(1048576, config.maxWebSocketFrameSize);
    Assert.assertEquals(2500, config.timeoutWebsocketHandshake);
    Assert.assertEquals(4194304, config.maxContentLengthSize);
    Assert.assertEquals(1000, config.heartbeatTimeMilliseconds);
    Assert.assertEquals(8080, config.port);
  }

  @Test
  public void override() {
    ObjectNode node = Json.newJsonObject();
    node.put("http_port", 9000);
    node.put("http_max_content_length_size", 5000);
    node.put("websocket_max_frame_size", 7000);
    node.put("websocket_handshake_timeout_ms", 123);
    node.put("http_health_check_path", "HEALTH");
    node.put("websocket_heart_beat_ms", 666);
    Config config = new Config(node);
    Assert.assertEquals(666, config.heartbeatTimeMilliseconds);
    Assert.assertEquals("HEALTH", config.healthCheckPath);
    Assert.assertEquals(7000, config.maxWebSocketFrameSize);
    Assert.assertEquals(123, config.timeoutWebsocketHandshake);
    Assert.assertEquals(5000, config.maxContentLengthSize);
    Assert.assertEquals(9000, config.port);
  }

  public static enum Scenario {
    Mock1(15000),
    Mock2(15001),
    Mock3(15002),
    Dev(15003),
    Prod(15004),
    DevScope(15005),
    ProdScope(15006);

    public final int port;
    private Scenario(int port) {
      this.port = port;
    }
  }

  public static Config mockConfig(Scenario scenario) throws Exception {
    ObjectNode configNode = Json.newJsonObject();
    configNode.put("http_port", scenario.port);
    return new Config(configNode);
  }
}

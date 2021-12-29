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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class WebConfigTests {
  @Test
  public void defaults() {
    WebConfig webConfig = new WebConfig(Json.newJsonObject());
    Assert.assertEquals("/~health_check_lb", webConfig.healthCheckPath);
    Assert.assertEquals(1048576, webConfig.maxWebSocketFrameSize);
    Assert.assertEquals(2500, webConfig.timeoutWebsocketHandshake);
    Assert.assertEquals(4194304, webConfig.maxContentLengthSize);
    Assert.assertEquals(1000, webConfig.heartbeatTimeMilliseconds);
    Assert.assertEquals(8080, webConfig.port);
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
    WebConfig webConfig = new WebConfig(node);
    Assert.assertEquals(666, webConfig.heartbeatTimeMilliseconds);
    Assert.assertEquals("HEALTH", webConfig.healthCheckPath);
    Assert.assertEquals(7000, webConfig.maxWebSocketFrameSize);
    Assert.assertEquals(123, webConfig.timeoutWebsocketHandshake);
    Assert.assertEquals(5000, webConfig.maxContentLengthSize);
    Assert.assertEquals(9000, webConfig.port);
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

  public static WebConfig mockConfig(Scenario scenario) throws Exception {
    ObjectNode configNode = Json.newJsonObject();
    configNode.put("http_port", scenario.port);
    configNode.put("websocket_heart_beat_ms", 250);
    return new WebConfig(configNode);
  }
}

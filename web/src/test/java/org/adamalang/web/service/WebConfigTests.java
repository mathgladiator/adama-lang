/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class WebConfigTests {
  public static WebConfig mockConfig(Scenario scenario) throws Exception {
    ObjectNode configNode = Json.newJsonObject();
    configNode.put("http-port", scenario.port);
    configNode.put("websocket-heart-beat-ms", 250);
    return new WebConfig(new ConfigObject(configNode));
  }

  @Test
  public void defaults() throws Exception {
    WebConfig webConfig = new WebConfig(new ConfigObject(Json.newJsonObject()));
    Assert.assertEquals("/~health_check_lb", webConfig.healthCheckPath);
    Assert.assertEquals(1048576, webConfig.maxWebSocketFrameSize);
    Assert.assertEquals(2500, webConfig.timeoutWebsocketHandshake);
    Assert.assertEquals(4194304, webConfig.maxContentLengthSize);
    Assert.assertEquals(1000, webConfig.heartbeatTimeMilliseconds);
    Assert.assertEquals(8080, webConfig.port);
  }

  @Test
  public void override() throws Exception {
    ObjectNode node = Json.newJsonObject();
    node.put("http-port", 9000);
    node.put("http-max-content-length-size", 5000);
    node.put("websocket-max-frame-size", 7000);
    node.put("websocket-handshake-timeout-ms", 123);
    node.put("http-health-check-path", "HEALTH");
    node.put("websocket-heart-beat-ms", 666);
    WebConfig webConfig = new WebConfig(new ConfigObject(node));
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
    ProdScope(15006),
    ClientTest1(15100),
    ClientTest2(15101),
    ClientTest3(15102),
    ClientTest4(15103),
    ClientTest5(15104);

    public final int port;

    private Scenario(int port) {
      this.port = port;
    }
  }
}

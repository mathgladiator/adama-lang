package org.adamalang.canary;

import org.adamalang.canary.agents.simple.SimpleWebSocketConnectionAgent;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;

public class DriveTraffic {
  public static void execute(CanaryConfig config) throws Exception {
    if ("simple".equals(config.mode)) {
      executeSimple(config);
    }
  }

  private static void executeSimple(CanaryConfig config) throws Exception {
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    SimpleExecutor canaryExecutor = SimpleExecutor.create("canary");
    try {
      SimpleWebSocketConnectionAgent[] connections = new SimpleWebSocketConnectionAgent[config.connections];
      for (int k = 0; k < connections.length; k++) {
        connections[k] = new SimpleWebSocketConnectionAgent(canaryExecutor, config, base);
      }
      for (int k = 0; k < connections.length; k++) {
        connections[k].kickOff();
        Thread.sleep(config.connectDelayMs);
      }
      config.blockUntilQuit();
    } finally {
      canaryExecutor.shutdown();
      base.shutdown();
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.canary.agents.simple;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;

public class SimpleDrive {
  public static void go(SimpleCanaryConfig config) throws Exception {
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

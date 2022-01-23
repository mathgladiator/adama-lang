/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary;

import org.adamalang.canary.agents.SimpleWebSocketConnectionAgent;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CanaryLoadGenTool {
  public static void main(String[] args) throws Exception {
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    SimpleExecutor canaryExecutor = SimpleExecutor.create("canary");
    CanaryConfig config = new CanaryConfig(); // TODO: fill from JSON
    SimpleWebSocketConnectionAgent[] connections = new SimpleWebSocketConnectionAgent[config.connections];
    for (int k = 0; k < connections.length; k++) {
      connections[k] = new SimpleWebSocketConnectionAgent(canaryExecutor, config, base);
    }
    for (int k = 0; k < connections.length; k++) {
      connections[k].kickOff();
    }
    CountDownLatch quitter = new CountDownLatch(1);
    while (!quitter.await(1000, TimeUnit.MILLISECONDS)) {
    }
  }
}

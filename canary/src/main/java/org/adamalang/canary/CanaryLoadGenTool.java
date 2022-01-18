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
  public static void execute() {
    String canaryURL = ""; // TODO: stand up a ELB instance backed by some capacity
    String canarySpace = ""; // TODO: create and define a space, maybe have a tool for it
    String canaryIdentity =
        ""; // TODO: maybe automate this, maybe not... This requires an init flow OR an authorities
            // setup for the space. Either way
    // TODO: create a scalable WebSocket client and use it here to spin up; this will need to be a
    // MUCH better version than what the CLI uses as we intend to create SERIOUS load from a dev
    // machine instance
    int distinctSockets = 100; // TODO: define as a parameter
    int connectionsPerSocket = 20; // TODO: define as a parameter
    int sendRatePerConnectionPerSecond = 5; // TODO: define as a parameter
  }

  public static void main(String[] args) throws Exception {
    // TODO: move this to config
    String url = "http://adama-lb-us-east-2-2073537616.us-east-2.elb.amazonaws.com/s";
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    SimpleExecutor canaryExecutor = SimpleExecutor.create("canaryExecutor");

    CanaryConfig config = new CanaryConfig(); // TODO: fill from JSON
    SimpleWebSocketConnectionAgent[] connections = new SimpleWebSocketConnectionAgent[config.connections];
    for (int k = 0; k < connections.length; k++) {
      connections[k] = new SimpleWebSocketConnectionAgent(canaryExecutor, config, base);
    }
    for (int k = 0; k < connections.length; k++) {
      connections[k].kickOff();
    }

    CountDownLatch quitter = new CountDownLatch(1);
    while(!quitter.await(1000, TimeUnit.MILLISECONDS)) {

    }
  }

}

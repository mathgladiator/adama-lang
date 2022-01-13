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

import org.adamalang.common.*;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

public class CanaryLoadGenTool {
  public static void execute() {
    String canaryURL = ""; // TODO: stand up a ELB instance backed by some capacity
    String canarySpace = ""; // TODO: create and define a space, maybe have a tool for it
    String canaryIdentity = ""; // TODO: maybe automate this, maybe not... This requires an init flow OR an authorities setup for the space. Either way
    // TODO: create a scalable WebSocket client and use it here to spin up; this will need to be a MUCH better version than what the CLI uses as we intend to create SERIOUS load from a dev machine instance
    int distinctSockets = 100; // TODO: define as a parameter
    int connectionsPerSocket = 20; // TODO: define as a parameter
    int sendRatePerConnectionPerSecond = 5; // TODO: define as a parameter
  }

  public static void main(String[] args) {
    String url = "ws://3.18.145.197/s";
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    base.open(url, new WebLifecycle() {
      @Override
      public void connected(WebClientConnection connection) {
        System.err.println("connected");
      }

      @Override
      public void ping() {
        System.err.println("Ping");
      }

      @Override
      public void failure(Throwable t) {
        System.err.println("error");
      }

      @Override
      public void disconnected() {
        System.err.println("disconnected");
        // TODO: schedule retry
        // base.open(url, this);
      }
    });
  }
}

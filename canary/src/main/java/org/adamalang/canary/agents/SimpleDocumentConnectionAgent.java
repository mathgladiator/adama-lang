/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary.agents;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.canary.CanaryConfig;
import org.adamalang.common.Json;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleDocumentConnectionAgent extends NamedRunnable implements WebJsonStream {
  private final WebClientConnection connection;
  private final CanaryConfig config;
  private SimpleExecutor agent;
  private AtomicBoolean alive;
  private boolean waitingForFirstData;
  private int connectionId;
  private long kickoffStarted;

  public SimpleDocumentConnectionAgent(WebClientConnection connection, CanaryConfig config) {
    super("agent");
    this.connection = connection;
    this.config = config;
    this.agent = SimpleExecutor.create("agent");
    this.alive = new AtomicBoolean(true);
    this.waitingForFirstData = true;
  }

  @Override
  public void execute() throws Exception {
    if (alive.get()) {
      ObjectNode send = Json.newJsonObject();
      send.put("method", "connection/send");
      send.put("connection", connectionId);
      send.put("channel", "foo");
      send.putObject("message");
      long sendStart = System.currentTimeMillis();
      connection.execute(send, new WebJsonStream() {
        @Override
        public void data(ObjectNode node) {
          System.err.println("RESP:" + node.toString());
          long latency = System.currentTimeMillis() - sendStart;
          System.err.println("Send latency:" + latency);
          agent.schedule(SimpleDocumentConnectionAgent.this, (int) (2500 + 2500 * Math.random()));
        }

        @Override
        public void complete() {

        }

        @Override
        public void failure(int code) {
          System.err.println("Send failed: " + code);
          agent.schedule(SimpleDocumentConnectionAgent.this, (int) (5000 + 5000 * Math.random()));
        }
      });

    }
  }

  public void kickOff() {
    System.err.println("connected");
    ObjectNode request = Json.newJsonObject();
    request.put("method", "connection/create");
    request.put("space", "demo1");
    request.put("key", "key11");
    request.put("identity", config.identities[(int) (config.identities.length * Math.random())]);
    kickoffStarted = System.currentTimeMillis();
    waitingForFirstData = true;
    connectionId = connection.execute(request, this);
  }

  public void kill() {
    agent.shutdown();
    alive.set(false);
  }

  @Override
  public void data(ObjectNode node) {
    if (waitingForFirstData) {
      long connectLatency = System.currentTimeMillis() - kickoffStarted;
      System.err.println("Connect latency:" + connectLatency);
      waitingForFirstData = false;
      agent.schedule(this, 2000);
    }
    System.err.println("Data:" + node.toString());
  }

  @Override
  public void complete() {
  }

  @Override
  public void failure(int code) {
    System.err.println("stream failure:" + code);
    alive.set(false);
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.canary.agents.simple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDocumentConnectionAgent extends NamedRunnable implements WebJsonStream {
  private final SimpleWebSocketConnectionAgent parent;
  private final WebClientConnection connection;
  private final SimpleCanaryConfig config;
  private final SimpleExecutor agent;
  private final AtomicBoolean alive;
  private final Random rng;
  private boolean waitingForFirstData;
  private int connectionId;
  private long kickoffStarted;
  private final AtomicInteger messagesLeft;

  public SimpleDocumentConnectionAgent(SimpleWebSocketConnectionAgent parent, WebClientConnection connection, SimpleCanaryConfig config) {
    super("agent");
    this.parent = parent;
    this.connection = connection;
    this.config = config;
    this.agent = SimpleExecutor.create("agent");
    this.alive = new AtomicBoolean(true);
    this.waitingForFirstData = true;
    this.messagesLeft = new AtomicInteger(config.messagesPerConnection);
    this.rng = new Random();
  }

  @Override
  public void execute() throws Exception {
    try {
      if (alive.get()) {
        ObjectNode send = Json.newJsonObject();
        send.put("method", "connection/send");
        send.put("connection", connectionId);
        SimpleCanaryConfig.Message msg = config.messages[rng.nextInt(config.messages.length)];
        send.put("channel", msg.channel);
        send.set("message", msg.message);
        long sendStart = System.currentTimeMillis();
        config.metrics.messages_sent.incrementAndGet();
        connection.execute(send, new WebJsonStream() {
          @Override
          public void data(int cId, ObjectNode node) {
            config.metrics.record_send_latency((int) (System.currentTimeMillis() - sendStart));
            config.metrics.messages_acked.incrementAndGet();
          }

          @Override
          public void complete() {
            if (messagesLeft.decrementAndGet() > 0) {
              agent.schedule(SimpleDocumentConnectionAgent.this, config.messageDelayMs);
            } else {
              SimpleDocumentConnectionAgent.this.kill();
            }
          }

          @Override
          public void failure(int code) {
            config.metrics.report_failure(code);
            config.metrics.messages_failed.incrementAndGet();
            complete();
          }
        });
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void kill() {
    if (alive.compareAndSet(true, false)) {
      agent.shutdown();
      parent.reportDeath();
    }
  }

  public void kickOff() {
    ObjectNode request = Json.newJsonObject();
    request.put("method", "connection/create");
    request.put("space", config.space);
    int keyId = (int) (config.keyIdMin + (config.keyIdMax - config.keyIdMin + 1) * Math.random());
    request.put("key", config.keyPrefix + keyId);
    request.put("identity", config.identities[(int) (config.identities.length * Math.random())]);
    kickoffStarted = System.currentTimeMillis();
    waitingForFirstData = true;
    connectionId = connection.execute(request, this);
  }

  @Override
  public void data(int cId, ObjectNode node) {
    if (waitingForFirstData) {
      config.metrics.record_connect_latency((int) (System.currentTimeMillis() - kickoffStarted));
      waitingForFirstData = false;
      agent.schedule(SimpleDocumentConnectionAgent.this, config.messageDelayMs);
    }
    config.metrics.deltas.incrementAndGet();
  }

  @Override
  public void complete() {
    kill();
  }

  @Override
  public void failure(int code) {
    config.metrics.report_failure(code);
    kill();
  }
}

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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CanaryConfig {
  public final String endpoint;
  public final String[] identities;
  public final String mode;
  public final int connections;
  public final String space;
  public final int documentsPerConnectionMin;
  public final int documentsPerConnectionMax;
  public final String keyPrefix;
  public final int keyIdMin;
  public final int keyIdMax;
  public final Message[] messages;
  public final int messagesPerConnection;
  public final int messageDelayMs;
  public final CountDownLatch quitter;
  public final Metrics metrics;
  public final int connectDelayMs;

  public CanaryConfig(ConfigObject config) {
    this.endpoint = config.strOf("endpoint", "https://aws-us-east-2.adama-platform.com/s");
    this.identities = config.stringsOf("identities", "identities was not an array");
    this.mode = config.strOf("mode", "simple");
    this.connections = config.intOf("connections", 100);
    this.documentsPerConnectionMin = config.intOf("documents_per_connection_min", 1);
    this.documentsPerConnectionMax = config.intOf("documents_per_connection_max", 2);
    this.space = config.strOf("space", "demo");
    this.keyPrefix = config.strOf("key_prefix", "");
    this.keyIdMin = config.intOf("key_id_min", 1);
    this.keyIdMax = config.intOf("key_id_max", 50);
    String[] rawMessages = config.stringsOf("messages", "messages was not an array");
    this.messagesPerConnection = config.intOf("messages_per_connection", 50);
    this.messageDelayMs = config.intOf("message_delay_ms", 250);
    this.connectDelayMs = config.intOf("connect_delay_ms", 5);
    this.messages = new Message[rawMessages.length];
    for (int k = 0; k < messages.length; k++) {
      this.messages[k] = new Message(rawMessages[k]);
    }
    this.quitter = new CountDownLatch(connections);
    this.metrics = new Metrics();
  }

  public static class Message {
    public final String channel;
    public final ObjectNode message;

    public Message(String json) {
      ObjectNode messageRaw = Json.parseJsonObject(json);
      channel = messageRaw.get("@channel").textValue();
      messageRaw.remove("@channel");
      message = messageRaw;
    }
  }

  public void blockUntilQuit() throws Exception {
    while (!quitter.await(1000, TimeUnit.MILLISECONDS)) {
      metrics.snapshot();
    }
    metrics.snapshot();
  }
}

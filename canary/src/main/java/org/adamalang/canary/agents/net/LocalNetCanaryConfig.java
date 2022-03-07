package org.adamalang.canary.agents.net;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.canary.Metrics;
import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LocalNetCanaryConfig {
  public final String source;
  public final int agents;
  public final String space;
  public final String keyPrefix;
  public final int keyIdMin;
  public final int keyIdMax;
  public final SimpleCanaryConfig.Message[] messages;
  public final int messagesPerAgent;
  public final int messageDelayMs;
  public final CountDownLatch quitter;
  public final Metrics metrics;
  public final int coreThreads;
  public final String identityFile;
  public final int port;
  public final String host;

  public LocalNetCanaryConfig(ConfigObject config) {
    this.source = config.strOf("source", "canary.adama");
    this.space = config.strOf("space", "demo");
    this.agents = config.intOf("agents", 1);
    this.coreThreads = config.intOf("core_threads", 2);
    this.keyPrefix = config.strOf("key_prefix", "");
    this.keyIdMin = config.intOf("key_id_min", 1);
    this.keyIdMax = config.intOf("key_id_max", 50);
    String[] rawMessages = config.stringsOf("messages", "messages was not an array");
    this.messagesPerAgent = config.intOf("messages_per_agent", 50);
    this.messageDelayMs = config.intOf("message_delay_ms", 250);
    this.identityFile = config.strOf("identity_file", "localhost.identity");
    this.port = config.intOf("port", 9999);
    this.host = config.strOf("host", "127.0.0.1");
    this.messages = new SimpleCanaryConfig.Message[rawMessages.length];
    for (int k = 0; k < messages.length; k++) {
      this.messages[k] = new SimpleCanaryConfig.Message(rawMessages[k]);
    }
    this.quitter = new CountDownLatch(agents);
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

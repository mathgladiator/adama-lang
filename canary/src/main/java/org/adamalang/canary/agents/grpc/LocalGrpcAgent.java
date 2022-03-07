package org.adamalang.canary.agents.grpc;

import org.adamalang.canary.agents.net.LocalNetCanaryConfig;
import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.client.contracts.SimpleEvents;
import org.adamalang.grpc.client.sm.Connection;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtClient;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LocalGrpcAgent implements SimpleEvents {
  private final Client client;
  private final LocalGrpcCanaryConfig config;
  private final NtClient who;
  private final Key key;
  private final ScheduledExecutorService executor;
  private final Random rng;
  private AtomicBoolean dedupe;
  private Connection connection;

  public LocalGrpcAgent(Client client, LocalGrpcCanaryConfig config, int agentId, ScheduledExecutorService executor) {
    this.client = client;
    this.config = config;
    this.who = new NtClient("agent" + agentId, "canary");
    int keyId = (int) (config.keyIdMin + (config.keyIdMax - config.keyIdMin + 1) * Math.random());
    this.key = new Key(config.space, config.keyPrefix + keyId);
    this.executor = executor;
    this.rng = new Random();
    this.dedupe = new AtomicBoolean(true);
  }

  public void kickOff() {
    connection = client.connect(who.agent, who.authority, key.space, key.key, "{}", this);
  }

  @Override
  public void connected() {

  }

  private void kickOffAfterFirstData() {
    AtomicInteger messages = new AtomicInteger(config.messagesPerAgent);
    AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();
    future.set(executor.scheduleAtFixedRate(() -> {
      if (messages.getAndDecrement() < 0) {
        future.get().cancel(false);
        if (dedupe.compareAndSet(true, false)) {
          config.quitter.countDown();
        }
        return;
      }
      SimpleCanaryConfig.Message msg = config.messages[rng.nextInt(config.messages.length)];
      config.metrics.messages_sent.incrementAndGet();
      long started = System.currentTimeMillis();
      connection.send(msg.channel, null, msg.message.toString(), new SeqCallback() {
        @Override
        public void success(int seq) {
          config.metrics.messages_failed.incrementAndGet();
          config.metrics.record_send_latency((int) (System.currentTimeMillis() - started));
        }

        @Override
        public void error(int code) {
          config.metrics.report_failure(code);
          config.metrics.messages_failed.incrementAndGet();
        }
      });
    }, (int) (config.messageDelayMs * Math.random()),  config.messageDelayMs, TimeUnit.MILLISECONDS));
  }

  private final AtomicBoolean firstData = new AtomicBoolean(true);

  @Override
  public void delta(String data) {
    if (firstData.compareAndSet(true, false)) {
      kickOffAfterFirstData();
    }
    config.metrics.deltas.incrementAndGet();
  }

  @Override
  public void error(int code) {
    config.metrics.stream_failed.incrementAndGet();
    config.metrics.report_failure(code);
    if (dedupe.compareAndSet(true, false)) {
      config.quitter.countDown();
    }
  }

  @Override
  public void disconnected() {

  }
}

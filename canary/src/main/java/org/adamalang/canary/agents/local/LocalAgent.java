/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.canary.agents.local;

import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LocalAgent implements Streamback {
  private final CoreService service;
  private final LocalCanaryConfig config;
  private final NtPrincipal who;
  private final Key key;
  private final ScheduledExecutorService executor;
  private final Random rng;
  private CoreStream stream;
  private final AtomicBoolean dedupe;

  public LocalAgent(CoreService service, LocalCanaryConfig config, int agentId, ScheduledExecutorService executor) {
    this.service = service;
    this.config = config;
    this.who = new NtPrincipal("agent" + agentId, "canary");
    int keyId = (int) (config.keyIdMin + (config.keyIdMax - config.keyIdMin + 1) * Math.random());
    this.key = new Key(config.space, config.keyPrefix + keyId);
    this.executor = executor;
    this.rng = new Random();
    this.dedupe = new AtomicBoolean(true);
  }

  public void kickOff() {
    service.connect(new CoreRequestContext(who, "origin", "127.0.0.1", key.key), key, "{}", null, this);
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    this.stream = stream;
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
      stream.send(msg.channel, null, msg.message.toString(), new Callback<Integer>() {
        @Override
        public void success(Integer value) {
          config.metrics.messages_failed.incrementAndGet();
          config.metrics.record_send_latency((int) (System.currentTimeMillis() - started));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          config.metrics.report_failure(ex.code);
          config.metrics.messages_failed.incrementAndGet();
        }
      });
    }, (int) (config.messageDelayMs * Math.random()), config.messageDelayMs, TimeUnit.MILLISECONDS));
  }

  @Override
  public void status(StreamStatus status) {
  }

  @Override
  public void next(String data) {
    config.metrics.deltas.incrementAndGet();
  }

  @Override
  public void failure(ErrorCodeException exception) {
    config.metrics.stream_failed.incrementAndGet();
    config.metrics.report_failure(exception.code);
    if (dedupe.compareAndSet(true, false)) {
      config.quitter.countDown();
    }
  }
}

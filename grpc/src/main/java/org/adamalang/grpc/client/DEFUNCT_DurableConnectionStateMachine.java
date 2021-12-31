package org.adamalang.grpc.client;

import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.FoundInstanceCallback;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.client.contracts.SimpleEvents;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DEFUNCT_DurableConnectionStateMachine {
  private final DurableClientBase base;

  public final String agent;
  public final String authority;
  public final String space;
  public final String key;
  private final SimpleEvents events;

  private boolean alive;
  private InstanceClient.Remote remote;
  private ArrayList<Consumer<InstanceClient.Remote>> buffer;
  private int backoff;
  private int lastSentNotice;

  public DEFUNCT_DurableConnectionStateMachine(DurableClientBase base, String agent, String authority, String space, String key, SimpleEvents events) {
    this.base = base;
    this.agent = agent;
    this.authority = authority;
    this.space = space;
    this.key = key;
    this.events = events;
    this.alive = true;
    this.buffer = null;
    this.remote = null;
    this.backoff = 1;
    this.lastSentNotice = 0;
  }

  public void start() {
    backoff = 0;
    scheduleRetryWhileInExecutor();
  }

  private void setRemoteWhileInExecutor(InstanceClient.Remote remote) {
    this.remote = remote;
    if (buffer != null) {
      for (Consumer<InstanceClient.Remote> action : buffer) {
        action.accept(remote);
      }
      buffer = null;
    }
  }

  private void executeOrQueue(Consumer<InstanceClient.Remote> action) {
    base.executor.execute(() -> {
      if (remote == null) {
        if (buffer == null) {
          buffer = new ArrayList<>();
        }
        buffer.add(action);
      } else {
        action.accept(remote);
      }
    });
  }

  public void send(String channel, String marker, String message, SeqCallback callback) {
    Consumer<InstanceClient.Remote> action = (remote) -> remote.send(channel, marker, message, callback);
    executeOrQueue(action);
  }

  public void disconnect() {
    base.executor.execute(() -> {
      alive = false;
      if (remote != null) {
        remote.disconnect();
      }
      if (lastSentNotice != 2) {
        events.disconnected();
      }
    });
  }

  private void signalHappyWhileInExecutor() {
    backoff /= 2;
  }

  private void scheduleRetryWhileInExecutor() {
    if (alive) {
      base.executor.schedule(this::retry, backoff, TimeUnit.MILLISECONDS);
      backoff ++;
      backoff *= 2;
      if (backoff > 2000) {
        backoff = 2000;
      }
    }
  }

  public void join(InstanceClient client) {
    base.executor.execute(() -> {
      signalHappyWhileInExecutor();
      DEFUNCT_DurableConnectionStateMachine.this.remote = remote;
      setRemoteWhileInExecutor(remote);
      if (lastSentNotice != 1) {
        events.connected();
        lastSentNotice = 1;
      }
    });
  }

  private void retry() {
    base.finder.find(space, key, new FoundInstanceCallback() {
      @Override
      public void found(InstanceClient client) {
        client.connect(agent, authority, space, key, new Events() {
          @Override
          public void connected(InstanceClient.Remote remote) {
            // It may be useful to think about how queues work
            base.executor.execute(() -> {
              signalHappyWhileInExecutor();
              DEFUNCT_DurableConnectionStateMachine.this.remote = remote;
              setRemoteWhileInExecutor(remote);
              if (lastSentNotice != 1) {
                events.connected();
                lastSentNotice = 1;
              }
            });
          }

          @Override
          public void delta(String data) {
            base.executor.execute(() -> {
              signalHappyWhileInExecutor();
              events.delta(data);
            });
          }

          @Override
          public void error(int code) {
            base.executor.execute(() -> {
              events.error(code);
            });
          }

          @Override
          public void disconnected() {
            base.executor.execute(() -> {
              if (lastSentNotice != 2) {
                events.disconnected();
              }
              scheduleRetryWhileInExecutor();
            });
          }
        });
      }

      @Override
      public void nope() {
        base.executor.execute(() -> {
          scheduleRetryWhileInExecutor();
        });
      }
    });
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.overlord.OverlordMetrics;

public class LiveCanaryMonitor {

  private static enum State {
    Blank,
    Created,
  }

  private static class LiveCanaryStateMachine {
    private final OverlordMetrics metrics;
    private final Client client;
    private final String canarySpace;
    private final String agent;
    private final String suffix;
    private State state;
    private String key;
    private String prior;
    private Connection connection;
    private long started;

    private class Handler implements SimpleEvents {

      @Override
      public void connected() {

      }

      @Override
      public void delta(String data) {
        // We got some data
        // TODO: if the object is over 6 hours old, then delete it
        // TODO: if the object is created, then connect to it, then send a message, then disconnect
      }

      @Override
      public void error(int code) {

      }

      @Override
      public void disconnected() {

      }
    }

    public LiveCanaryStateMachine(OverlordMetrics metrics, Client client, String canarySpace, String agent, String suffix) {
      this.metrics = metrics;
      this.client = client;
      this.canarySpace = canarySpace;
      this.agent = agent;
      this.state = State.Blank;
      this.key = null;
      this.prior = null;
      this.suffix = suffix;
    }

    public void next(NamedRunnable self, SimpleExecutor executor) {
      switch (state) {
        case Blank:
          this.started = System.currentTimeMillis();
          this.key = "canary-" + started + suffix;
          client.create("0.0.0.0", "overlord", agent, "adama", canarySpace, key, null, "{}", new Callback<Void>() {
            @Override
            public void success(Void value) {
              state = State.Created;
              executor.schedule(self, 100);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              executor.schedule(self, 1000);
            }
          });
          return;
        case Created:
          this.connection = client.connect("0.0.0.0", "overlord", agent, "adama", canarySpace, key, "{}", null, new Handler());
          return;
      }
    }
  }

  public static void kickOff(OverlordMetrics metrics, Client client, String canarySpace, String agent, int canaries) {
    SimpleExecutor executor = SimpleExecutor.create("metering-aggregator");
    for (int k = 0; k < canaries; k++) {
      final LiveCanaryStateMachine capture = new LiveCanaryStateMachine(metrics, client, canarySpace, agent, "-" + k);
      executor.schedule(new NamedRunnable("canary", "" + k) {
        LiveCanaryStateMachine state = capture;
        @Override
        public void execute() throws Exception {
          state.next(this, executor);
        }
      }, k * (1000 / canaries));
    }
  }
}

package org.adamalang.grpc.client.sm;

import org.adamalang.ErrorCodes;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.client.sm.Base;
import org.adamalang.grpc.client.sm.Label;
import org.adamalang.runtime.contracts.Key;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Connection {
  // these can be put under a base
  private final Base base;
  // these are critical to the request (i.e they are the request)
  private final String agent;
  private final String authority;
  private final Key key;
  private final SimpleEvents events;

  // the state machine registers itself with the routing table which will tell it where to go
  Runnable cancelRouting;
  boolean routingAlive;

  // the state machine is complicated because of the two stages.
  // first, we have to find a client
  // second, then using that client we have to connect
  private Label state;

  // the state machine has implicit state that we dump here. TODO: see which state can be held by various closures.
  private String target;
  private InstanceClient foundClient;

  private ArrayList<QueueAction<Remote>> buffer;
  private Remote foundRemote;
  private int backoffFindInstance;

  public Connection(Base base, String agent, String authority, String space, String key, SimpleEvents events) {
    this.base = base;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.events = events;

    this.routingAlive = true;
    this.cancelRouting = null;
    this.state = Label.NotConnected;
    this.target = null;
    this.foundClient = null;
    this.buffer = new ArrayList<>();
    this.foundRemote = null;
    this.backoffFindInstance = 1;
  }

  private void bufferOrExecute(QueueAction<Remote> action) {
    if (state == Label.Connected) {
      action.execute(foundRemote);
    } else {
      if (buffer == null) {
        buffer = new ArrayList<>();
      }
      buffer.add(action);
    }
  }

  public void send(String channel, String marker, String message, SeqCallback callback) {
    base.scheduler.execute(() -> {
      bufferOrExecute(new QueueAction<>(ErrorCodes.API_SEND_TIMEOUT, ErrorCodes.API_SEND_REJECTED) {
        @Override
        protected void executeNow(Remote remote) {
          remote.send(channel, marker, message, callback);
        }

        @Override
        protected void failure(int code) {
          callback.error(code);
        }
      });
    });
  }

  public void disconnect() {
    base.scheduler.execute(() -> {

    });
  }

  private void handle_onFoundRemote() {
    switch (state) {
      case FoundClientConnectingWait:
        state = Label.Connected;
        return;
      case FoundClientConnectingStop:
        foundRemote.disconnect();
        state = Label.NotConnected;
        return;
      case FoundClientConnectingTryNewTarget:
        foundRemote.disconnect();
        fireFindClient();
        return;
    }
  }

  private void handle_onError(int code) {

  }

  private void handle_onDisconnected() {
  }

  private void handle_onFoundClient() {
    backoffFindInstance = 0;
    switch (state) {
      case FindingClientWait:
        state = Label.FoundClientConnectingWait;
        fireConnectRemote();
        return;
      case FindingClientCancelStop:
        state = Label.FindingClientCancelTryNewTarget;
        return;
      case FindingClientCancelTryNewTarget:

        fireFindClient();
      default:
        // INVALID
    }
  }

  private void handle_onFailedFindingClient() {
    switch (state) {
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
        state = Label.FindingClientWait;
        if (backoffFindInstance < 1000) {
          base.scheduler.schedule(() -> {
            fireFindClient();
          }, backoffFindInstance, TimeUnit.MILLISECONDS);
          backoffFindInstance = (int) (backoffFindInstance + Math.random() * backoffFindInstance + 1);
        } else {
          // TODO: RAISE ISSUE AND SHUTDOWN
        }
        return;
      case FindingClientCancelStop:
        state = Label.NotConnected;
        return;
      default:
        // INVALID
    }
  }

  private void fireConnectRemote() {
    foundClient.connect(agent, authority, key.space, key.key, new Events() {
      @Override
      public void connected(Remote remote) {
        base.scheduler.execute(() -> {
          foundRemote = remote;
          handle_onFoundRemote();
        });
      }

      @Override
      public void delta(String data) {
        // TODO: handle data
        // send data3
      }

      @Override
      public void error(int code) {
        base.scheduler.execute(() -> {
          handle_onError(code);
        });
      }

      @Override
      public void disconnected() {
        base.scheduler.execute(() -> {
          handle_onDisconnected();
        });
      }
    });
  }

  private void fireFindClient() {
    state = Label.FindingClientWait;
    base.mesh.find(target, new QueueAction<>(ErrorCodes.INSTANCE_FINDER_TIMEOUT, ErrorCodes.INSTANCE_FINDER_REJECTED) {
      @Override
      protected void executeNow(InstanceClient client) {
        base.scheduler.execute(() -> {
          foundClient = client;
          handle_onFoundClient();
        });
      }

      @Override
      protected void failure(int code) {
        base.scheduler.execute(() -> {
          handle_onFailedFindingClient();
        });
      }
    });
  }

  private void handle_onKillRoutingTarget() {
    switch (state) {
      case NotConnected:
      case FindingClientCancelStop:
      case FoundClientConnectingStop:
        return; // NO-OP
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
        state = Label.FindingClientCancelStop;
        return;
      case FoundClientConnectingWait:
      case FoundClientConnectingTryNewTarget:
        state = Label.FoundClientConnectingStop;
        ;
        return;
      case Connected:
        // TODO: disconnect trigger disconnect cascade
        state = Label.WaitingForDisconnect;
        return;
      case WaitingForDisconnectCancelTryAgain:
        state = Label.WaitingForDisconnect;
        return;
      case WaitingForDisconnect:
        // NO-OP
        return;
    }
  }

  private void handle_onNewRoutingTarget() {
    switch (state) {
      case NotConnected:
        fireFindClient();
        return;
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
      case FindingClientCancelStop:
        state = Label.FindingClientCancelTryNewTarget;
        return;
      case FoundClientConnectingWait:
      case FoundClientConnectingStop:
        state = Label.FoundClientConnectingTryNewTarget;
        return;
      case FoundClientConnectingTryNewTarget:
        return; // NO-OP, just a change in the target parameter
      case Connected:
        // TODO: trigger disconnect
        state = Label.WaitingForDisconnectCancelTryAgain;
        return;
      case WaitingForDisconnect:
        state = Label.WaitingForDisconnectCancelTryAgain;
        return;
      case WaitingForDisconnectCancelTryAgain:
        // NO-OP
        return;
    }
  }

  public void onAcquireRoutingCancel(Runnable cancel) {
    if (routingAlive) {
      cancelRouting = cancel;
    } else {
      cancel.run();
    }
  }

  public void start() {
    events.connected();
    base.engine.subscribe(key, (newTarget) -> {
      base.scheduler.execute(() -> {
        if (newTarget == null) {
          if (target != null) {
            target = null;
            handle_onKillRoutingTarget();
          }
        } else {
          if (!newTarget.equals(target)) {
            target = newTarget;
            handle_onNewRoutingTarget();
          }
        }
      });
    }, this::onAcquireRoutingCancel);
  }
}

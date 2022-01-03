package org.adamalang.grpc.client.sm;

import org.adamalang.ErrorCodes;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.runtime.contracts.Key;

import java.util.ArrayList;

public class Connection {
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String agent;
  private final String authority;
  private final Key key;
  private final SimpleEvents events;

  // the state machine registers itself with the routing table which will tell it where to go
  Runnable unsubscribeFromRouting;
  boolean routingAlive;

  // the state machine is complicated because of the two stages.
  // first, we have to find a client
  // second, then using that client we have to connect
  private Label state;

  // the state machine has implicit state that we dump here. TODO: see which state can be held by
  // various closures.
  private String target;
  private InstanceClient foundClient;

  private ArrayList<QueueAction<Remote>> buffer;
  private Remote foundRemote;
  private int backoffFindInstance;

  public Connection(
      ConnectionBase base,
      String agent,
      String authority,
      String space,
      String key,
      SimpleEvents events) {
    this.base = base;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.events = events;

    this.routingAlive = true;
    this.unsubscribeFromRouting = null;
    this.state = Label.NotConnected;
    this.target = null;
    this.foundClient = null;
    this.buffer = new ArrayList<>();
    this.foundRemote = null;
    this.backoffFindInstance = 1;
  }

  public void start() {
    events.connected();
    base.engine.subscribe(
        key,
        (newTarget) -> {
          base.executor.execute(
              () -> {
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
        },
        this::onAcquireRoutingCancel);
  }

  public void send(String channel, String marker, String message, SeqCallback callback) {
    base.executor.execute(
        () -> {
          bufferOrExecute(
              new QueueAction<>(ErrorCodes.API_SEND_TIMEOUT, ErrorCodes.API_SEND_REJECTED) {
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

  public void disconnect() {
    base.executor.execute(
        () -> {
          events.disconnected();

          // this will trigger a target change to null which will take care of a great deal of
          // things
          unsubscribeFromRouting.run();
          unsubscribeFromRouting = null;
          routingAlive = false;
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
    events.error(code);
  }

  private void handle_onDisconnected() {
    events.disconnected();
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
          base.executor.schedule(
              () -> {
                fireFindClient();
              },
              backoffFindInstance);
          backoffFindInstance =
              (int) (backoffFindInstance + Math.random() * backoffFindInstance + 1);
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
    foundClient.connect(
        agent,
        authority,
        key.space,
        key.key,
        new Events() {
          @Override
          public void connected(Remote remote) {
            base.executor.execute(
                () -> {
                  foundRemote = remote;
                  handle_onFoundRemote();
                });
          }

          @Override
          public void delta(String data) {
            events.delta(data);
          }

          @Override
          public void error(int code) {
            base.executor.execute(
                () -> {
                  handle_onError(code);
                });
          }

          @Override
          public void disconnected() {
            base.executor.execute(
                () -> {
                  handle_onDisconnected();
                });
          }
        });
  }

  private void fireFindClient() {
    state = Label.FindingClientWait;
    base.mesh.find(
        target,
        new QueueAction<>(ErrorCodes.INSTANCE_FINDER_TIMEOUT, ErrorCodes.INSTANCE_FINDER_REJECTED) {
          @Override
          protected void executeNow(InstanceClient client) {
            base.executor.execute(
                () -> {
                  foundClient = client;
                  handle_onFoundClient();
                });
          }

          @Override
          protected void failure(int code) {
            base.executor.execute(
                () -> {
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
    this.base.executor.execute(
        () -> {
          if (routingAlive) {
            unsubscribeFromRouting = cancel;
          } else {
            cancel.run();
          }
        });
  }
}

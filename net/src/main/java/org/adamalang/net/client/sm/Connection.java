/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.sm;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.StreamMonitor;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.data.Key;

/**
 * a single connection for a document; given the number of outstanding events that can influence the
 * connection; this is s sizeable state machine.
 */
public class Connection {
  public static final int MAGIC_CONNECTION_QUEUE_SIZE = 16;
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String origin;
  private final String agent;
  private final String authority;
  private final Key key;
  private final SimpleEvents events;
  // the buffer of actions to execute once we have a remote
  private final ItemQueue<Remote> queue;
  // the state machine registers itself with the routing table which will tell it where to go
  Runnable unsubscribeFromRouting;
  boolean routingAlive;
  private String viewerState;
  // the state machine is complicated because of the two stages.
  // first, we have to find a client
  // second, then using that client we have to connect
  private Label state;
  // the target provided by the routing table
  private String target;
  // the client found via the finder
  private InstanceClient foundClient;
  // the remote to talk to the remote peer's connection
  private Remote foundRemote;
  // how long to wait on a failure to find an instance
  private int backoffFindInstance;
  // how long to wait on a failure to connect to a remote peer
  private int backoffConnectPeer;

  public Connection(ConnectionBase base, String origin, String agent, String authority, String space, String key, String viewerState, SimpleEvents events) {
    this.base = base;
    this.origin = origin;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.viewerState = viewerState;
    this.events = events;
    this.routingAlive = false;
    this.unsubscribeFromRouting = null;
    this.state = Label.NotConnected;
    this.target = null;
    this.foundClient = null;
    this.queue = new ItemQueue<>(base.executor, MAGIC_CONNECTION_QUEUE_SIZE, 2500);
    this.foundRemote = null;
    this.backoffFindInstance = 1;
    this.backoffConnectPeer = 1;
  }

  /** api: open the connection */
  public void open() {
    base.executor.execute(new NamedRunnable("connection-open", key.space, key.key) {
      @Override
      public void execute() throws Exception {
        if (!routingAlive) {
          base.metrics.client_state_machines_alive.up();
          routingAlive = true;
          events.connected();
          base.engine.subscribe(key, (newTarget) -> {
            base.executor.execute(new NamedRunnable("connection-found-target", newTarget) {
              @Override
              public void execute() throws Exception {
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
              }
            });
          }, (cancel) -> {
            base.executor.execute(new NamedRunnable("connection-provide-cancel-routing") {
              @Override
              public void execute() throws Exception {
                if (routingAlive) {
                  unsubscribeFromRouting = cancel;
                } else {
                  cancel.run();
                }
              }
            });
          });
        }
      }
    });
  }

  /** send the remote peer a message */
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    ItemActionMonitor.ItemActionMonitorInstance mInstance = base.metrics.client_connection_send.start();
    base.executor.execute(new NamedRunnable("connection-send", key.space, key.key, channel) {
      @Override
      public void execute() throws Exception {
        bufferOrExecute(new ItemAction<>(ErrorCodes.API_SEND_TIMEOUT, ErrorCodes.API_SEND_REJECTED, mInstance) {
          @Override
          protected void executeNow(Remote remote) {
            remote.send(channel, marker, message, callback);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  private void bufferOrExecute(ItemAction<Remote> action) {
    queue.add(action);
  }

  public void update(String viewerState) {
    ItemActionMonitor.ItemActionMonitorInstance mInstance = base.metrics.client_connection_update.start();
    base.executor.execute(new NamedRunnable("connection-update") {
      @Override
      public void execute() throws Exception {
        Connection.this.viewerState = viewerState;
        bufferOrExecute(new ItemAction<>(ErrorCodes.API_UPDATE_TIMEOUT, ErrorCodes.API_UPDATE_REJECTED, mInstance) {
          @Override
          protected void executeNow(Remote remote) {
            remote.update(viewerState);
          }

          @Override
          protected void failure(int code) {
          }
        });
      }
    });
  }

  public void canAttach(Callback<Boolean> callback) {
    ItemActionMonitor.ItemActionMonitorInstance mInstance = base.metrics.client_connection_can_attach.start();
    base.executor.execute(new NamedRunnable("connection-can-attach", key.space, key.key) {
      @Override
      public void execute() throws Exception {
        bufferOrExecute(new ItemAction<Remote>(ErrorCodes.API_CAN_ATTACH_TIMEOUT, ErrorCodes.API_CAN_ATTACH_REJECTED, mInstance) {
          @Override
          protected void executeNow(Remote item) {
            item.canAttach(callback);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    ItemActionMonitor.ItemActionMonitorInstance mInstance = base.metrics.client_connection_attach.start();
    base.executor.execute(new NamedRunnable("connection-attach", key.space, key.key, id) {
      @Override
      public void execute() throws Exception {
        bufferOrExecute(new ItemAction<Remote>(ErrorCodes.API_ATTACH_TIMEOUT, ErrorCodes.API_ATTACH_REJECTED, mInstance) {
          @Override
          protected void executeNow(Remote item) {
            item.attach(id, name, contentType, size, md5, sha384, callback);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void close() {
    base.executor.execute(new NamedRunnable("connection-close", key.space, key.key) {
      @Override
      public void execute() throws Exception {
        if (routingAlive) {
          base.metrics.client_state_machines_alive.down();
          routingAlive = false;
          events.disconnected();
          switch (state) {
            case Connected:
              state = Label.WaitingForDisconnect;
              remoteDoDisconnect();
              break;
            default:
              state = Label.NotConnected;
          }

          // this will trigger a target change to null which will take care of a great deal of
          // things
          if (unsubscribeFromRouting != null) {
            unsubscribeFromRouting.run();
            unsubscribeFromRouting = null;
          }
        }
      }
    });
  }

  private void remoteDoDisconnect() {
    queue.unready();
    if (foundRemote != null) {
      foundRemote.disconnect();
      foundRemote = null;
    }
  }

  private void handle_onFoundRemote() {
    backoffConnectPeer = 1;
    switch (state) {
      case FoundClientConnectingWait:
        state = Label.Connected;
        queue.ready(foundRemote);
        return;
      default:
        remoteDoDisconnect();
        return;
    }
  }

  private void handle_onError(int code) {
    remoteDoDisconnect();
    if (routingAlive) {
      base.metrics.client_state_machines_alive.down();
      routingAlive = false;
      if (unsubscribeFromRouting != null) {
        unsubscribeFromRouting.run();
      }
    }
    state = Label.Failed;
    events.error(code);
  }

  private void handle_onDisconnected() {
    queue.unready();
    foundRemote = null;
    switch (state) {
      case FoundClientConnectingWait:
      case FoundClientConnectingTryNewTarget:
      case ConnectedStoppingPleaseReconnect:
      case Connected:
        if (backoffConnectPeer < 2000) {
          state = Label.FindingClientWait;
          base.executor.schedule(new NamedRunnable("connection-retry") {
            @Override
            public void execute() throws Exception {
              fireFindClient();
            }
          }, backoffConnectPeer);
          backoffConnectPeer = (int) (backoffConnectPeer + Math.random() * backoffConnectPeer + 1);
        } else {
          base.metrics.client_too_many_failures_disconnected_by_peer.run();
          handle_onError(ErrorCodes.STATE_MACHINE_UNABLE_TO_RECONNECT);
        }
        return;
      case ConnectedStopping:
      case FoundClientConnectingStop:
      case WaitingForDisconnect:
        state = Label.NotConnected;
        return;
    }
  }

  private void handle_onFoundClient() {
    backoffFindInstance = 0;
    switch (state) {
      case FindingClientWait:
        state = Label.FoundClientConnectingWait;
        fireConnectRemote();
        return;
      case FindingClientCancelStop:
        state = Label.NotConnected;
        return;
      case FindingClientCancelTryNewTarget:
        fireFindClient();
        return;
      default:
        // nothing to do
    }
  }

  private void handle_onFailedFindingClient() {
    if (backoffFindInstance < 1000) {
      base.executor.schedule(new NamedRunnable("connection-failed-retry") {
        @Override
        public void execute() throws Exception {
          switch (state) {
            case FindingClientWait:
            case FindingClientCancelTryNewTarget:
              fireFindClient();
              return;
            case FindingClientCancelStop:
              state = Label.NotConnected;
              return;
          }
        }
      }, backoffFindInstance);
      backoffFindInstance = (int) (backoffFindInstance + Math.random() * backoffFindInstance + 1);
    } else {
      base.metrics.client_too_many_failures_finding_client.run();
      handle_onError(ErrorCodes.STATE_MACHINE_TOO_MANY_FAILURES);
    }
  }

  private void fireConnectRemote() {
    StreamMonitor.StreamMonitorInstance mInstance = base.metrics.client_open_document.start();
    foundClient.connect(origin, agent, authority, key.space, key.key, viewerState, new Events() {
      @Override
      public void connected(Remote remote) {
        mInstance.progress();
        base.executor.execute(new NamedRunnable("connection-connected") {
          @Override
          public void execute() throws Exception {
            foundRemote = remote;
            handle_onFoundRemote();
          }
        });
      }

      @Override
      public void delta(String data) {
        mInstance.progress();
        events.delta(data);
      }

      @Override
      public void error(int code) {
        mInstance.failure(code);
        base.executor.execute(new NamedRunnable("connection-error") {
          @Override
          public void execute() throws Exception {
            handle_onError(code);
          }
        });
      }

      @Override
      public void disconnected() {
        mInstance.finish();
        base.executor.execute(new NamedRunnable("connection-disconnected") {
          @Override
          public void execute() throws Exception {
            handle_onDisconnected();
          }
        });
      }
    });
  }

  private void fireFindClient() {
    state = Label.FindingClientWait;
    base.mesh.find(target, new Callback<InstanceClient>() {
      @Override
      public void success(InstanceClient client) {
        base.executor.execute(new NamedRunnable("connection-found-client") {
          @Override
          public void execute() throws Exception {
            foundClient = client;
            handle_onFoundClient();
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        base.executor.execute(new NamedRunnable("connection-failed-finding-client", Integer.toString(ex.code)) {
          @Override
          public void execute() throws Exception {
            handle_onFailedFindingClient();
          }
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
        state = Label.ConnectedStopping;
        remoteDoDisconnect();
        return;
      case ConnectedStoppingPleaseReconnect:
        state = Label.ConnectedStopping;
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
      case FoundClientConnectingTryNewTarget:
        state = Label.FoundClientConnectingTryNewTarget;
        return;
      case Connected:
        remoteDoDisconnect();
        return;
      case ConnectedStopping:
        state = Label.ConnectedStoppingPleaseReconnect;
        return;
      case WaitingForDisconnect:
        // just ignore it
        return;
    }
  }

  @Override
  public String toString() {
    return "state=" + state;
  }
}

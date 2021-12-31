package org.adamalang.grpc.client.active;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.contracts.Remote;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;

import java.util.ArrayList;
import java.util.function.Consumer;

/** We group Connections (a single user connection to a document) within a StateMachine and then bind that StateMachine to a single host. */
public class Connection {
  // who is connecting
  private final NtClient who;

  // what events should we hand over to the client
  private Events events;

  // how we can manipulate the state on the remote peer
  private Remote remote;

  // if we are disconnected, then we buffer actions
  private ArrayList<QueueAction<Remote>> buffer;

  // have we issued an inflight connection to a client
  private boolean inflightConnect;

  // if we have issued an connection to a client, book-keep which id was used
  private long inflightId;

  public Connection(NtClient who) {
    this.who = who;
    this.events = null;
    this.remote = null;
    this.buffer = null;
    this.inflightConnect = false;
    this.inflightId = 0;
  }

  public void bind(Events events) {
    this.events = events;
  }

  /** a connection was established */
  public void connected(Remote remote) {
    inflightConnect = false;
    this.remote = remote;
    if (buffer != null) {
      for (QueueAction<Remote> action : buffer) {
        action.execute(remote);
      }
      buffer = null;
    }
  }

  /** a connection was removed, let's nuke the remote */
  public void disconnected() {
    inflightConnect = false;
    this.remote = null;
  }

  /** interact to the document /w the buffer */
  public void executeOrBuffer(DirectoryBase base, QueueAction<Remote> action) {
    if (remote == null) {
      if (buffer == null) {
        buffer = new ArrayList<>(10);
      }
      if (buffer.size() >= 10) {
        action.killDueToReject();
      } else {
        buffer.add(action);
      }
    } else {
      action.execute(remote);
    }
  }

  /** interact with document if is connected */
  public void executeIfConnected(Consumer<Remote> action) {
    if (remote != null) {
      action.accept(remote);
    }
  }

  /** should we issue a connect request */
  public boolean shouldSendConnect() {
    if (inflightConnect) {
      return false;
    }
    return remote == null;
  }

  /** send a connect on the given client */
  public void sendConnect(InstanceClient client, Key key) {
    inflightConnect = true;
    // OK, we have a problem with sharing the events. If I force a disconnect while a connect is inflight, then the events is shared between two clients
    inflightId = client.connect(who.agent, who.authority, key.space, key.key, events);
  }

  /** represents a disconnect from the system rather than the user */
  public void forceDisconnect(InstanceClient client) {
    if (remote != null) {
      remote.disconnect();
      remote = null;
    } else if (inflightConnect) {
      inflightConnect = false;
      if (client != null) {
        client.forceDisconnect(inflightId);
      }
    }
  }
}

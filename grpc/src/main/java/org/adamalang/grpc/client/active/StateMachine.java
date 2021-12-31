package org.adamalang.grpc.client.active;

import org.adamalang.ErrorCodes;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/** represents the state machine for this host to maintain state on another host */
public class StateMachine {
  private final DirectoryBase base;
  private final Key key;
  private final ArrayList<Connection> connections;
  private InstanceClient client;

  public StateMachine(DirectoryBase base, Key key) {
    this.base = base;
    this.key = key;
    this.connections = new ArrayList<>();
    this.client = null;
  }

  public void create(NtClient who, String entropy, String arg, CreateCallback callback) {
    // request an instance client
  }

  private void kill(Connection connection) {
    connections.remove(connection);
    // TODO: schedule a potential inspection
  }

  public void connect(NtClient who, Events events) {
    Connection connection = new Connection(who);
    // ensure all the events which touch the connection run insde the executor and manage the state here
    connection.bind(new Events() {
      @Override
      public void connected(Remote remote) {
        base.scheduler.execute(() -> {
          connection.connected(remote);
        });
      }

      @Override
      public void delta(String data) {
        events.delta(data);
      }

      @Override
      public void error(int code) {
        events.error(code);
        base.scheduler.execute(() -> {
          kill(connection);
        });
      }

      @Override
      public void disconnected() {
        base.scheduler.execute(() -> {
          connection.disconnected();
          scheduleRetry();
        });
      }
    });

    events.connected(new Remote() {
      @Override
      public void canAttach(AskAttachmentCallback callback) {
        base.scheduler.execute(() -> {
          connection.executeOrBuffer(base, new QueueAction<>() {
            @Override
            protected void executeNow(Remote remote) {
              remote.canAttach(callback);
            }

            @Override
            protected void rejected() {
              callback.error(ErrorCodes.API_CAN_ATTACH_REJECTED);
            }

            @Override
            protected void timeOut() {
              callback.error(ErrorCodes.API_CAN_ATTACH_TIMEOUT);
            }
          });
        });
      }

      @Override
      public void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback) {
        base.scheduler.execute(() -> {
          connection.executeOrBuffer(base, new QueueAction<>() {
            @Override
            protected void executeNow(Remote remote) {
              remote.attach(id, name, contentType, size, md5, sha384, callback);
            }

            @Override
            protected void rejected() {
              callback.error(ErrorCodes.API_ATTACH_REJECTED);
            }

            @Override
            protected void timeOut() {
              callback.error(ErrorCodes.API_ATTACH_TIMEOUT);
            }
          });
        });
      }

      @Override
      public void send(String channel, String marker, String message, SeqCallback callback) {
        base.scheduler.execute(() -> {
          connection.executeOrBuffer(base, new QueueAction<>() {
            @Override
            protected void executeNow(Remote remote) {
              remote.send(channel, marker, message, callback);
            }

            @Override
            protected void rejected() {
              callback.error(ErrorCodes.API_SEND_REJECTED);
            }

            @Override
            protected void timeOut() {
              callback.error(ErrorCodes.API_SEND_TIMEOUT);
            }
          });
        });
      }

      @Override
      public void disconnect() {
        base.scheduler.execute(() -> {
          kill(connection);
          connection.executeIfConnected((remote) -> remote.disconnect());
        });
      }
    });

    connections.add(connection);

    if (client != null) {
      connection.sendConnect(client, key);
    } else {
      scheduleRetry();
    }
  }

  private void attemptConnectAll() {
      if (client != null) {
        for (Connection connection : connections) {
          if (connection.shouldSendConnect()) {
            connection.sendConnect(client, key);
          }
        }
      }
  }

  private void scheduleRetry() {
    base.scheduler.schedule(this::attemptConnectAll, 100, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    if (client != null) {
      for (Connection connection : connections) {
        connection.forceDisconnect(client);
      }
    }
  }

  public void setClient(InstanceClient newClient) {
    if (client == null) {
      client = newClient;
      if (newClient != null) {
        attemptConnectAll();
      }
    } else if (client != newClient) {
      for (Connection connection : connections) {
        connection.forceDisconnect(client);
      }
      client = newClient;
      if (newClient != null) {
        scheduleRetry();
      }
    }
  }
}

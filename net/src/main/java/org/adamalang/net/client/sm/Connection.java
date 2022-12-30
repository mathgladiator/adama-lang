/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.sm;

import org.adamalang.ErrorCodes;
import org.adamalang.ErrorTable;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Assumes a fixed routing decision and heads until first failure */
public class Connection implements AdamaStream {
  private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String ip;
  private final String origin;
  private final String agent;
  private final String authority;
  private final Key key;
  private final String assetKey;
  private final SimpleEvents events;
  private String viewerState;
  // the buffer of actions to execute once we have a remote
  private final ItemQueue<Remote> queue;
  private int backoff;
  private boolean closed;
  private int timeoutMilliseconds;
  private int waitingInError;
  private boolean connectedOnce;

  public Connection(ConnectionBase base, String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, int timeoutMilliseconds, SimpleEvents events) {
    this.base = base;
    this.ip = ip;
    this.origin = origin;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.viewerState = viewerState;
    this.assetKey = assetKey;
    this.events = events;
    this.timeoutMilliseconds = timeoutMilliseconds;
    this.queue = new ItemQueue<>(base.executor, base.config.getConnectionQueueSize(), base.config.getConnectionQueueTimeoutMS());
    this.backoff = 1;
    this.closed = false;
    this.waitingInError = 0;
    this.connectedOnce = false;
    base.metrics.client_state_machines_alive.up();
  }

  private void signalError(int error) {
    if (!closed) {
      base.metrics.client_state_machines_alive.down();
      closed = true;
    }
    events.error(error);
  }

  private void handleError(int error) {
    if (ErrorTable.INSTANCE.shouldRetry(error)) {
      if (waitingInError > timeoutMilliseconds) {
        base.metrics.lcsm_timeout.run();
        LOG.error("connection-timeout;original=" + error);
        signalError(ErrorCodes.NET_LCSM_TIMEOUT);
        return;
      }
      backoff = Math.min((int) (backoff + Math.random() * backoff + 1), 2000);
      waitingInError += backoff;
      base.executor.schedule(new NamedRunnable("lcsm-handle-error") {
        @Override
        public void execute() throws Exception {
          if (!closed) {
            open();
          }
        }
      }, backoff);
    } else {
      signalError(error);
    }
  }

  public void open(String machine) {
    base.mesh.find(machine, new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.connect(ip, origin, agent, authority, key.space, key.key, viewerState, assetKey, new Events() {
          @Override
          public void connected(Remote remote) {
            base.executor.execute(new NamedRunnable("lcsm-connected") {
              @Override
              public void execute() throws Exception {
                if (closed) {
                  remote.disconnect();
                } else {
                  waitingInError = 0;
                  queue.ready(remote);
                  if (!connectedOnce) {
                    events.connected();
                    connectedOnce = true;
                  }
                }
              }
            });
          }

          @Override
          public void delta(String data) {
            events.delta(data);
          }



          @Override
          public void error(int code) {
            base.executor.execute(new NamedRunnable("lcsm-connected") {
              @Override
              public void execute() throws Exception {
                queue.unready();
                handleError(code);
              }
            });
          }

          @Override
          public void disconnected() {
            error(ErrorCodes.NET_LCSM_DISCONNECTED_PREMATURE);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        handleError(ex.code);
      }
    });
  }

  public void open() {
    base.router.get(key, new RoutingCallback() {
      @Override
      public void onRegion(String region) {
        handleError(ErrorCodes.NET_LCSM_WRONG_REGION);
      }

      @Override
      public void onMachine(String machine) {
        if (machine == null) {
          handleError(ErrorCodes.NET_LCSM_NO_MACHINE_FOUND);
          return;
        }
        open(machine);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        handleError(ex.code);
      }
    });
  }

  @Override
  public void update(String newViewerState) {
    ItemActionMonitor.ItemActionMonitorInstance instance = base.metrics.lcsm_connection_update.start();
    base.executor.execute(new NamedRunnable("lcsm-update") {
      @Override
      public void execute() throws Exception {
        viewerState = newViewerState; // TODO: merge? or set?
        queue.add(new ItemAction<>(ErrorCodes.NET_LCSM_UPDATE_TIMEOUT, ErrorCodes.NET_LCSM_UPDATE_REJECTED, instance) {
          @Override
          protected void executeNow(Remote remote) {
            remote.update(viewerState);
          }

          @Override
          protected void failure(int code) {
            // Don't care
          }
        });
      }
    });
  }

  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    ItemActionMonitor.ItemActionMonitorInstance instance = base.metrics.lcsm_connection_update.start();
    base.executor.execute(new NamedRunnable("lcsm-send") {
      @Override
      public void execute() throws Exception {
        queue.add(new ItemAction<>(ErrorCodes.NET_LCSM_SEND_TIMEOUT, ErrorCodes.NET_LCSM_SEND_REJECTED, instance) {
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

  @Override
  public void canAttach(Callback<Boolean> callback) {
    ItemActionMonitor.ItemActionMonitorInstance instance = base.metrics.lcsm_connection_can_attach.start();
    base.executor.execute(new NamedRunnable("lcsm-can-attach") {
      @Override
      public void execute() throws Exception {
        queue.add(new ItemAction<>(ErrorCodes.NET_LCSM_CAN_ATTACH_TIMEOUT, ErrorCodes.NET_LCSM_CAN_ATTACH_REJECTED, instance) {
          @Override
          protected void executeNow(Remote remote) {
            remote.canAttach(callback);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    ItemActionMonitor.ItemActionMonitorInstance instance = base.metrics.lcsm_connection_attach.start();
    base.executor.execute(new NamedRunnable("lcsm-attach") {
      @Override
      public void execute() throws Exception {
        queue.add(new ItemAction<>(ErrorCodes.NET_LCSM_ATTACH_TIMEOUT, ErrorCodes.NET_LCSM_ATTACH_REJECTED, instance) {
          @Override
          protected void executeNow(Remote remote) {
            remote.attach(id, name, contentType, size, md5, sha384, callback);
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  @Override
  public void close() {
    base.executor.execute(new NamedRunnable("lcsm-close") {
      @Override
      public void execute() throws Exception {
        if (!closed) {
          closed = true;
          base.metrics.client_state_machines_alive.down();
          events.disconnected();
        }
        Remote remote = queue.nuke();
        if (remote != null) {
          remote.disconnect();
        }
      }
    });
  }
}

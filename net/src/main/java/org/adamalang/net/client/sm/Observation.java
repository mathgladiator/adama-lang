/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
import org.adamalang.net.client.contracts.*;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.ConnectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Assumes a fixed routing decision and heads until first failure */
public class Observation implements AdamaStream {
  private static final Logger LOG = LoggerFactory.getLogger(Observation.class);
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String ip;
  private final String origin;
  private final String agent;
  private final String authority;
  private final Key key;
  private final SimpleEvents events;
  private String viewerState;
  // the buffer of actions to execute once we have a remote
  private final ItemQueue<ReadOnlyRemote> queue;
  private int backoff;
  private boolean closed;
  private int timeoutMilliseconds;
  private int waitingInError;
  private boolean connectedOnce;
  private String machineToAsk;

  public Observation(ConnectionBase base, String machineToAsk, String ip, String origin, String agent, String authority, String space, String key, String viewerState, int timeoutMilliseconds, SimpleEvents events) {
    this.base = base;
    this.ip = ip;
    this.origin = origin;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.viewerState = viewerState;
    this.events = events;
    this.timeoutMilliseconds = timeoutMilliseconds;
    this.queue = new ItemQueue<>(base.executor, base.config.getConnectionQueueSize(), base.config.getConnectionQueueTimeoutMS());
    this.backoff = 1;
    this.closed = false;
    this.waitingInError = 0;
    this.connectedOnce = false;
    this.machineToAsk = machineToAsk;
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

  public void open() {
    base.mesh.find(machineToAsk, new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.observe(ip, origin, agent, authority, key.space, key.key, viewerState, new ReadOnlyEvents() {
          @Override
          public void connected(ReadOnlyRemote remote) {
            base.executor.execute(new NamedRunnable("lcsm-connected-ro") {
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

  @Override
  public void update(String newViewerState, Callback<Void> callback) {
    ItemActionMonitor.ItemActionMonitorInstance instance = base.metrics.lcsm_connection_update.start();
    base.executor.execute(new NamedRunnable("lcsm-update") {
      @Override
      public void execute() throws Exception {
        viewerState = newViewerState; // TODO: merge? or set?
        queue.add(new ItemAction<>(ErrorCodes.NET_LCSM_UPDATE_TIMEOUT, ErrorCodes.NET_LCSM_UPDATE_REJECTED, instance) {
          @Override
          protected void executeNow(ReadOnlyRemote remote) {
            remote.update(viewerState, callback);
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
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    callback.failure(new ErrorCodeException(0));
  }

  @Override
  public void password(String password, Callback<Integer> callback) {
    callback.failure(new ErrorCodeException(0));
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    callback.failure(new ErrorCodeException(0));
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    callback.failure(new ErrorCodeException(0));
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
        ReadOnlyRemote remote = queue.nuke();
        if (remote != null) {
          remote.disconnect();
        }
      }
    });
  }
}

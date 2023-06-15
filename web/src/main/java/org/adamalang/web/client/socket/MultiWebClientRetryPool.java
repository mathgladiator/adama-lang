package org.adamalang.web.client.socket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

/** a pool of clients connected to one endpoint; this manages the lifecycle of the connection and does retry */
public class MultiWebClientRetryPool {
  private static final Logger LOG = LoggerFactory.getLogger(MultiWebClientRetryPool.class);
  private final Random rng;
  private final WebClientBase base;
  private final MultiWebClientRetryPoolMetrics metrics;
  private final MultiWebClientRetryPoolConfig config;
  private final AtomicBoolean alive;
  private final String endpoint;
  private final SimpleExecutor executor;
  private final WebSocketPoolEndpoint[] connections;

  public MultiWebClientRetryPool(SimpleExecutor executor, WebClientBase base, MultiWebClientRetryPoolMetrics metrics, MultiWebClientRetryPoolConfig config, String endpoint) {
    this.executor = executor;
    this.base = base;
    this.metrics = metrics;
    this.config = config;
    this.endpoint = endpoint;
    this.rng = new Random();
    this.alive = new AtomicBoolean(true);
    this.connections = new WebSocketPoolEndpoint[config.connectionCount];
    for (int k = 0; k < connections.length; k++) {
      connections[k] = new WebSocketPoolEndpoint(config.maxInflight, config.findTimeout);
    }
  }

  private class WebSocketPoolEndpoint {
    private final ItemQueue<WebClientConnection> queue;

    private WebSocketPoolEndpoint(int bound, int timeout) {
      this.queue = new ItemQueue<>(executor, bound, timeout);
      base.open(endpoint, new WebLifecycle() {
        private int backoff = 0;
        @Override
        public void connected(WebClientConnection connection) {
          metrics.inflight.up();
          backoff = 0;
          executor.execute(new NamedRunnable("connected") {
            @Override
            public void execute() throws Exception {
              queue.ready(connection);
            }
          });
        }

        @Override
        public void ping(int latency) {
          if (latency > 500) {
            metrics.slow.run();
          }
        }

        @Override
        public void failure(Throwable t) {
          LOG.error("mwcr-failure", t);
          metrics.failure.run();
        }

        @Override
        public void disconnected() {
          metrics.inflight.down();
          metrics.disconnected.run();
          WebLifecycle self = this;
          if (alive.get()) {
            backoff = Math.min(config.maxBackoff, backoff > 0 ? (rng.nextInt(backoff) + backoff + 1) : 1);
            executor.execute(new NamedRunnable("disconnected") {
              @Override
              public void execute() throws Exception {
                queue.unready();
                executor.schedule(new NamedRunnable("retry") {
                  @Override
                  public void execute() throws Exception {
                    if (alive.get()) {
                      base.open(endpoint, self);
                    }
                  }
                }, backoff);
              }
            });
          }
        }
      });
    }
  }

  public void get(Callback<WebClientConnection> callback) {
    int id = rng.nextInt(connections.length); // TODO: consider power of two load balancing
    connections[id].queue.add(new ItemAction<>(ErrorCodes.WEBBASE_CONNECT_TIMEOUT, ErrorCodes.WEBBASE_CONNECT_REJECTED, metrics.queue.start()) {
      @Override
      protected void executeNow(WebClientConnection item) {
        callback.success(item);
      }

      @Override
      protected void failure(int code) {
        callback.failure(new ErrorCodeException(code));
      }
    });
  }

  public <T> void requestResponse(ObjectNode request, Function<ObjectNode, T> transform, Callback<T> callback) {
    get(new Callback<WebClientConnection>() {
      @Override
      public void success(WebClientConnection connection) {
        connection.requestResponse(request, transform, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public <C, T> void requestStream(ObjectNode request, BiFunction<WebClientConnection, Integer, C> shared, Function<ObjectNode, T> transform, Callback<C> created, Stream<T> streamback) {
    get(new Callback<WebClientConnection>() {
      @Override
      public void success(WebClientConnection connection) {
        int id = connection.execute(request, new WebJsonStream() {
          @Override
          public void data(int connection, ObjectNode node) {
            streamback.next(transform.apply(node));
          }

          @Override
          public void complete() {
            streamback.complete();
          }

          @Override
          public void failure(int code) {
            streamback.failure(new ErrorCodeException(code));
          }
        });
        created.success(shared.apply(connection, id));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        created.failure(ex);
        streamback.failure(ex);
      }
    });
  }

  public <C, T> void requestStream(ObjectNode request,  Function<ObjectNode, T> transform, Stream<T> streamback) {
    get(new Callback<WebClientConnection>() {
      @Override
      public void success(WebClientConnection connection) {
        int id = connection.execute(request, new WebJsonStream() {
          @Override
          public void data(int connection, ObjectNode node) {
            streamback.next(transform.apply(node));
          }

          @Override
          public void complete() {
            streamback.complete();
          }

          @Override
          public void failure(int code) {
            streamback.failure(new ErrorCodeException(code));
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        streamback.failure(ex);
      }
    });
  }

  public void shutdown() {
    alive.set(false);
    executor.execute(new NamedRunnable("shutdown") {
      @Override
      public void execute() throws Exception {
        for (int k = 0; k < connections.length; k++) {
          WebClientConnection connection = connections[k].queue.nuke();
          if (connection != null) {
            connection.close();
          }
        }
      }
    });
  }
}

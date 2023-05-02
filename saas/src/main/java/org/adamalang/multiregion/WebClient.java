/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.multiregion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/** A web client for reaching over the internet and talking to Adama */
public class WebClient implements WebLifecycle {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebClient.class);
  private final WebClientBase base;
  private final String endpoint;
  private final SimpleExecutor executor;
  private final ItemQueue<WebClientConnection> connection;
  private int backoff;

  public WebClient(WebClientBase base, String endpoint) {
    this.base = base;
    this.endpoint = endpoint;
    this.executor = SimpleExecutor.create("web-client");
    this.connection = new ItemQueue<>(executor, 128, 2500);
    this.backoff = 1;
  }

  public class WebStreamConnectionId {
    public final WebClientConnection connection;
    public final int id;

    public WebStreamConnectionId(WebClientConnection connection, int id) {
      this.connection = connection;
      this.id = id;
    }
  }

  public class WebStream implements AdamaStream {
    private final ItemQueue<WebStreamConnectionId> queue;

    public WebStream() {
      this.queue = new ItemQueue<>(executor, 32, 1000);
    }

    private void enqueue(Consumer<WebStreamConnectionId> action, Callback<?> callback) {
      executor.execute(new NamedRunnable("web-stream-enqueue") {
        @Override
        public void execute() throws Exception {
          queue.add(new ItemAction<>(ErrorCodes.WEB_CLIENT_STREAM_TIMEOUT, ErrorCodes.WEB_CLIENT_STREAM_REJECTED, null) {
            @Override
            protected void executeNow(WebStreamConnectionId connect) {
              action.accept(connect);
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
    public void update(String newViewerState) {
      enqueue((connection) -> {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/update");
        request.put("connection", connection.id);
        request.set("viewer-state", Json.parseJsonObject(newViewerState));
      }, Callback.DONT_CARE_VOID);
    }

    @Override
    public void send(String channel, String marker, String message, Callback<Integer> callback) {
      enqueue((connection) -> {
        ObjectNode request = Json.newJsonObject();
        request.put("method", marker != null ? "connection/send-once" : "connection/send");
        request.put("connection", connection.id);
        request.put("channel", channel);
        if (marker != null) {
          request.put("dedupe", marker);
        }
        request.set("message", Json.parseJsonObject(message));
        connection.connection.execute(request, new WebJsonStream() {
          @Override
          public void data(int connection, ObjectNode node) {
            callback.success(node.get("seq").intValue());
          }

          @Override
          public void complete() {
          }

          @Override
          public void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }, callback);
    }

    @Override
    public void canAttach(Callback<Boolean> callback) {
      enqueue((connection) -> {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/can-attach");
        request.put("connection", connection.id);
        // TODO:
      }, callback);
    }

    @Override
    public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
      enqueue((connection) -> {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/attach");
        request.put("connection", connection.id);
        request.put("filename", name);
        request.put("content-type", contentType);
        request.put("size", size);
        request.put("digest-md5", md5);
        request.put("digest-sha384", sha384);
        // TODO:
      }, callback);
    }

    @Override
    public void close() {
      enqueue((connection) -> {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/end");
        request.put("connection", connection.id);

      }, Callback.DONT_CARE_VOID);
    }
  };

  public void directSend(String identity, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {

  }

  public AdamaStream connect(String identity, String space, String key, String viewerState, SimpleEvents events) {
    WebStream webStream = new WebStream();
    executor.execute(new NamedRunnable("web-client-connect") {
      @Override
      public void execute() throws Exception {
        connection.add(new ItemAction<>(ErrorCodes.WEB_CLIENT_CONNECTION_TIMEOUT, ErrorCodes.WEB_CLIENT_CONNECTION_REJECTED, null) {
          @Override
          protected void executeNow(WebClientConnection connection) {
            ObjectNode request = Json.newJsonObject();
            request.put("identity", identity);
            request.put("space", space);
            request.put("key", key);
            request.put("viewer-state", viewerState);
            int id = connection.execute(request, new WebJsonStream() {
              boolean sentConnected = false;
              @Override
              public void data(int connection, ObjectNode node) {
                if (!sentConnected) {
                  events.connected();
                  sentConnected = true;
                }
                events.delta(node.get("delta").toString());
              }

              @Override
              public void complete() {
                events.disconnected();
              }

              @Override
              public void failure(int code) {
                events.error(code);
              }
            });
            webStream.queue.ready(new WebStreamConnectionId(connection, id));
          }

          @Override
          protected void failure(int code) {
            events.error(code);
          }
        });
      }
    });
    return webStream;
  }

  @Override
  public void connected(WebClientConnection conn) {
    executor.execute(new NamedRunnable("webclient") {
      @Override
      public void execute() throws Exception {
        connection.ready(conn);
        backoff = 1;
      }
    });
  }

  @Override
  public void ping(int latency) {
  }

  @Override
  public void failure(Throwable t) {
    LOGGER.error("web-client-failure:", t);
  }

  @Override
  public void disconnected() {
    executor.execute(new NamedRunnable("webclient") {
      @Override
      public void execute() throws Exception {
        connection.unready();
        executor.schedule(new NamedRunnable("webclient-retry") {
          @Override
          public void execute() throws Exception {
            open();
          }
        }, backoff);
        backoff = Math.max(2500, (int) (backoff + Math.random() * backoff + 1));
      }
    });
  }

  public void open() {
    base.open(endpoint, this);
  }
}

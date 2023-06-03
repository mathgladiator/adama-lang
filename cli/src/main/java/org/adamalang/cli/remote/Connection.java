/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/** a single connection to a remote Web Proxy host */
public class Connection implements AutoCloseable {
  private final WebClientConnection connection;

  public Connection(WebClientConnection connection) {
    this.connection = connection;
  }

  public void stream(ObjectNode request, BiConsumer<Integer, ObjectNode> stream) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Exception> failure = new AtomicReference<>(null);
    connection.execute(request, new WebJsonStream() {
      @Override
      public void data(int cId, ObjectNode node) {
        stream.accept(cId, node);
      }

      @Override
      public void complete() {
        latch.countDown();
      }

      @Override
      public void failure(int code) {
        failure.set(new ErrorCodeException(code));
        latch.countDown();
      }
    });
    latch.await(60000, TimeUnit.MILLISECONDS);
    if (failure.get() != null) {
      throw failure.get();
    }
  }

  /** An object that is either an ObjectNode or an Exception */
  public static class IdObject {
    public final int id;
    public final Object value;

    public IdObject(int id, Object value) {
      this.id = id;
      this.value = value;
    }

    public ObjectNode node() throws Exception {
      if (value instanceof ObjectNode) {
        return (ObjectNode) value;
      }
      throw (Exception) value;
    }
  }

  /** use a blocking queue to interact with a stream */
  public BlockingDeque<IdObject> stream_queue(ObjectNode request) {
    BlockingDeque<IdObject> queue = new LinkedBlockingDeque<>();
    connection.execute(request, new WebJsonStream() {
      @Override
      public void data(int cId, ObjectNode node) {
        queue.offer(new IdObject(cId, node));
      }

      @Override
      public void complete() {
      }

      @Override
      public void failure(int code) {
        queue.offer(new IdObject(-1, new ErrorCodeException(code)));
      }
    });
    return queue;
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  public ObjectNode execute(ObjectNode request) throws Exception {
    AtomicReference<Object> value = new AtomicReference<>(null);
    CountDownLatch latch = new CountDownLatch(1);
    connection.execute(request, new WebJsonStream() {
      @Override
      public void data(int cId, ObjectNode node) {
        value.set(node);
      }

      @Override
      public void complete() {
        latch.countDown();
      }

      @Override
      public void failure(int code) {
        value.set(new ErrorCodeException(code));
        latch.countDown();
      }
    });
    if (latch.await(30000, TimeUnit.MILLISECONDS)) {
      if (value.get() != null) {
        if (value.get() instanceof ObjectNode) {
          return (ObjectNode) value.get();
        } else {
          throw (Exception) value.get();
        }
      }
      return Json.newJsonObject();
    } else {
      throw new Exception("timed out");
    }
  }
}

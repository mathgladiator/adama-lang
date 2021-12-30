/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** a single connection to a remote Web Proxy host */
public class Connection implements AutoCloseable {
  private final AtomicInteger idgen;
  private final Channel channel;
  private final ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks;

  public Connection(Channel channel, ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks) {
    this.idgen = new AtomicInteger(0);
    this.channel = channel;
    this.callbacks = callbacks;
  }

  public void stream(ObjectNode request, Consumer<ObjectNode> stream) throws Exception {
    long id = idgen.incrementAndGet();
    request.put("id", Long.toString(id));
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Exception> value = new AtomicReference<>(null);
    callbacks.put(id, (result, done) -> {
      if (result instanceof ObjectNode) {
        stream.accept((ObjectNode) result);
        if (done) {
          latch.countDown();
        }
      } else if (result instanceof Exception) {
        value.set((Exception) result);
      }
    });
    channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
    latch.await();
    if (value.get() != null) {
      throw value.get();
    }
  }

  public long open(ObjectNode request, Consumer<ObjectNode> stream, Consumer<Exception> error) throws Exception {
    long id = idgen.incrementAndGet();
    request.put("id", Long.toString(id));
    callbacks.put(id, (result, done) -> {
      if (result instanceof ObjectNode) {
        stream.accept((ObjectNode) result);
      } else if (result instanceof Exception) {
        error.accept((Exception) result);
      }
    });
    channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
    return id;
  }

  @Override
  public void close() throws Exception {
    channel.close();
  }

  public ObjectNode execute(ObjectNode request) throws Exception {
    long id = idgen.incrementAndGet();
    request.put("id", Long.toString(id));
    CountDownLatch latch = new CountDownLatch(1);

    AtomicReference<Object> value = new AtomicReference<>(null);
    try {
      callbacks.put(id, (result, done) -> {
        value.set(result);
        latch.countDown();
      });
      channel.writeAndFlush(new TextWebSocketFrame(request.toString()));
      boolean result = latch.await(5000, TimeUnit.MILLISECONDS);
      if (result && value.get() != null) {
        if (value.get() instanceof ObjectNode) {
          return (ObjectNode) value.get();
        } else {
          throw (Exception) value.get();
        }
      } else {
        throw new Exception("timed out");
      }
    } finally {
      callbacks.remove(id);
    }
  }
}

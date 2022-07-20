/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.bidi;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.mocks.LatchedSeqCallback;
import org.adamalang.net.mocks.MockEvents;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DocumentExchangeTests {
  @Test
  public void proxy() throws Exception {
    MockEvents proxy = new MockEvents();
    Runnable got = proxy.latchAt(1);
    DocumentExchange exchange = new DocumentExchange(new ClientMessage.StreamConnect(), proxy);
    exchange.failure(new ErrorCodeException(-42));
    got.run();
    proxy.assertWrite(0, "ERROR:-42");
  }

  @Test
  public void max_op_id() throws Exception {
    MockEvents proxy = new MockEvents();
    Runnable got = proxy.latchAt(1);
    ClientMessage.StreamConnect connect = new ClientMessage.StreamConnect();
    connect.key = "key";
    connect.space = "space";
    connect.origin = "origin";
    connect.viewerState = "{}";
    connect.authority = "authority";
    connect.agent = "agent";
    DocumentExchange exchange = new DocumentExchange(connect, proxy);
    ByteStream upstream = new ByteStream() {
      @Override
      public void request(int bytes) {

      }

      @Override
      public ByteBuf create(int bestGuessForSize) {
        return Unpooled.buffer(bestGuessForSize);
      }

      @Override
      public void next(ByteBuf buf) {

      }

      @Override
      public void completed() {

      }

      @Override
      public void error(int errorCode) {

      }
    };
    exchange.success(upstream);
    Class<?> clazz = DocumentExchange.class;
    Field f = clazz.getDeclaredField("nextOp");
    f.setAccessible(true);
    f.setInt(exchange, -1);
    ArrayList<LatchedSeqCallback> callbacks = new ArrayList<>();
    for (int k = 0; k < DocumentExchange.MAX_ATTEMPTS_TO_CREATE_OP + 10; k++) {
      LatchedSeqCallback callback = new LatchedSeqCallback();
      callbacks.add(callback);
      exchange.send("ch", "marker", "{}", callback);
    }
    f.setInt(exchange, -1);
    {
      LatchedSeqCallback callback = new LatchedSeqCallback();
      exchange.send("ch", "marker", "{}", callback);
      callback.assertFail(797755);
    }
    exchange.failure(new ErrorCodeException(-1));
    for (LatchedSeqCallback cb : callbacks) {
      cb.assertFail(769085);
    }
    {
      LatchedSeqCallback callback = new LatchedSeqCallback();
      exchange.send("ch", "marker", "{}", callback);
      callback.assertFail(769085);
    }
  }
}

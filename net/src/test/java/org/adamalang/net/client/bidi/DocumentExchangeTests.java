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

/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.web.client.socket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.WebJsonStream;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/** a single WebSocket connection */
public class WebClientConnection {
  private final ChannelHandlerContext ctx;
  private final AtomicInteger idgen;
  private final WebClientConnectionInboundHandler handler;
  private final Runnable close;

  WebClientConnection(final ChannelHandlerContext ctx, WebClientConnectionInboundHandler handler, Runnable close) {
    this.ctx = ctx;
    this.idgen = new AtomicInteger(0);
    this.handler = handler;
    this.close = close;
  }

  public int execute(ObjectNode request, WebJsonStream streamback) {
    int id = idgen.incrementAndGet();
    ctx.executor().execute(() -> {
      request.put("id", id);
      if (handler.registerWhileInExecutor(id, streamback)) {
        ctx.writeAndFlush(new TextWebSocketFrame(request.toString()));
      }
    });
    return id;
  }

  public <T> void requestResponse(ObjectNode request, Function<ObjectNode, T> transform, Callback<T> callback) {
    execute(request, new WebJsonStream() {
      boolean sentSuccess;
      @Override
      public void data(int connection, ObjectNode node) {
        if (!sentSuccess) {
          callback.success(transform.apply(node));
          sentSuccess = true;
        }
      }

      @Override
      public void complete() {
        if (!sentSuccess) {
          callback.success(null);
          sentSuccess = true;
        }
      }

      @Override
      public void failure(int code) {
        callback.failure(new ErrorCodeException(code));
      }
    });
  }

  public void close() {
    this.close.run();
  }
}

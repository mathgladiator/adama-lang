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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Json;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/** internal: class for wrapping the netty handler into a nice and neat package */
public class WebClientConnectionInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebClientConnectionInboundHandler.class);
  private final WebLifecycle lifecycle;
  private final ConcurrentHashMap<Integer, WebJsonStream> streams;
  private WebClientConnection connection;
  private boolean closed;

  public WebClientConnectionInboundHandler(WebLifecycle lifecycle) {
    this.lifecycle = lifecycle;
    this.streams = new ConcurrentHashMap<>();
    this.connection = null;
    this.closed = false;
  }

  public boolean registerWhileInExecutor(int id, WebJsonStream stream) {
    if (closed) {
      stream.failure(ErrorCodes.WEBBASE_CONNECTION_CLOSE);
      return false;
    }
    streams.put(id, stream);
    return true;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) throws Exception {
    ObjectNode node = Json.parseJsonObject(frame.text());
    if (node.has("ping")) {
      int latency = node.get("latency").asInt();
      if (latency > 0) {
        lifecycle.ping(latency);
      }
      node.put("pong", true);
      ctx.channel().writeAndFlush(new TextWebSocketFrame(node.toString()));
      return;
    }

    if (node.has("status")) {
      if ("connected".equals(node.get("status").textValue())) {
        String version = "?";
        if (node.has("version")) {
          version = node.get("version").textValue();
        }
        lifecycle.connected(connection, version);
      }
      return;
    }

    if (node.has("failure")) {
      int id = node.get("failure").asInt();
      int reason = node.get("reason").asInt();
      WebJsonStream streamback = streams.remove(id);
      if (streamback != null) {
        streamback.failure(reason);
      }
    } else if (node.has("deliver")) {
      int id = node.get("deliver").asInt();
      boolean done = node.get("done").asBoolean();
      WebJsonStream streamback = done ? streams.remove(id) : streams.get(id);
      if (streamback != null) {
        ObjectNode response = (ObjectNode) node.get("response");
        if (response != null && !response.isEmpty()) {
          streamback.data(id, response);
        }
        if (done) {
          streamback.complete();
        }
      }
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    connection = new WebClientConnection(ctx, this, () -> {
      end(ctx);
    });
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    end(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    LOGGER.error("web-client-fail:", cause);
    lifecycle.failure(cause);
    end(ctx);
  }

  private boolean end(ChannelHandlerContext ctx) {
    if (closed) {
      return false;
    }
    closed = true;
    HashSet<WebJsonStream> copy = new HashSet<>(streams.values());
    streams.clear();
    for (WebJsonStream stream : copy) {
      stream.failure(ErrorCodes.WEBBASE_LOST_CONNECTION);
    }
    lifecycle.disconnected();
    ctx.close();
    return true;
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class WebClientBase {
  private final WebConfig config;
  private final EventLoopGroup group;

  public WebClientBase(WebConfig config) {
    group = new NioEventLoopGroup();
    this.config = config;
  }

  public void shutdown() {
    group.shutdownGracefully();
  }

  public void open(String endpoint, WebLifecycle lifecycle) {
    URI uri = URI.create(endpoint);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("wss");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    // TODO: figure out how to do TLS if secure
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(2424242));
            ch.pipeline().addLast(new WriteTimeoutHandler(3));
            ch.pipeline().addLast(new ReadTimeoutHandler(3));
            ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
            ch.pipeline()
                .addLast(
                    new WebSocketClientProtocolHandler( //
                        URI.create(endpoint), //
                        WebSocketVersion.V13, //
                        null, //
                        true, //
                        new DefaultHttpHeaders(), //
                        50000));
            ch.pipeline()
                .addLast(
                    new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                      WebClientConnection connection;
                      final ConcurrentHashMap<Integer, WebJsonStream> streams = new ConcurrentHashMap<>();
                      private boolean closed = false;

                      @Override
                      protected void channelRead0(
                          final ChannelHandlerContext ctx, final TextWebSocketFrame frame)
                          throws Exception {
                        ObjectNode node = Json.parseJsonObject(frame.text());
                        if (node.has("ping")) {
                          lifecycle.ping();
                          node.put("pong", true);
                          ch.writeAndFlush(new TextWebSocketFrame(node.toString()));
                          return;
                        }

                        if (node.has("status")) {
                          if ("connected".equals(node.get("status").textValue())) {
                            lifecycle.connected(connection);
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
                            if (!response.isEmpty()) {
                              streamback.data(response);
                            }
                            if (done) {
                              streamback.complete();
                            }
                          }
                        }
                      }

                      @Override
                      public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        connection = new WebClientConnection(ctx, streams, () -> {
                          end(ctx);
                        });
                      }

                      @Override
                      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        end(ctx);
                      }

                      private boolean end(ChannelHandlerContext ctx) {
                        if (closed) {
                          return false;
                        }
                        lifecycle.disconnected();
                        closed = true;
                        ctx.close();
                        return true;
                      }

                      @Override
                      public void exceptionCaught(
                          final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                        lifecycle.failure(cause);
                        end(ctx);
                      }
                    });
          }
        });
      b.connect(host, port).addListeners(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
         if (!channelFuture.isSuccess()) {
           lifecycle.failure(new Exception("Failed to connect to " + host + ":" + port));
           lifecycle.disconnected();
         }
        }
      });
  }
}

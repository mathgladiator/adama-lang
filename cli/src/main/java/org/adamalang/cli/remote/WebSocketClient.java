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
import org.adamalang.cli.Config;
import org.adamalang.common.Json;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/** a very simple websocket client for usage within only the CLI (due to blocking nature) */
public class WebSocketClient implements AutoCloseable {
  private final Config config;
  private final EventLoopGroup group;

  public WebSocketClient(Config config) {
    this.group = new NioEventLoopGroup();
    this.config = config;
  }

  @Override
  public void close() throws Exception {
    group.shutdownGracefully().await(1000, TimeUnit.MILLISECONDS);
  }

  public Connection open() throws Exception {
    int maxContentLength =  config.get_int("ws_max_content_length", 1048576);
    int timeoutSeconds = config.get_int("ws_timeout_seconds", 2);
    URI uri = URI.create(config.get_string("endpoint", "ws://localhost:8080/s"));
    final var b = new Bootstrap();
    b.group(group);
    ConcurrentHashMap<Long, BiConsumer<Object, Boolean>> callbacks = new ConcurrentHashMap<>();
    b.channel(NioSocketChannel.class);
    CountDownLatch connected = new CountDownLatch(1);
    b.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
            ch.pipeline().addLast(new WriteTimeoutHandler(timeoutSeconds));
            ch.pipeline().addLast(new ReadTimeoutHandler(timeoutSeconds));
            ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
            ch.pipeline()
                .addLast(
                    new WebSocketClientProtocolHandler( //
                        uri, //
                        WebSocketVersion.V13, //
                        null, //
                        true, //
                        new DefaultHttpHeaders(), // TODO: put in a user agent
                        50000));
            ch.pipeline()
                .addLast(
                    new SimpleChannelInboundHandler<TextWebSocketFrame>() {

                      @Override
                      public void channelActive(ChannelHandlerContext ctx) throws Exception {
                      }

                      @Override
                      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                      }

                      @Override
                      public void exceptionCaught(
                          final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                        cause.printStackTrace();
                      }

                      @Override
                      protected void channelRead0(
                          final ChannelHandlerContext ctx, final TextWebSocketFrame frame)
                          throws Exception {
                        ObjectNode node = Json.parseJsonObject(frame.text());
                        if (node.has("ping")) {
                          node.put("pong", true);
                          ch.writeAndFlush(new TextWebSocketFrame(node.toString()));
                          return;
                        }

                        if (node.has("status")) {
                          if ("connected".equals(node.get("status").textValue())) {
                            connected.countDown();
                          }
                          return;
                        }

                        if (node.has("failure")) {
                          long id = node.get("failure").asLong();
                          int reason = node.get("reason").asInt();
                          BiConsumer<Object, Boolean> callback = callbacks.remove(id);
                          if (callback != null) {
                            callback.accept(new Exception("failure: " + reason), true);
                            return;
                          }
                        } else if (node.has("deliver")) {
                          long id = node.get("deliver").asLong();
                          boolean done = node.get("done").asBoolean();
                          BiConsumer<Object, Boolean> callback =
                              done ? callbacks.remove(id) : callbacks.get(id);
                          if (callback != null) {
                            callback.accept(node.get("response"), done);
                            return;
                          }
                        }

                        System.err.println("UNKNOWN:" + frame.text());
                      }
                    });
          }
        });
    final var future = b.connect(uri.getHost(), uri.getPort() < 0 ? 443 : uri.getPort());
    Channel ch = future.sync().channel();
    if (!connected.await(5000, TimeUnit.MILLISECONDS)) {
      throw new Exception("failed to establish");
    }
    return new Connection(ch, callbacks);
  }

}

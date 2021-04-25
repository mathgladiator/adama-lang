/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.api.util.Json;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketBenchmarkClientBuilder {
  public static WebSocketBenchmarkClientBuilder start(final EventLoopGroup workerGroup) {
    return new WebSocketBenchmarkClientBuilder(workerGroup);
  }

  private Channel channelToUse;
  private final DefaultHttpHeaders headers;
  private String host;
  private final int maxContentLength;
  private int port;
  private final int timeoutSeconds;
  private String uri;
  private final EventLoopGroup workerGroup;

  private WebSocketBenchmarkClientBuilder(final EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
    host = "localhost";
    port = 9000;
    uri = "/";
    maxContentLength = 1048576;
    timeoutSeconds = 10;
    headers = new DefaultHttpHeaders();
  }

  public Channel channel() {
    return channelToUse;
  }

  public void execute(BenchmarkClientFlow flow) {
    final var b = new Bootstrap();
    b.group(workerGroup);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        AtomicBoolean ready = new AtomicBoolean(false);
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
        ch.pipeline().addLast(new WriteTimeoutHandler(timeoutSeconds));
        ch.pipeline().addLast(new ReadTimeoutHandler(timeoutSeconds));
        channelToUse = ch;
        ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
        ch.pipeline().addLast(new WebSocketClientProtocolHandler( //
            URI.create("ws://" + host + ":" + port + "/~socket"), //
            WebSocketVersion.V13, //
            null, //
            true, //
            headers, //
            50000));
        ch.pipeline().addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
          @Override
          protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) throws Exception {
            ObjectNode data = Json.parseJsonObject(frame.text());
            if (data.has("signal")) {
              JsonNode statusNode = data.get("status");
              if (statusNode != null && statusNode.isTextual() && statusNode.textValue().equals("connected")) {
                flow.ready(channelToUse);
              }
              return;
            }
            if (data.has("ping")) {
              data.put("pong", System.currentTimeMillis() + "");
              ch.writeAndFlush(new TextWebSocketFrame(data.toString()));
              return;
            }
            flow.data(data);
          }

          @Override
          public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.err.println("closed()");
          }

          @Override
          public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            System.err.println("exception!");
            cause.printStackTrace();
          }
        });
      }
    });
    final var future = b.connect(host, port);
 }

  public WebSocketBenchmarkClientBuilder header(final String name, final String value) {
    headers.add(name, value);
    return this;
  }

  public WebSocketBenchmarkClientBuilder auth(String value) {
    headers.add("Cookie", new DefaultCookie("adama_token", value).toString());
    // headers.add("Cookie", new DefaultCookie("ADAMA", value).toString());
    return this;
  }

  public WebSocketBenchmarkClientBuilder server(final String host, final int port) {
    this.host = host;
    this.port = port;
    return this;
  }
}

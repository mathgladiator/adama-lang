/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class TestClientRequestBuilder {
  public static TestClientRequestBuilder start(final EventLoopGroup workerGroup) {
    return new TestClientRequestBuilder(workerGroup);
  }

  private Channel channelToUse;
  private final DefaultHttpHeaders headers;
  private String host;
  private final int maxContentLength;
  private HttpMethod method;
  private int port;
  private String postBody;
  private final int timeoutSeconds;
  private String uri;
  private boolean websocket = false;
  private final EventLoopGroup workerGroup;
  private boolean junk;

  private TestClientRequestBuilder(final EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
    host = "localhost";
    port = 8080;
    uri = "/";
    method = HttpMethod.GET;
    maxContentLength = 1048576;
    timeoutSeconds = 2;
    postBody = null;
    headers = new DefaultHttpHeaders();
    this.junk = false;
  }

  public Channel channel() {
    return channelToUse;
  }

  public void execute(final TestClientCallback callback) {
    final var b = new Bootstrap();
    b.group(workerGroup);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
        ch.pipeline().addLast(new WriteTimeoutHandler(timeoutSeconds));
        ch.pipeline().addLast(new ReadTimeoutHandler(timeoutSeconds));
        if (websocket) {
          channelToUse = ch;
          ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
          ch.pipeline().addLast(new WebSocketClientProtocolHandler( //
              URI.create("ws://" + host + ":" + port + uri), //
              WebSocketVersion.V13, //
              null, //
              true, //
              headers, //
              50000));
          ch.pipeline().addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
            @Override
            protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) throws Exception {
              callback.successfulResponse(frame.text());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              callback.closed();
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
              callback.failed(cause);
            }
          });
        } else {
          System.err.println("using simple data");
          ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
            @Override
            protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
              System.err.println("read some data");
              callback.successfulResponse(msg.content().toString(CharsetUtil.UTF_8));
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
              System.err.println("caught an exception");
              cause.printStackTrace();
              callback.failed(cause);
            }
          });
        }
      }
    });
    final var future = b.connect(host, port);
    if (!websocket) {
      System.err.println("connecting to [" + host + ":" + port + "]");
      final var postContent = postBody != null ? Unpooled.copiedBuffer(postBody, CharsetUtil.UTF_8) : null;
      future.addListener((final ChannelFuture chFuture) -> {
        if (chFuture.isSuccess()) {
          System.err.println("connection success");
          HttpRequest request;
          if (method == HttpMethod.POST) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, postContent, headers, new DefaultHttpHeaders(true));
            request.headers().set("host", "localhost");
            request.headers().set("origin", "localhost");
            request.headers().set("Content-Length", postContent.readableBytes());
          } else {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri, Unpooled.buffer(0), headers, new DefaultHttpHeaders(true));
          }

          if (junk) {
            request.headers().set("Content-Length", 25);
          }

          chFuture.channel().writeAndFlush(request);
        } else {
          System.err.println("connection failure");
          callback.failedToConnect();
        }
      });
    }
  }

  public TestClientRequestBuilder junk() {
    this.junk = true;
    return this;
  }

  public TestClientRequestBuilder get(final String uri) {
    method = HttpMethod.GET;
    this.uri = uri;
    return this;
  }

  public TestClientRequestBuilder header(final String name, final String value) {
    headers.add(name, value);
    return this;
  }

  public TestClientRequestBuilder auth(String value) {
    headers.add("Cookie", new DefaultCookie("NO", value).toString() + ";" + new DefaultCookie("ADAMA", value).toString());
    return this;
  }

  public TestClientRequestBuilder post(final String uri, final String data) {
    method = HttpMethod.POST;
    this.uri = uri;
    postBody = data;
    return this;
  }

  public TestClientRequestBuilder server(final String host, final int port) {
    this.host = host;
    this.port = port;
    return this;
  }

  public TestClientRequestBuilder withWebSocket() {
    websocket = true;
    return this;
  }
}

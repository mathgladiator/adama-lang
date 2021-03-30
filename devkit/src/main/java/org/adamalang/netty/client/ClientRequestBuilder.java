/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.client;

import java.net.URI;
import org.adamalang.netty.contracts.ClientCallback;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.CharsetUtil;

public class ClientRequestBuilder {
  public static ClientRequestBuilder start(final EventLoopGroup workerGroup) {
    return new ClientRequestBuilder(workerGroup);
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

  private ClientRequestBuilder(final EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
    host = "localhost";
    port = 8080;
    uri = "/";
    method = HttpMethod.GET;
    maxContentLength = 1048576;
    timeoutSeconds = 2;
    postBody = null;
    headers = new DefaultHttpHeaders();
  }

  public Channel channel() {
    return channelToUse;
  }

  public void execute(final ClientCallback callback) {
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
          ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
            @Override
            protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
              callback.successfulResponse(msg.content().toString(CharsetUtil.UTF_8));
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
              callback.failed(cause);
            }
          });
        }
      }
    });
    final var future = b.connect(host, port);
    if (!websocket) {
      final var postContent = postBody != null ? Unpooled.copiedBuffer(postBody, CharsetUtil.UTF_8) : null;
      future.addListener((final ChannelFuture chFuture) -> {
        if (chFuture.isSuccess()) {
          HttpRequest request;
          if (method == HttpMethod.POST) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, postContent, headers, new DefaultHttpHeaders(true));
            request.headers().set("host", "localhost");
            request.headers().set("origin", "localhost");
            request.headers().set("Content-Length", postContent.readableBytes());
          } else {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
          }
          chFuture.channel().writeAndFlush(request);
        } else {
          callback.failedToConnect();
        }
      });
    }
  }

  public ClientRequestBuilder get(final String uri) {
    method = HttpMethod.GET;
    this.uri = uri;
    return this;
  }

  public ClientRequestBuilder header(final String name, final String value) {
    headers.add(name, value);
    return this;
  }

  public ClientRequestBuilder post(final String uri, final String data) {
    method = HttpMethod.POST;
    this.uri = uri;
    postBody = data;
    return this;
  }

  public ClientRequestBuilder server(final String host, final int port) {
    this.host = host;
    this.port = port;
    return this;
  }

  public ClientRequestBuilder withWebSocket() {
    websocket = true;
    return this;
  }
}

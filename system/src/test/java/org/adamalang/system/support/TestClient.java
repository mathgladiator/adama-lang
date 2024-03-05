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
package org.adamalang.system.support;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.CharsetUtil;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

public class TestClient {
  public final DefaultHttpHeaders headers;
  private final int maxContentLength;
  private final int timeoutSeconds;
  private final EventLoopGroup workerGroup;
  private String host;
  private HttpMethod method;
  private int port;
  private String postBody;
  private String uri;

  public TestClient(final EventLoopGroup workerGroup, int port) {
    this.workerGroup = workerGroup;
    host = "127.0.0.1";
    this.port = port;
    uri = "/";
    method = HttpMethod.GET;
    maxContentLength = 1048576;
    timeoutSeconds = 2;
    postBody = null;
    headers = new DefaultHttpHeaders();
  }

  public void uri(String uri) {
    this.uri = uri;
  }

  public void method(HttpMethod method) {
    this.method = method;
  }


  public void execute(Callback<FullHttpResponse> callback) {
    final var b = new Bootstrap();
    b.group(workerGroup);
    b.channel(NioSocketChannel.class);
    b.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
            ch.pipeline().addLast(new WriteTimeoutHandler(timeoutSeconds));
            ch.pipeline().addLast(new ReadTimeoutHandler(timeoutSeconds));
            ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
              @Override
              protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
                callback.success(msg);
                ctx.close();
              }

              @Override
              public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                cause.printStackTrace();
                callback.failure(new ErrorCodeException(-1));
                ctx.close();
              }
            });
          }
        });
    final var future = b.connect(host, port);
    System.err.println("connecting to [" + host + ":" + port + "]");
    final var postContent = postBody != null ? Unpooled.copiedBuffer(postBody, CharsetUtil.UTF_8) : null;
    future.addListener(
        (final ChannelFuture chFuture) -> {
          if (chFuture.isSuccess()) {
            System.err.println("connection success");
            HttpRequest request;
            if (method == HttpMethod.POST || method == HttpMethod.PUT) {
              request =
                  new DefaultFullHttpRequest(
                      HttpVersion.HTTP_1_1,
                      method,
                      uri,
                      postContent,
                      headers,
                      new DefaultHttpHeaders(true));
              request.headers().set("Content-Length", postContent.readableBytes());
            } else {
              request =
                  new DefaultFullHttpRequest(
                      HttpVersion.HTTP_1_1,
                      method,
                      uri,
                      Unpooled.buffer(0),
                      headers,
                      new DefaultHttpHeaders(true));
            }
            chFuture.channel().writeAndFlush(request);
          } else {
            System.err.println("connection failure");
            callback.failure(new ErrorCodeException(-13));
          }
        });
  }
}

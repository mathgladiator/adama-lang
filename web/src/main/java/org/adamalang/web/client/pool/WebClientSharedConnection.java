/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client.pool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpResponder;
import org.adamalang.web.client.SimpleHttpResponseHeader;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** a shared connection to a remote endpoint */
public class WebClientSharedConnection {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(WebClientSharedConnection.class);
  private static final byte[] EMPTY_BODY = new byte[0];

  private final WebEndpoint endpoint;
  private final EventLoopGroup group;
  private SimpleHttpResponder responder;
  private byte[] chunk = new byte[8196];
  private Channel channel;

  public WebClientSharedConnection(final WebEndpoint endpoint, EventLoopGroup group) {
    this.endpoint = endpoint;
    this.group = group;
  }

  public void close() {
    channel.close();
  }

  // phase 1: if we are connected, then the channel is set
  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  // a failure happened, respond to the most recent responder
  public void failure(ErrorCodeException ex) {
    if (responder != null) {
      responder.failure(ex);
    }
  }

  // handle an HTTP message
  public void handle(HttpObject msg) {
    if (msg instanceof HttpResponse) {
      HttpResponse httpResponse = (HttpResponse) msg;
      TreeMap<String, String> headers = new TreeMap<>();
      for (Map.Entry<String, String> header : httpResponse.headers()) {
        headers.put(header.getKey().toLowerCase(Locale.ENGLISH), header.getValue());
      }
      String contentLength = headers.get("content-length");
      long size = -1;
      if (contentLength != null) {
        size = Long.parseLong(contentLength);
      } else {
        size = -1;
      }
      responder.start(new SimpleHttpResponseHeader(httpResponse.status().code(), headers));
      responder.bodyStart(size);
    } else if (msg instanceof HttpContent) {
      HttpContent content = (HttpContent) msg;
      ByteBuf body = content.content();
      while (body.readableBytes() > 0) {
        int rd = Math.min(body.readableBytes(), chunk.length);
        body.readBytes(chunk, 0, rd);
        responder.bodyFragment(chunk, 0, rd);
      }
      if (msg instanceof LastHttpContent) {
        responder.bodyEnd();
        responder = null;
      }
    }
  }

  /** write a request to the connection */
  public void writeRequest(SimpleHttpRequest request, SimpleHttpResponder responder) {
    group.execute(new Runnable() {
      @Override
      public void run() {
        WebClientSharedConnection.this.responder = responder;
        try {
          URI uri = URI.create(request.url);
          String requestPath = uri.getRawPath() + (uri.getRawQuery() != null ? ("?" + uri.getRawQuery()) : "");
          boolean success = false;
          try {
            // convert the method
            HttpMethod method = HttpMethod.valueOf(request.method.toUpperCase());
            // initialiize the headers
            HttpHeaders headers = new DefaultHttpHeaders(true);
            headers.set("Host", endpoint.host);
            // get the body size
            long bodySize = request.body.size();
            if (method != HttpMethod.GET || bodySize > 0) {
              headers.set(HttpHeaderNames.CONTENT_LENGTH, bodySize);
            }
            // apply the headers
            for (Map.Entry<String, String> entry : request.headers.entrySet()) {
              headers.set(entry.getKey(), entry.getValue());
            }
            if (bodySize < 32 * 1024) {
              final ByteBuf content;
              if (bodySize == 0) {
                content = Unpooled.wrappedBuffer(EMPTY_BODY);
              } else {
                byte[] buffer = new byte[8196];
                content = Unpooled.buffer((int) bodySize);
                int left = (int) bodySize;
                while (left > 0) {
                  int sz = request.body.read(buffer);
                  content.writeBytes(buffer, 0, sz);
                  left -= sz;
                }
              }
              channel.writeAndFlush(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, requestPath, content, headers, new DefaultHttpHeaders(true)));
            } else {
              channel.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, requestPath, headers));
              long left = bodySize;
              while (left > 0) {
                byte[] buffer = new byte[8196];
                int sz = request.body.read(buffer);
                final ByteBuf content;
                if (sz == buffer.length) {
                  content = Unpooled.wrappedBuffer(buffer);
                } else {
                  content = Unpooled.wrappedBuffer(Arrays.copyOfRange(buffer, 0, sz));
                }
                left -= sz;
                if (left == 0) {
                  channel.writeAndFlush(new DefaultLastHttpContent(content));
                } else {
                  channel.writeAndFlush(new DefaultHttpContent(content));
                }
              }
            }
            success = true;
          } finally {
            request.body.finished(success);
          }
        } catch (Exception cause) {
          responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_SHARED_EXECUTE_FAILED_READ, cause, EXLOGGER));
        }
      }
    });
  }
}

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
package org.adamalang.web.client.pool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Living;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpResponder;
import org.adamalang.web.client.SimpleHttpResponseHeader;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** a shared connection to a remote endpoint */
public class WebClientSharedConnection implements Living {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebClientSharedConnection.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  private static final byte[] EMPTY_BODY = new byte[0];

  private final AtomicBoolean alive;
  private final WebClientBaseMetrics metrics;
  private final WebEndpoint endpoint;
  private final EventLoopGroup group;
  private SimpleHttpResponder responder;
  private byte[] chunk = new byte[8196];
  private Channel channel;
  private ErrorCodeException exception;

  public WebClientSharedConnection(WebClientBaseMetrics metrics, final WebEndpoint endpoint, EventLoopGroup group) {
    this.metrics = metrics;
    this.endpoint = endpoint;
    this.group = group;
    this.responder = null;
    this.channel = null;
    this.exception = null;
    this.alive = new AtomicBoolean(true);
  }

  @Override
  public boolean alive() {
    return this.alive.get();
  }

  public void close() {
    channel.close();
    alive.set(false);
  }

  // phase 1: if we are connected, then the channel is set
  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  // a failure happened, respond to the most recent responder
  public void failure(ErrorCodeException ex) {
    alive.set(false);
    exception = ex;
    if (responder != null) {
      responder.failure(ex);
    }
  }

  // handle an HTTP message
  public void handle(HttpObject msg) {
    if (responder != null) {
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
        int code = httpResponse.status().code();
        if (code == 200 || code == 204) {
          metrics.web_client_200_or_204.run();
        } else if (code == 400) {
          metrics.web_client_400.run();
        } else if (code == 403) {
          metrics.web_client_403.run();
        } else if (code == 404) {
          metrics.web_client_404.run();
        } else if (code == 410) {
          metrics.web_client_410.run();
        } else if (code >= 500) {
          metrics.web_client_500_plus.run();
        } else {
          metrics.web_client_code_unknown.run();
        }
        responder.start(new SimpleHttpResponseHeader(code, headers));
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
          SimpleHttpResponder old = responder;
          responder = null;
          old.bodyEnd();
        }
      }
    } else {
      metrics.alarm_web_client_null_responder.up();
    }
  }

  /** write a request to the connection */
  public void writeRequest(SimpleHttpRequest request, SimpleHttpResponder responder) {
    if (exception != null) {
      metrics.web_client_instant_fail.run();
      responder.failure(exception);
      return;
    }
    metrics.inflight_web_requests.up();
    this.responder = responder;
    group.execute(new Runnable() {
      @Override
      public void run() {
        try {
          URI uri = URI.create(request.url);
          String requestPath = uri.getRawPath() + (uri.getRawQuery() != null ? ("?" + uri.getRawQuery()) : "");
          boolean success = false;
          try {
            metrics.web_client_request_start.run();
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
              metrics.web_client_request_sent_small_full.run();
            } else {
              channel.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, requestPath, headers));
              metrics.web_client_request_send_large_started.run();
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
            metrics.web_client_request_send_large_finished.run();
            success = true;
          } finally {
            request.body.finished(success);
          }
        } catch (Exception cause) {
          metrics.web_client_request_failed_send.run();
          responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_SHARED_EXECUTE_FAILED_READ, cause, EXLOGGER));
        } finally {
          metrics.inflight_web_requests.down();
        }
      }
    });
  }
}

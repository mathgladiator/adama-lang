/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.nio.charset.StandardCharsets;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);

  private static void sendWithKeepAlive(final Config config, final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
    final var responseStatus = res.status();
    String origin = req.headers().get("origin");
    if (origin != null) {
      if (config.allowedOrigins.contains(origin)) {
        res.headers().set("Access-Control-Allow-Origin", origin);
        res.headers().set("Access-Control-Allow-Credentials", true);
      }
    }
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  private final Nexus nexus;

  public WebHandler(Nexus nexus) {
    this.nexus = nexus;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    // analyze the request
    boolean isInvalid = !req.decoderResult().isSuccess();
    boolean isHealthCheck = nexus.config.healthCheckPath.equals(req.uri());

    // send the default response for bad or health checks
    if (isInvalid || isHealthCheck) {
      HttpResponseStatus status = isHealthCheck ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST;
      final var content = (isHealthCheck ? "HEALTHY:" + System.currentTimeMillis() : "Bad Request").getBytes(StandardCharsets.UTF_8);
      final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.wrappedBuffer(content));
      HttpUtil.setContentLength(res, content.length);
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
      sendWithKeepAlive(nexus.config, ctx, req, res);
      return;
    }

    // parse the uri into a query stsring
    final var qsd = new QueryStringDecoder(req.uri());

    // find the handler
    UriHandler handler = nexus.handlers.get(qsd.path());
    if (handler == null) {
      handler = nexus.passthroughHandler;
    }

    // let the handler handle it
    handler.handle(req, qsd, new Callback<>() {
      @Override
      public void success(FullHttpResponse value) {
        sendWithKeepAlive(nexus.config, ctx, req, value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        // TODO: consider routing back to the handler with a new path?
        final var content = ("Error:" + ex.code).getBytes();
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.valueOf(ex.code), Unpooled.copiedBuffer(content));
        HttpUtil.setContentLength(response, content.length);
        sendWithKeepAlive(nexus.config, ctx, req, response);
      }
    });
  }

        /*
        if (req.method() == HttpMethod.POST) {
          if (qsd.path().equals("/upload")) {
            String space = getSpace(qsd);
            GameSpace gs = nexus.db.getOrCreate(space);
            long docId = getDocumentId(qsd);
            gs.get(docId, new Callback<DurableLivingDocument>() {
              @Override
              public void success(DurableLivingDocument document) {
                try {
                  HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(factory, req);
                  httpDecoder.setDiscardThreshold(0);
                  for (InterfaceHttpData data : httpDecoder.getBodyHttpDatas()) {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                      FileUpload result = (FileUpload) data;
                      MessageDigest digest1 = MessageDigest.getInstance("SHA-384");
                      MessageDigest digest2 = MessageDigest.getInstance("MD5");
                      InputStream in = new FileInputStream(result.getFile());
                      try {
                        byte[] buffer = new byte[256 * 256];
                        int rd;
                        while ((rd = in.read(buffer)) > 0) {
                          digest1.update(buffer, 0, rd);
                          digest2.update(buffer, 0, rd);
                        }
                        byte[] hash1 = digest1.digest();
                        byte[] hash2 = digest1.digest();
                      } finally {
                        in.close();
                      }
                      AssetRequest request = new AssetRequest() {
                        @Override
                        public String name() {
                          return result.getFilename();
                        }

                        @Override
                        public String type() {
                          return result.getContentType();
                        }

                        @Override
                        public long documentId() {
                          return docId;
                        }

                        @Override
                        public String space() {
                          return space;
                        }

                        @Override
                        public long size() {
                          return result.length();
                        }

                        @Override
                        public String md5() {
                          return null;
                        }

                        @Override
                        public String sha384() {
                          return null;
                        }

                        @Override
                        public Supplier<InputStream> source() {
                          return () -> {
                            try {
                              return new FileInputStream(result.getFile());
                            } catch (IOException ioe) {
                              throw new RuntimeException(ioe);
                            }
                          };
                        }
                      };
                      System.err.println("Got asset request, now I just need a service for it... blah");
                      nexus.assetService.upload(request, new Callback<NtAsset>() {
                        @Override
                        public void success(NtAsset asset) {
                          document.attach(NtClient.NO_ONE, asset, new Callback<Integer>() {
                            @Override
                            public void success(Integer value) {

                            }

                            @Override
                            public void failure(ErrorCodeException ex) {

                            }
                          });
                          System.err.println("got the asset");
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          System.err.println("failed at this endeavor... this is getting complicated");
                        }
                      });
                    }
                  }
                } catch (Exception ex) {
                  failure(new ErrorCodeException(-1, ex));
                }
              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
            // TODO: validate both these exist


            sendWithKeepAlive(ctx, req, StaticSite.ofHTML(req, "Uploaded Result:" + System.currentTimeMillis()));
            return;
          }
          final var request = WebHandler.parseJsonObject(req.content().toString(StandardCharsets.UTF_8));
          final AuthCallback afterAuth = new AuthCallback() {
            @Override
            public void failure() {
              success(null);
            }

            @Override
            public void success(final AdamaSession session) {
              final JsonResponder responder = new JsonResponder() {
                @Override
                public void failure(ErrorCodeException ex) {
                  final var content = ("{\"error\":" + ex.code + "}").getBytes(StandardCharsets.UTF_8);
                  final FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(content));
                  HttpUtil.setContentLength(response, content.length);
                  sendWithKeepAlive(ctx, req, response);
                }

                @Override
                public void respond(final String json, final boolean done, final HashMap<String, String> headers) {
                  final var content = json.getBytes(StandardCharsets.UTF_8);
                  final FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.copiedBuffer(content));
                  if (headers != null) {
                    for (final Map.Entry<String, String> headerEntry : headers.entrySet()) {
                      response.headers().set(headerEntry.getKey(), headerEntry.getValue());
                    }
                  }
                  HttpUtil.setContentLength(response, content.length);
                  sendWithKeepAlive(ctx, req, response);
                }
              };
              try {
                nexus.handler.handle(session, request, responder);
              } catch (final ErrorCodeException ex) {
                responder.failure(ex);
              } catch (final Throwable ex) {
                responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E5_UNCAUGHT_EXCEPTION_WEB_HANDLER, ex));
              }
              if (session != null) {
                session.kill();
              }
            }
          };
          final var authToken = AdamaCookieCodec.extractCookieValue(req.headers().get(HttpHeaderNames.COOKIE), AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME);
          if (authToken != null) {
            nexus.authenticator.authenticate(authToken, afterAuth);
          } else {
            afterAuth.failure();
          }
          return;
        }
        if (response == null && req.method() == HttpMethod.GET) {
          response = nexus.site.request(qsd.path().substring(1), HttpResponseStatus.OK, req);
          if (response == null) {
            generatedFailureStatusCode = HttpResponseStatus.NOT_FOUND;
            response = nexus.site.request("errors.404.html", HttpResponseStatus.NOT_FOUND, req);
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
        generatedFailureStatusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        response = nexus.site.request("errors.500.html", HttpResponseStatus.INTERNAL_SERVER_ERROR, req);
      }

    }
         */
      /*
    if (response != null) {
      sendWithKeepAlive(ctx, req, response);
    } else {
      final var content = ("Error:" + generatedFailureStatusCode.toString()).getBytes();
      response = new DefaultFullHttpResponse(req.protocolVersion(), generatedFailureStatusCode, Unpooled.copiedBuffer(content));
      HttpUtil.setContentLength(response, content.length);
      sendWithKeepAlive(ctx, req, response);
    }

       */

}

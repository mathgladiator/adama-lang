/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

import io.netty.buffer.ByteBuf;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.bidi.DocumentExchange;
import org.adamalang.net.client.bidi.MeteringExchange;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.client.contracts.impl.CallbackByteStreamInfo;
import org.adamalang.net.client.contracts.impl.CallbackByteStreamWriter;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocationType;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a client using the common-net code to connect to a remote server */
public class InstanceClient implements AutoCloseable {
  public final String target;
  public final SimpleExecutor executor;
  private final NetBase base;
  private final LocalRegionClientMetrics metrics;
  private final RoutingTarget routing;
  private final HeatMonitor monitor;
  private final AtomicBoolean alive;
  private final ItemQueue<ChannelClient> client;
  private final ClientConfig config;
  private int backoff;

  public InstanceClient(NetBase base, ClientConfig config, LocalRegionClientMetrics metrics, HeatMonitor monitor, RoutingTarget routing, String target, SimpleExecutor executor) throws Exception {
    this.base = base;
    this.config = config;
    this.target = target;
    this.executor = executor;
    this.metrics = metrics;
    this.monitor = monitor;
    this.routing = routing;
    this.client = new ItemQueue<>(executor, config.getClientQueueSize(), config.getClientQueueTimeoutMS());
    this.alive = new AtomicBoolean(true);
    this.backoff = 1;
    retryConnection();
  }

  private void retryConnection() {
    if (alive.get() && base.alive()) {
      base.connect(this.target, new org.adamalang.common.net.Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
          metrics.client_connection_alive.up();
          metrics.client_info_start.run();
          channel.open(new ServerCodec.StreamInfo() {
            @Override
            public void handle(ServerMessage.InventoryHeartbeat payload) {
              routing.integrate(InstanceClient.this.target, Arrays.asList(payload.spaces));
            }

            @Override
            public void handle(ServerMessage.HeatPayload heat) {
              monitor.heat(target, heat.cpu, heat.mem);
            }

            @Override
            public void completed() {
              metrics.client_info_completed.run();
            }

            @Override
            public void error(int errorCode) {
              metrics.client_info_failed_downstream.run();
            }
          }, new CallbackByteStreamInfo(monitor, metrics));

          executor.execute(new NamedRunnable("channel-client-ready") {
            @Override
            public void execute() throws Exception {
              backoff = 1;
              if (alive.get()) {
                client.ready(channel);
              }
            }
          });
        }

        @Override
        public void failed(ErrorCodeException ex) {
          executor.execute(new NamedRunnable("channel-client-failed") {
            @Override
            public void execute() throws Exception {
              client.unready();
              scheduleWithinExecutor();
            }
          });
        }

        @Override
        public void disconnected() {
          metrics.client_connection_alive.down();
          executor.execute(new NamedRunnable("channel-client-disconnect") {
            @Override
            public void execute() throws Exception {
              client.unready();
              scheduleWithinExecutor();
            }
          });
        }

        private void scheduleWithinExecutor() {
          if (alive.get()) {
            metrics.client_retry.run();
            backoff = (int) (5 + backoff + backoff * Math.random());
            if (backoff > 500) {
              backoff = (int) (250 + 250 * Math.random());
            }
            executor.schedule(new NamedRunnable("client-retry") {
              @Override
              public void execute() throws Exception {
                retryConnection();
              }
            }, backoff);
          }
        }
      });
    }
  }

  /** block the current thread to ensure the client is connected right now */
  public boolean ping(int timeLimit) throws Exception {
    AtomicBoolean success = new AtomicBoolean(false);
    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(new NamedRunnable("execute-ping") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<>(ErrorCodes.ADAMA_NET_PING_TIMEOUT, ErrorCodes.ADAMA_NET_PING_REJECTED, metrics.client_ping.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamPing() {
              @Override
              public void handle(ServerMessage.PingResponse payload) {
                success.set(true);
              }

              @Override
              public void completed() {
                latch.countDown();
              }

              @Override
              public void error(int code) {
                latch.countDown();
              }
            }, new Callback<ByteStream>() {
              @Override
              public void success(ByteStream stream) {
                ByteBuf toWrite = stream.create(4);
                ClientCodec.write(toWrite, new ClientMessage.PingRequest());
                stream.next(toWrite);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                latch.countDown();
              }
            });
          }

          @Override
          protected void failure(int code) {
            latch.countDown();
          }
        }, timeLimit);
      }
    });
    latch.await(timeLimit, TimeUnit.MILLISECONDS);
    return success.get();
  }

  public void authorize(String ip, String origin, String space, String key, String username, String password, Callback<String> callback) {
    executor.execute(new NamedRunnable("execute-authorize") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_AUTH_TIMEOUT, ErrorCodes.ADAMA_NET_AUTH_REJECTED, metrics.client_auth.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamAuth() {
              @Override
              public void handle(ServerMessage.AuthResponse payload) {
                callback.success(payload.agent);
              }

              @Override
              public void completed() {

              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + username.length() + password.length() + 40);
                ClientMessage.Authorize auth = new ClientMessage.Authorize();
                auth.space = space;
                auth.key = key;
                auth.username = username;
                auth.password = password;
                auth.origin = origin;
                auth.ip = ip;
                ClientCodec.write(toWrite, auth);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void find(Key key, Callback<DocumentLocation> callback) {
    executor.execute(new NamedRunnable("client-find") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_FIND_TIMEOUT, ErrorCodes.ADAMA_NET_FIND_REJECTED, metrics.client_find.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamFinder() {
              @Override
              public void handle(ServerMessage.FindResponse payload) {
                callback.success(new DocumentLocation(payload.id, LocationType.fromType(payload.location), payload.region, payload.machine, payload.archive, false));
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                int est = key.space.length() + key.key.length() + 40;
                ByteBuf toWrite = stream.create(est);
                ClientMessage.FindRequest request = new ClientMessage.FindRequest();
                request.space = key.space;
                request.key = key.key;
                ClientCodec.write(toWrite, request);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public static class ProbeResponse {
    public final String json;
    public final String[] errors;

    public ProbeResponse(String json, String[] errors) {
      this.json = json;
      this.errors = errors;
    }
  }

  public void probe(String command, String[] args, Callback<ProbeResponse> callback) {
   executor.execute(new NamedRunnable("execute-" + command) {
     @Override
     public void execute() throws Exception {
       client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_PROBE_TIMEOUT, ErrorCodes.ADAMA_NET_PROBE_REJECTED, metrics.client_webget.start()) {
         @Override
         protected void executeNow(ChannelClient client) {
           client.open(new ServerCodec.StreamProbe() {
             @Override
             public void handle(ServerMessage.ProbeCommandResponse payload) {
               callback.success(new ProbeResponse(payload.json, payload.errors));
             }

             @Override
             public void completed() {
             }

             @Override
             public void error(int errorCode) {
               callback.failure(new ErrorCodeException(errorCode));
             }
           }, new CallbackByteStreamWriter(callback) {
             @Override
             public void write(ByteStream stream) {
               int est = command.length() + 32;
               for(String arg : args) {
                 est += arg.length() + 16;
               }
               ByteBuf toWrite = stream.create(est);
               ClientMessage.ProbeCommandRequest request = new ClientMessage.ProbeCommandRequest();
               request.command = command;
               request.args = args;
               ClientCodec.write(toWrite, request);
               stream.next(toWrite);
             }
           });
         }

         @Override
         protected void failure(int code) {
           callback.failure(new ErrorCodeException(code));
         }
       });
     }
   });
  }

  public void webGet(String space, String key, WebGet request, Callback<WebResponse> callback) {
    executor.execute(new NamedRunnable("execute-web-get") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_WEBGET_TIMEOUT, ErrorCodes.ADAMA_NET_WEBGET_REJECTED, metrics.client_webget.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamWeb() {
              @Override
              public void handle(ServerMessage.WebResponseNet payload) {
                commonWebReturn(payload, callback);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + request.context.who.agent.length() + request.context.who.authority.length() + request.uri.length() + 40);
                ClientMessage.WebGet webGet = new ClientMessage.WebGet();
                webGet.agent = request.context.who.agent;
                webGet.authority = request.context.who.authority;
                webGet.uri = request.uri;
                webGet.key = key;
                webGet.space = space;
                webGet.origin = request.context.origin;
                webGet.ip = request.context.ip;
                webGet.headers = new ClientMessage.Header[request.headers.size()];
                int at = 0;
                for (Map.Entry<String, String> header : request.headers.entries()) {
                  webGet.headers[at] = new ClientMessage.Header();
                  webGet.headers[at].key = header.getKey();
                  webGet.headers[at].value = header.getValue();
                  at++;
                }
                webGet.parametersJson = request.parameters.json;
                ClientCodec.write(toWrite, webGet);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  private void commonWebReturn(ServerMessage.WebResponseNet payload, Callback<WebResponse> callback) {
    WebResponse response = new WebResponse();
    response.body = payload.body;
    response.contentType = payload.contentType;
    if (payload.assetId != null) {
      response.asset = new NtAsset(payload.assetId, payload.assetName, payload.contentType, payload.assetSize, payload.assetMD5, payload.assetSHA384);
    }
    response.cors = payload.cors;
    response.cache_ttl_seconds = payload.cache_ttl_seconds;
    response.asset_transform = payload.asset_transform;
    callback.success(response);
  }

  public void webOptions(String space, String key, WebGet request, Callback<WebResponse> callback) {
    executor.execute(new NamedRunnable("execute-web-get") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_WEBOPTIONS_TIMEOUT, ErrorCodes.ADAMA_NET_WEBOPTIONS_REJECTED, metrics.client_weboptions.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamWeb() {
              @Override
              public void handle(ServerMessage.WebResponseNet payload) {
                commonWebReturn(payload, callback);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + request.context.who.agent.length() + request.context.who.authority.length() + request.uri.length() + 40);
                ClientMessage.WebOptions webOptions = new ClientMessage.WebOptions();
                webOptions.agent = request.context.who.agent;
                webOptions.authority = request.context.who.authority;
                webOptions.uri = request.uri;
                webOptions.key = key;
                webOptions.space = space;
                webOptions.origin = request.context.origin;
                webOptions.ip = request.context.ip;
                webOptions.headers = new ClientMessage.Header[request.headers.size()];
                int at = 0;
                for (Map.Entry<String, String> header : request.headers.entries()) {
                  webOptions.headers[at] = new ClientMessage.Header();
                  webOptions.headers[at].key = header.getKey();
                  webOptions.headers[at].value = header.getValue();
                  at++;
                }
                webOptions.parametersJson = request.parameters.json;
                ClientCodec.write(toWrite, webOptions);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void webPut(String space, String key, WebPut request, Callback<WebResponse> callback) {
    executor.execute(new NamedRunnable("execute-web-put") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_WEBPUT_TIMEOUT, ErrorCodes.ADAMA_NET_WEBPUT_REJECTED, metrics.client_webput.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamWeb() {
              @Override
              public void handle(ServerMessage.WebResponseNet payload) {
                commonWebReturn(payload, callback);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + request.context.who.agent.length() + request.context.who.authority.length() + request.uri.length() + 40);
                ClientMessage.WebPut webPut = new ClientMessage.WebPut();
                webPut.agent = request.context.who.agent;
                webPut.authority = request.context.who.authority;
                webPut.uri = request.uri;
                webPut.key = key;
                webPut.space = space;
                webPut.origin = request.context.origin;
                webPut.ip = request.context.ip;
                webPut.headers = new ClientMessage.Header[request.headers.size()];
                int at = 0;
                for (Map.Entry<String, String> header : request.headers.entries()) {
                  webPut.headers[at] = new ClientMessage.Header();
                  webPut.headers[at].key = header.getKey();
                  webPut.headers[at].value = header.getValue();
                  at++;
                }
                webPut.parametersJson = request.parameters.json;
                webPut.bodyJson = request.bodyJson;
                ClientCodec.write(toWrite, webPut);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }


  public void webDelete(String space, String key, WebDelete request, Callback<WebResponse> callback) {
    executor.execute(new NamedRunnable("execute-web-put") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_WEBPUT_TIMEOUT, ErrorCodes.ADAMA_NET_WEBPUT_REJECTED, metrics.client_webput.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamWeb() {
              @Override
              public void handle(ServerMessage.WebResponseNet payload) {
                commonWebReturn(payload, callback);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + request.context.who.agent.length() + request.context.who.authority.length() + request.uri.length() + 40);
                ClientMessage.WebDelete webPut = new ClientMessage.WebDelete();
                webPut.agent = request.context.who.agent;
                webPut.authority = request.context.who.authority;
                webPut.uri = request.uri;
                webPut.key = key;
                webPut.space = space;
                webPut.origin = request.context.origin;
                webPut.ip = request.context.ip;
                webPut.headers = new ClientMessage.Header[request.headers.size()];
                int at = 0;
                for (Map.Entry<String, String> header : request.headers.entries()) {
                  webPut.headers[at] = new ClientMessage.Header();
                  webPut.headers[at].key = header.getKey();
                  webPut.headers[at].value = header.getValue();
                  at++;
                }
                webPut.parametersJson = request.parameters.json;
                ClientCodec.write(toWrite, webPut);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void directSend(String ip, String origin, String agent, String authority, String space, String key, String marker, String channel, String message, Callback<Integer> callbackRaw) {
    Callback<Integer> callback = metrics.client_directsend_cb.wrap(callbackRaw);
    executor.execute(new NamedRunnable("execute-direct-send") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_DIRECTSEND_TIMEOUT, ErrorCodes.ADAMA_NET_DIRECTSEND_REJECTED, metrics.client_directsend.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamDirect() {
              @Override
              public void handle(ServerMessage.DirectSendResponse payload) {
                callback.success(payload.seq);
              }

              @Override
              public void completed() {

              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter<>(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(agent.length() + authority.length() + space.length() + key.length() + (marker != null ? marker.length() : 0) + message.length() + channel.length() + origin.length() + 40);
                ClientMessage.DirectSend create = new ClientMessage.DirectSend();
                create.origin = origin;
                create.agent = agent;
                create.authority = authority;
                create.space = space;
                create.key = key;
                create.marker = marker;
                create.channel = channel;
                create.message = message;
                create.ip = ip;
                ClientCodec.write(toWrite, create);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  /** create a document */
  public void create(String ip, String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.client_create_cb.wrap(callbackRaw);
    executor.execute(new NamedRunnable("execute-create") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_CREATE_TIMEOUT, ErrorCodes.ADAMA_NET_CREATE_REJECTED, metrics.client_create.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamCreation() {
              @Override
              public void handle(ServerMessage.CreateResponse payload) {
                callback.success(null);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(agent.length() + authority.length() + space.length() + key.length() + (entropy != null ? entropy.length() : 0) + arg.length() + origin.length() + 40);
                ClientMessage.CreateRequest create = new ClientMessage.CreateRequest();
                create.origin = origin;
                create.agent = agent;
                create.authority = authority;
                create.space = space;
                create.key = key;
                create.entropy = entropy;
                create.arg = arg;
                create.ip = ip;
                ClientCodec.write(toWrite, create);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  /** delete a document */
  public void delete(String ip, String origin, String agent, String authority, String space, String key, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.client_delete_cb.wrap(callbackRaw);
    executor.execute(new NamedRunnable("execute-delete") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_DELETE_TIMEOUT, ErrorCodes.ADAMA_NET_DELETE_REJECTED, metrics.client_delete.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamDeletion() {
              @Override
              public void handle(ServerMessage.DeleteResponse payload) {
                callback.success(null);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(agent.length() + authority.length() + space.length() + key.length() + origin.length() + 40);
                ClientMessage.DeleteRequest delete = new ClientMessage.DeleteRequest();
                delete.origin = origin;
                delete.agent = agent;
                delete.authority = authority;
                delete.space = space;
                delete.key = key;
                delete.ip = ip;
                ClientCodec.write(toWrite, delete);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void reflect(String space, String key, Callback<String> callbackRaw) {
    Callback<String> callback = metrics.client_reflection_cb.wrap(callbackRaw);
    executor.execute(new NamedRunnable("execute-reflect") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_REFLECT_TIMEOUT, ErrorCodes.ADAMA_NET_REFLECT_REJECTED, metrics.client_create.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamReflection() {
              @Override
              public void handle(ServerMessage.ReflectResponse payload) {
                callback.success(payload.schema);
              }

              @Override
              public void completed() {

              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + 10);
                ClientMessage.ReflectRequest reflect = new ClientMessage.ReflectRequest();
                reflect.space = space;
                reflect.key = key;
                ClientCodec.write(toWrite, reflect);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  public void startMeteringExchange(MeteringStream meteringStream) {
    executor.execute(new NamedRunnable("metering-exchange") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<>(ErrorCodes.ADAMA_NET_METERING_TIMEOUT, ErrorCodes.ADAMA_NET_METERING_REJECTED, metrics.client_metering_exchange.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            MeteringExchange exchange = new MeteringExchange(target, meteringStream);
            client.open(exchange, exchange);
          }

          @Override
          protected void failure(int code) {
            meteringStream.failure(code);
          }
        });
      }
    });
  }

  public void scanDeployments(String space, Callback<Void> callbackRaw) {
    Callback<Void> callback = metrics.client_scan_deployment_cb.wrap(callbackRaw);
    executor.execute(new NamedRunnable("execute-scan") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_SCAN_DEPLOYMENT_TIMEOUT, ErrorCodes.ADAMA_NET_SCAN_DEPLOYMENT_REJECTED, metrics.client_scan_deployment.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.open(new ServerCodec.StreamDeployment() {
              @Override
              public void handle(ServerMessage.ScanDeploymentResponse payload) {
                callback.success(null);
              }

              @Override
              public void completed() {
              }

              @Override
              public void error(int errorCode) {
                callback.failure(new ErrorCodeException(errorCode));
              }
            }, new CallbackByteStreamWriter(callback) {
              @Override
              public void write(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + 10);
                ClientMessage.ScanDeployment scan = new ClientMessage.ScanDeployment();
                scan.space = space;
                ClientCodec.write(toWrite, scan);
                stream.next(toWrite);
              }
            });
          }

          @Override
          protected void failure(int code) {
            callback.failure(new ErrorCodeException(code));
          }
        });
      }
    });
  }

  /** connect to a document */
  public void connect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, Events events) {
    ClientMessage.StreamConnect connectMessage = new ClientMessage.StreamConnect();
    connectMessage.ip = ip;
    connectMessage.origin = origin;
    connectMessage.agent = agent;
    connectMessage.authority = authority;
    connectMessage.space = space;
    connectMessage.key = key;
    connectMessage.viewerState = viewerState;
    connectMessage.assetKey = assetKey;
    executor.execute(new NamedRunnable("document-exchange") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_CONNECT_DOCUMENT_TIMEOUT, ErrorCodes.ADAMA_NET_CONNECT_DOCUMENT_REJECTED, metrics.client_document_exchange.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            DocumentExchange exchange = new DocumentExchange(connectMessage, events);
            client.open(exchange, exchange);
          }

          @Override
          protected void failure(int code) {
            events.error(code);
          }
        });
      }
    });
  }

  @Override
  public void close() {
    alive.set(false);
    executor.execute(new NamedRunnable("close-connection") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_CLOSE_TIMEOUT, ErrorCodes.ADAMA_NET_CLOSE_REJECTED, metrics.client_close.start()) {
          @Override
          protected void executeNow(ChannelClient client) {
            client.close();
          }

          @Override
          protected void failure(int code) {
            // don't care
          }
        });
        client.unready();
        client.nuke();
      }
    });
  }
}

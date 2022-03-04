/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
import org.adamalang.net.client.contracts.*;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a managed client that makes talking to the gRPC server nice */
public class InstanceClient implements AutoCloseable {
  private final NetBase base;
  public final String target;
  private final SimpleExecutor executor;
  private final ClientMetrics metrics;
  private final HeatMonitor monitor;
  private final Lifecycle lifecycle;
  private final Random rng;
  private final ExceptionLogger logger;
  private final AtomicBoolean alive;
  private int backoff;
  private ItemQueue<ChannelClient> client;

  public InstanceClient(NetBase base, ClientMetrics metrics, HeatMonitor monitor, String target, SimpleExecutor executor, Lifecycle lifecycle, ExceptionLogger logger) throws Exception {
    this.base = base;
    this.target = target;
    this.executor = executor;
    this.metrics = metrics;
    this.monitor = monitor;
    this.rng = new Random();
    this.client = new ItemQueue<>(executor, 64, 2500);
  //  this.downstream = null;
    this.lifecycle = lifecycle;
    this.logger = logger;
    this.alive = new AtomicBoolean(true);
    this.backoff = 1;
    retryConnection();
  }

  private void retryConnection() {
    if (alive.get()) {
      base.connect(this.target, new org.adamalang.common.net.Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
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
          executor.execute(new NamedRunnable("channel-client-disconnect") {
            @Override
            public void execute() throws Exception {
              client.unready();
              scheduleWithinExecutor();
            }
          });
        }

        private void scheduleWithinExecutor() {
          backoff = (int) (1 + backoff * Math.random());
          if (backoff > 2000) {
            backoff = (int) (1500 + 500 * Math.random());
          }
          executor.schedule(new NamedRunnable("client-retry") {
            @Override
            public void execute() throws Exception {
              retryConnection();
            }
          }, backoff);
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
                stream.completed();
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

  /** create a document */
  public void create(String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callback) {
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
            }, new Callback<ByteStream>() {
              @Override
              public void success(ByteStream stream) {
                ByteBuf toWrite = stream.create(agent.length() + authority.length() + space.length() + key.length() + entropy.length() + arg.length() + origin.length() + 40);
                ClientMessage.CreateRequest create = new ClientMessage.CreateRequest();
                create.origin = origin;
                create.agent = agent;
                create.authority = authority;
                create.space = space;
                create.key = key;
                create.entropy = entropy;
                create.arg = arg;
                ClientCodec.write(toWrite, create);
                stream.next(toWrite);
                stream.completed();
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
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

  public void reflect(String space, String key, Callback<String> callback) {
    executor.execute(new NamedRunnable("execute-reflect") {
      @Override
      public void execute() throws Exception {
        client.add(new ItemAction<ChannelClient>(ErrorCodes.ADAMA_NET_CREATE_TIMEOUT, ErrorCodes.ADAMA_NET_CREATE_REJECTED, metrics.client_create.start()) {
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
            }, new Callback<ByteStream>() {
              @Override
              public void success(ByteStream stream) {
                ByteBuf toWrite = stream.create(space.length() + key.length() + 10);
                ClientMessage.ReflectRequest reflect = new ClientMessage.ReflectRequest();
                reflect.space = space;
                reflect.key = key;
                ClientCodec.write(toWrite, reflect);
                stream.next(toWrite);
                stream.completed();
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
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
    /*
    executor.execute(new NamedRunnable("metering-exchange") {
      @Override
      public void execute() throws Exception {
        MeteringObserver observer = new MeteringObserver(meteringStream);
        observer.start(stub.meteringExchange(observer));
      }
    });
     */
  }

  public void scanDeployments(String space, ScanDeploymentCallback callback) {
    // stub.scanDeployments(ScanDeploymentsRequest.newBuilder().setSpace(space).build(), ScanDeploymentCallback.WRAP(callback));
  }

  /** connect to a document */
  public long connect(String agent, String authority, String space, String key, String viewerState, Events events) {
    /*
    long docId = table.id();
    executor.execute(new NamedRunnable("client-connect", target, space, key) {
      @Override
      public void execute() throws Exception {
        if (InstanceClient.this.upstream != null) {
          table.associate(docId, events);
          upstream.onNext(StreamMessageClient.newBuilder().setId(docId).setConnect(StreamConnect.newBuilder().setAgent(agent).setAuthority(authority).setSpace(space).setKey(key).setState(viewerState).build()).build());
        } else {
          events.disconnected();
        }
      }
    });
    return docId;
    */
    return 0;
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
/*
  private class MeteringObserver implements StreamObserver<MeteringReverse> {
    private final MeteringStream meteringStream;
    boolean completedThisSide = false;
    private StreamObserver<MeteringForward> upstream;
    private String batchRemoved;

    public MeteringObserver(MeteringStream meteringStream) {
      this.meteringStream = meteringStream;
      this.batchRemoved = null;
    }

    public void start(StreamObserver<MeteringForward> upstream) {
      this.upstream = upstream;
      // start the conversation
      upstream.onNext(MeteringForward.newBuilder().setBegin(MeteringBegin.newBuilder().build()).build());
    }

    @Override
    public void onNext(MeteringReverse reverse) {
      switch (reverse.getOperationCase()) {
        case FOUND: {
          executor.execute(new NamedRunnable("asking-to-remove") {
            @Override
            public void execute() throws Exception {
              MeteringBatchFound found = reverse.getFound();
              MeteringObserver.this.batchRemoved = found.getBatch();
              upstream.onNext(MeteringForward.newBuilder().setRemove(MeteringDeleteBatch.newBuilder().setId(found.getId()).build()).build());
            }
          });
          return;
        }
        case REMOVED: {
          executor.execute(new NamedRunnable("removing-batch") {
            @Override
            public void execute() throws Exception {
              meteringStream.handle(target, batchRemoved, () -> {
                executor.execute(new NamedRunnable("batch-processed") {
                  @Override
                  public void execute() throws Exception {
                    upstream.onNext(MeteringForward.newBuilder().setBegin(MeteringBegin.newBuilder().build()).build());
                  }
                });
              });
            }
          });
          return;
        }
      }
    }

    @Override
    public void onError(Throwable throwable) {
      meteringStream.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GRPC_METERING_UNKNOWN_EXCEPTION, throwable, logger).code);
    }

    @Override
    public void onCompleted() {
      meteringStream.finished();
      if (!completedThisSide) {
        upstream.onCompleted();
        completedThisSide = true;
      }
    }
  }

  public class InstanceRemote implements Remote {
    private final long docId;

    public InstanceRemote(long docId) {
      this.docId = docId;
    }

    @Override
    public void canAttach(AskAttachmentCallback callback) {
      long askId = table.id();
      executor.execute(new NamedRunnable("client-ask-attachment", target) {
        @Override
        public void execute() throws Exception {
          if (upstream != null) {
            table.associate(askId, callback);
            upstream.onNext(StreamMessageClient.newBuilder().setId(askId).setAct(docId).setAsk(StreamAskAttachmentRequest.newBuilder().build()).build());
          } else {
            callback.error(ErrorCodes.GRPC_ASK_FAILED_NOT_CONNECTED);
          }
        }
      });
    }

    @Override
    public void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback) {
      long attachId = table.id();
      executor.execute(new NamedRunnable("client-attach", target) {
        @Override
        public void execute() throws Exception {
          if (upstream != null) {
            table.associate(attachId, callback);
            upstream.onNext(StreamMessageClient.newBuilder().setId(attachId).setAct(docId).setAttach(StreamAttach.newBuilder().setId(id).setFilename(name).setContentType(contentType).setSize(size).setMd5(md5).setSha384(sha384).build()).build());
          } else {
            callback.error(ErrorCodes.GRPC_ATTACHED_FAILED_NOT_CONNECTED);
          }
        }
      });
    }

    @Override
    public void update(String viewerState) {
      long updateId = table.id();
      executor.execute(new NamedRunnable("client-update", target) {
        @Override
        public void execute() throws Exception {
          if (upstream != null) {
            upstream.onNext(StreamMessageClient.newBuilder().setId(updateId).setAct(docId).setUpdate(StreamUpdate.newBuilder().setState(viewerState).build()).build());
          }
        }
      });
    }

    @Override
    public void send(String channel, String marker, String message, SeqCallback callback) {
      long sendId = table.id();
      executor.execute(new NamedRunnable("client-send", target, channel) {
        @Override
        public void execute() throws Exception {
          if (upstream != null) {
            table.associate(sendId, callback);
            StreamSend.Builder send = StreamSend.newBuilder().setChannel(channel).setMessage(message);
            if (marker != null) {
              send.setMarker(marker);
            }
            upstream.onNext(StreamMessageClient.newBuilder().setId(sendId).setAct(docId).setSend(send.build()).build());
          } else {
            callback.error(ErrorCodes.GRPC_SEND_FAILED_NOT_CONNECTED);
          }
        }
      });
    }

    @Override
    public void disconnect() {
      executor.execute(new NamedRunnable("client-disconnect", target) {
        @Override
        public void execute() throws Exception {
          if (upstream != null) {
            upstream.onNext(StreamMessageClient.newBuilder().setId(table.id()).setAct(docId).setDisconnect(StreamDisconnect.newBuilder().build()).build());
          }
        }
      });
    }
  }

  private class MultiplexObserver implements StreamObserver<StreamMessageServer> {
    private StreamObserver<StreamMessageClient> upstream = null;

    @Override
    public void onNext(StreamMessageServer message) {
      switch (message.getByTypeCase()) {
        case ESTABLISH:
          executor.execute(new NamedRunnable("client-established", target) {
            @Override
            public void execute() throws Exception {
              if (alive.get()) {
                InstanceClient.this.upstream = MultiplexObserver.this.upstream;
                backoff = 1;
                lifecycle.connected(InstanceClient.this);
                if (monitor != null) {
                  upstream.onNext(StreamMessageClient.newBuilder().setMonitor(RequestHeat.newBuilder().build()).build());
                }
              }
            }
          });
          return;
        case HEAT:
          if (monitor != null) {
            HeatPayload heat = message.getHeat();
            executor.execute(new NamedRunnable("route-heat") {
              @Override
              public void execute() throws Exception {
                monitor.heat(target, heat.getCpu(), heat.getMemory());
              }
            });
          }
        case HEARTBEAT:
          executor.execute(new NamedRunnable("client-heartbeat", target) {
            @Override
            public void execute() {
              lifecycle.heartbeat(InstanceClient.this, message.getHeartbeat().getSpacesList());
            }
          });
          return;
        case DATA:
          executor.execute(new NamedRunnable("client-data", target) {
            @Override
            public void execute() throws Exception {
              Events events = table.documentsOf(message.getId());
              if (events != null) {
                events.delta(message.getData().getDelta());
              }
            }
          });
          return;
        case ERROR:
          executor.execute(new NamedRunnable("client-error", target) {
            @Override
            public void execute() throws Exception {
              table.error(message.getId(), message.getError().getCode());
            }
          });
          return;
        case RESPONSE:
          executor.execute(new NamedRunnable("client-ask-attachment-response", target) {
            @Override
            public void execute() throws Exception {
              table.finishAsk(message.getId(), message.getResponse().getAllowed());
            }
          });
          return;
        case RESULT:
          executor.execute(new NamedRunnable("client-seq-result", target) {
            @Override
            public void execute() throws Exception {
              table.finishSeq(message.getId(), message.getResult().getSeq());
            }
          });
          return;
        case STATUS:
          executor.execute(new NamedRunnable("client-status", target, message.getStatus().getCode().toString()) {
            @Override
            public void execute() throws Exception {
              if (message.getStatus().getCode() == StreamStatusCode.Connected) {
                Events events = table.documentsOf(message.getId());
                if (events != null) {
                  events.connected(new InstanceRemote(message.getId()));
                }
              } else {
                table.disconnectDocument(message.getId());
              }
            }
          });
          return;
      }
    }

    @Override
    public void onError(Throwable throwable) {
      backoff += (rng.nextDouble() * backoff + 1);
      logger.convertedToErrorCode(throwable, ErrorCodes.GRPC_FAILURE);
      onCompleted();
    }

    @Override
    public void onCompleted() {
      executor.execute(new NamedRunnable("client-completed", target) {
        @Override
        public void execute() throws Exception {
          boolean send = InstanceClient.this.upstream != null;
          downstream = null;
          upstream = null;
          InstanceClient.this.upstream = null;
          table.kill();
          if (send) {
            lifecycle.disconnected(InstanceClient.this);
          }
          if (alive.get()) {
            executor.schedule(new NamedRunnable("client-reconnecting", target) {
              @Override
              public void execute() throws Exception {
                downstream = new MultiplexObserver();
                downstream.upstream = stub.multiplexedProtocol(downstream);
              }
            }, backoff);
          }
        }
      });
    }
  }
  */
}

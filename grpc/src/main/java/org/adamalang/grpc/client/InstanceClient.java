/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import io.grpc.ChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.grpc.proto.*;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a managed client that makes talking to the gRPC server nice */
public class InstanceClient implements AutoCloseable {
  public final SimpleExecutor executor;
  public final AdamaGrpc.AdamaStub stub;
  public final String target;
  private final ClientMetrics metrics;
  private final HeatMonitor monitor;
  private final Lifecycle lifecycle;
  private final ManagedChannel channel;
  private final Random rng;
  private final ExceptionLogger logger;
  private final AtomicBoolean alive;
  private final CallbackTable table;
  private StreamObserver<StreamMessageClient> upstream;
  private MultiplexObserver downstream;
  private int backoff;

  public InstanceClient(MachineIdentity identity, ClientMetrics metrics, HeatMonitor monitor, String target, SimpleExecutor executor, Lifecycle lifecycle, ExceptionLogger logger) throws Exception {
    this.executor = executor;
    this.target = target;
    this.metrics = metrics;
    this.monitor = monitor;
    this.table = new CallbackTable();
    ChannelCredentials credentials = TlsChannelCredentials.newBuilder().keyManager(identity.getCert(), identity.getKey()).trustManager(identity.getTrust()).build();
    this.channel = NettyChannelBuilder.forTarget(target, credentials).build();
    this.stub = AdamaGrpc.newStub(channel);
    this.rng = new Random();
    this.upstream = null;
    this.downstream = null;
    this.lifecycle = lifecycle;
    this.logger = logger;
    this.alive = new AtomicBoolean(true);
    this.backoff = 1;
    executor.execute(new NamedRunnable("client-setup", target) {
      @Override
      public void execute() throws Exception {
        downstream = new MultiplexObserver();
        downstream.upstream = stub.multiplexedProtocol(downstream);
      }
    });
  }

  /** block the current thread to ensure the client is connected right now */
  public boolean ping(int timeLimit) throws Exception {
    int backoff = 5;
    int time = 0;
    do {
      AtomicBoolean success = new AtomicBoolean(false);
      CountDownLatch latch = new CountDownLatch(1);
      this.stub.ping(PingRequest.newBuilder().build(), new StreamObserver<>() {
        @Override
        public void onNext(PingResponse pingResponse) {
          success.set(true);
          latch.countDown();
        }

        @Override
        public void onError(Throwable throwable) {
          latch.countDown();
        }

        @Override
        public void onCompleted() {
          latch.countDown();
        }
      });
      latch.await(timeLimit - time, TimeUnit.MILLISECONDS);
      if (success.get()) {
        return true;
      }
      Thread.sleep(backoff);
      time += backoff;
      backoff += Math.round(backoff * rng.nextDouble() + 1);
    } while (time < timeLimit);
    return false;
  }

  /** create a document */
  public void create(String agent, String authority, String space, String key, String entropy, String arg, CreateCallback callback) {
    CreateRequest.Builder builder = CreateRequest.newBuilder().setAgent(agent).setAuthority(authority).setSpace(space).setKey(key).setArg(arg);
    if (entropy != null) {
      builder.setEntropy(entropy);
    }
    CreateRequest request = builder.build();
    stub.create(request, CreateCallback.WRAP(callback, logger));
  }

  public void reflect(String space, String key, Callback<String> callback) {
    executor.execute(new NamedRunnable("reflect") {
      @Override
      public void execute() throws Exception {
        stub.reflect(ReflectRequest.newBuilder().setSpace(space).setKey(key).build(), new StreamObserver<ReflectResponse>() {
          @Override
          public void onNext(ReflectResponse reflectResponse) {
            callback.success(reflectResponse.getSchema());
          }

          @Override
          public void onError(Throwable throwable) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.GRPC_REFLECT_UNKNOWN_EXCEPTION, throwable, logger));
          }

          @Override
          public void onCompleted() {
          }
        });
      }
    });
  }

  public void startMeteringExchange(MeteringStream meteringStream) {
    executor.execute(new NamedRunnable("metering-exchange") {
      @Override
      public void execute() throws Exception {
        MeteringObserver observer = new MeteringObserver(meteringStream);
        observer.start(stub.meteringExchange(observer));
      }
    });
  }

  public void scanDeployments(String space, ScanDeploymentCallback callback) {
    stub.scanDeployments(ScanDeploymentsRequest.newBuilder().setSpace(space).build(), ScanDeploymentCallback.WRAP(callback));
  }

  /** connect to a document */
  public long connect(String agent, String authority, String space, String key, String viewerState, Events events) {
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
  }

  @Override
  public void close() {
    executor.execute(new NamedRunnable("client-close", target) {
      @Override
      public void execute() throws Exception {
        alive.set(false);
        if (downstream != null) {
          downstream.onCompleted();
        }
        if (upstream != null) {
          upstream.onCompleted();
        }
      }
    });
  }

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
}

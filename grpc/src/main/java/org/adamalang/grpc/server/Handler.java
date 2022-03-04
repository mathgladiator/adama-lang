/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.server;

import io.grpc.stub.StreamObserver;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.runtime.sys.metering.MeterReading;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** the handler to answer the client's call */
public class Handler extends AdamaGrpc.AdamaImplBase {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(Handler.class);
  private final ServerNexus nexus;
  private final SimpleExecutor[] executors;
  private final Random rng;

  public Handler(ServerNexus nexus) {
    this.nexus = nexus;
    this.executors = new SimpleExecutor[nexus.handlerThreads];
    for (int k = 0; k < nexus.handlerThreads; k++) {
      executors[k] = SimpleExecutor.create("handler-" + k);
    }
    this.rng = new Random();
  }

  @Override
  public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
    responseObserver.onNext(PingResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
    nexus.service.create(new NtClient(request.getAgent(), request.getAuthority()), new Key(request.getSpace(), request.getKey()), request.getArg(), fixEntropy(request.getEntropy()), nexus.metrics.server_create.wrap(new Callback<>() {
      @Override
      public void success(Void value) {
        responseObserver.onNext(CreateResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responseObserver.onNext(CreateResponse.newBuilder().setSuccess(false).setFailureReason(ex.code).build());
        responseObserver.onCompleted();
      }
    }));
  }

  @Override
  public void reflect(ReflectRequest request, StreamObserver<ReflectResponse> responseObserver) {
    nexus.service.reflect(new Key(request.getSpace(), request.getKey()), nexus.metrics.server_reflect.wrap(new Callback<>() {
      @Override
      public void success(String schema) {
        responseObserver.onNext(ReflectResponse.newBuilder().setSchema(schema).build());
        responseObserver.onCompleted();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responseObserver.onError(ex);
      }
    }));
  }

  @Override
  public StreamObserver<StreamMessageClient> multiplexedProtocol(StreamObserver<StreamMessageServer> responseObserver) {
    nexus.metrics.server_handlers_active.up();
    SimpleExecutor executor = executors[rng.nextInt(executors.length)];
    ConcurrentHashMap<Long, LazyCoreStream> streams = new ConcurrentHashMap<>();
    AtomicBoolean alive = new AtomicBoolean(true);
    responseObserver.onNext(StreamMessageServer.newBuilder().setEstablish(Establish.newBuilder().build()).build());
    nexus.meteringPubSub.subscribe((bills) -> {
      if (alive.get()) {
        ArrayList<String> spaces = new ArrayList<>();
        for (MeterReading meterReading : bills) {
          spaces.add(meterReading.space);
        }
        executor.execute(new NamedRunnable("handler-send-heartbeat") {
          @Override
          public void execute() throws Exception {
            responseObserver.onNext(StreamMessageServer.newBuilder().setHeartbeat(InventoryHeartbeat.newBuilder().addAllSpaces(spaces).build()).build());
          }
        });
      }
      return alive.get();
    });
    return new StreamObserver<>() {
      @Override
      public void onNext(StreamMessageClient payload) {
        if (payload.hasMonitor()) {
          executor.schedule(new NamedRunnable("measure-heat") {
            @Override
            public void execute() throws Exception {
              if (alive.get()) {
                responseObserver.onNext(StreamMessageServer.newBuilder().setHeat(HeatPayload.newBuilder().setCpu(MachineHeat.cpu()).setMemory(MachineHeat.memory()).build()).build());
                executor.schedule(this, 100);
              }
            }
          }, 250);
          return;
        }
        long id = payload.getId();
        LazyCoreStream stream = null;
        if (payload.hasAct()) {
          stream = streams.get(payload.getAct());
          if (stream == null) {
            executor.execute(new NamedRunnable("error-no-stream-for-act") {
              @Override
              public void execute() throws Exception {
                responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ErrorCodes.GRPC_COMMON_FAILED_TO_FIND_STREAM_USING_GIVEN_ACT).build()).build());
              }
            });
            return;
          }
        }
        switch (payload.getByTypeCase()) {
          case CONNECT: {
            nexus.metrics.server_witness_packet_connect.run();
            StreamConnect connect = payload.getConnect();
            LazyCoreStream lazy = new LazyCoreStream(executor);
            streams.put(id, lazy);
            nexus.service.connect(new NtClient(connect.getAgent(), connect.getAuthority()), new Key(connect.getSpace(), connect.getKey()), connect.getState(), new Streamback() {
              @Override
              public void onSetupComplete(CoreStream stream) {
                executor.execute(new NamedRunnable("connected") {
                  @Override
                  public void execute() throws Exception {
                    if (alive.get()) {
                      lazy.ready(stream);
                    } else {
                      stream.disconnect();
                    }
                  }
                });
              }

              @Override
              public void status(StreamStatus status) {
                StreamStatusCode code = status == StreamStatus.Connected ? StreamStatusCode.Connected : StreamStatusCode.Disconnected;
                executor.execute(new NamedRunnable("handler-send-status") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setStatus(org.adamalang.grpc.proto.StreamStatus.newBuilder().setCode(code).build()).build());
                    if (status == StreamStatus.Disconnected) {
                      streams.remove(id);
                    }
                  }
                });
              }

              @Override
              public void next(String data) {
                executor.execute(new NamedRunnable("handler-send-data") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setData(StreamData.newBuilder().setDelta(data).build()).build());
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException exception) {
                executor.execute(new NamedRunnable("handler-send-failure") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(exception.code).build()).build());
                    streams.remove(id);
                  }
                });
              }
            });
            return;
          }
          case ASK: {
            stream.execute(new ItemAction<>(ErrorCodes.GRPC_STREAM_ASK_TIMEOUT, ErrorCodes.GRPC_STREAM_ASK_REJECTED, nexus.metrics.server_stream_ask.start()) {
              @Override
              protected void executeNow(CoreStream stream) {
                stream.canAttach(new Callback<>() {
                  @Override
                  public void success(Boolean value) {
                    executor.execute(new NamedRunnable("handler-can-attach-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResponse(StreamAskAttachmentResponse.newBuilder().setAllowed(value).build()).build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    executor.execute(new NamedRunnable("handler-can-attach-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ex.code).build()).build());
                      }
                    });
                  }
                });
              }

              @Override
              protected void failure(int code) {
                executor.execute(new NamedRunnable("handler-can-attach-failure-queue") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(code).build()).build());
                  }
                });
              }
            });

            return;
          }
          case ATTACH: {
            StreamAttach attach = payload.getAttach();
            NtAsset asset = new NtAsset(attach.getId(), attach.getFilename(), attach.getContentType(), attach.getSize(), attach.getMd5(), attach.getSha384());
            stream.execute(new ItemAction<CoreStream>(ErrorCodes.GRPC_STREAM_ATTACH_TIMEOUT, ErrorCodes.GRPC_STREAM_ATTACH_REJECTED, nexus.metrics.server_stream_attach.start()) {
              @Override
              protected void executeNow(CoreStream stream) {
                stream.attach(asset, new Callback<>() {
                  @Override
                  public void success(Integer value) {
                    executor.execute(new NamedRunnable("handler-attach-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResult(StreamSeqResult.newBuilder().setSeq(value).build()).build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    executor.execute(new NamedRunnable("handler-attach-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(ex.code).build()).build());
                      }
                    });
                  }
                });
              }

              @Override
              protected void failure(int code) {
                executor.execute(new NamedRunnable("handler-attach-failure-queue") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(code).build()).build());
                  }
                });
              }
            });
            return;
          }
          case UPDATE: {
            stream.execute(new ItemAction<>(ErrorCodes.GRPC_STREAM_UPDATE_TIMEOUT, ErrorCodes.GRPC_STREAM_UPDATE_REJECTED, nexus.metrics.server_stream_update.start()) {
              @Override
              protected void executeNow(CoreStream item) {
                item.updateView(new JsonStreamReader(payload.getUpdate().getState()));
              }

              @Override
              protected void failure(int code) {
                // don't care
              }
            });
            return;
          }
          case SEND: {
            stream.execute(new ItemAction<>(ErrorCodes.GRPC_STREAM_SEND_TIMEOUT, ErrorCodes.GRPC_STREAM_SEND_REJECTED, nexus.metrics.server_stream_send.start()) {
              @Override
              protected void executeNow(CoreStream stream) {
                StreamSend send = payload.getSend();
                String marker = null;
                if (send.hasMarker()) {
                  marker = send.getMarker();
                }
                stream.send(send.getChannel(), marker, send.getMessage(), new Callback<>() {
                  @Override
                  public void success(Integer value) {
                    executor.execute(new NamedRunnable("stream-send-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setResult(StreamSeqResult.newBuilder().setSeq(value).build()).build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException exception) {
                    executor.execute(new NamedRunnable("stream-send-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(exception.code).build()).build());
                      }
                    });
                  }
                });
              }

              @Override
              protected void failure(int code) {
                executor.execute(new NamedRunnable("stream-send-failure-queue") {
                  @Override
                  public void execute() throws Exception {
                    responseObserver.onNext(StreamMessageServer.newBuilder().setId(id).setError(StreamError.newBuilder().setCode(code).build()).build());
                  }
                });
              }
            });

            return;
          }
          case DISCONNECT: {
            streams.remove(payload.getAct());
            LazyCoreStream _stream = stream;
            executor.execute(new NamedRunnable("stream-send-failure-queue") {
              @Override
              public void execute() throws Exception {
                _stream.kill();
              }
            });
            return;
          }
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LOGGER.convertedToErrorCode(throwable, ErrorCodes.GRPC_HANDLER_EXCEPTION);
        onCompleted();
      }

      @Override
      public void onCompleted() {
        if (alive.get()) {
          nexus.metrics.server_handlers_active.down();
          alive.set(false);
        }
        executor.execute(new NamedRunnable("stream-on-completed") {
          @Override
          public void execute() throws Exception {
            for (LazyCoreStream stream : streams.values()) {
              stream.kill();
            }
            streams.clear();
            responseObserver.onCompleted();
          }
        });
      }
    };
  }

  @Override
  public void scanDeployments(ScanDeploymentsRequest request, StreamObserver<ScanDeploymentsResponse> responseObserver) {
    try {
      nexus.scanForDeployments.accept(request.getSpace());
      responseObserver.onNext(ScanDeploymentsResponse.newBuilder().build());
      responseObserver.onCompleted();
    } catch (Exception ex) {
      LOGGER.convertedToErrorCode(ex, ErrorCodes.GRPC_HANDLER_SCAN_EXCEPTION);
      responseObserver.onError(ex);
    }
  }

  @Override
  public StreamObserver<MeteringForward> meteringExchange(StreamObserver<MeteringReverse> responseObserver) {
    SimpleExecutor executor = executors[rng.nextInt(executors.length)];
    return new StreamObserver<>() {
      private boolean sentComplete = false;

      @Override
      public void onNext(MeteringForward forward) {
        executor.execute(new NamedRunnable("processing-metering-next") {
          @Override
          public void execute() throws Exception {
            switch (forward.getOperationCase()) {
              case BEGIN: {
                String id = nexus.meteringBatchMaker.getNextAvailableBatchId();
                if (id != null) {
                  String batch = nexus.meteringBatchMaker.getBatch(id);
                  responseObserver.onNext(MeteringReverse.newBuilder().setFound(MeteringBatchFound.newBuilder().setId(id).setBatch(batch).build()).build());
                } else {
                  sendCompleteWhileInExecutor();
                }
                return;
              }
              case REMOVE: {
                MeteringDeleteBatch deleteBill = forward.getRemove();
                nexus.meteringBatchMaker.deleteBatch(deleteBill.getId());
                responseObserver.onNext(MeteringReverse.newBuilder().setRemoved(MeteringBatchRemoved.newBuilder().build()).build());
                return;
              }
            }
          }
        });
      }

      private void sendCompleteWhileInExecutor() {
        if (!sentComplete) {
          sentComplete = true;
          responseObserver.onCompleted();
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LOGGER.convertedToErrorCode(throwable, ErrorCodes.GRPC_METERING_UNEXPECTED_ERROR);
        onCompleted();
      }

      @Override
      public void onCompleted() {
        executor.execute(new NamedRunnable("processing-client-closed") {
          @Override
          public void execute() throws Exception {
            sendCompleteWhileInExecutor();
          }
        });
      }
    };
  }

  public static String fixEntropy(String entropy) {
    if ("".equals(entropy)) {
      return null;
    }
    try {
      Long.parseLong(entropy);
      return entropy;
    } catch (NumberFormatException nfe) {
      return "" + entropy.hashCode();
    }
  }

  private class LazyCoreStream {
    private final ItemQueue<CoreStream> queue;
    private CoreStream stream;
    private boolean alive;

    public LazyCoreStream(SimpleExecutor executor) {
      this.stream = null;
      this.queue = new ItemQueue<>(executor, 32, 2500);
      this.alive = true;
    }

    public void execute(ItemAction<CoreStream> task) {
      queue.add(task);
    }

    public void ready(CoreStream stream) {
      if (this.alive) {
        this.stream = stream;
        queue.ready(stream);
      } else {
        stream.disconnect();
        queue.unready();
      }
    }

    public void kill() {
      if (this.alive) {
        alive = false;
        if (stream != null) {
          stream.disconnect();
          stream = null;
        }
        queue.unready();
      }
    }
  }
}

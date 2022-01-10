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
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.billing.Bill;
import org.adamalang.runtime.sys.CoreStream;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** the handler to answer the client's call */
public class Handler extends AdamaGrpc.AdamaImplBase {
  private final static ExceptionLogger LOGGER = ExceptionLogger.FOR(Handler.class);
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
    nexus.service.create(
        new NtClient(request.getAgent(), request.getAuthority()),
        new Key(request.getSpace(), request.getKey()),
        request.getArg(),
        fixEntropy(request.getEntropy()),
        new Callback<>() {
          @Override
          public void success(Void value) {
            responseObserver.onNext(CreateResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responseObserver.onNext(
                CreateResponse.newBuilder().setSuccess(false).setFailureReason(ex.code).build());
            responseObserver.onCompleted();
          }
        });
  }

  private static String fixEntropy(String entropy) {
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

  @Override
  public StreamObserver<BillingForward> billingExchange(StreamObserver<BillingReverse> responseObserver) {
    SimpleExecutor executor = executors[rng.nextInt(executors.length)];
    return new StreamObserver<BillingForward>() {
      private boolean sentComplete = false;
      @Override
      public void onNext(BillingForward billingForward) {
        executor.execute(new NamedRunnable("processing-billing-next") {
          @Override
          public void execute() throws Exception {
            switch (billingForward.getOperationCase()) {
              case BEGIN: {
                String idFound = "";
                String batchFound = "";
                // IF FOUND
                responseObserver.onNext(BillingReverse.newBuilder().setFound(BillingBatchFound.newBuilder().setId(idFound).setBatch(batchFound).build()).build());
                // ELSE SEND onComplete ;; sendCompleteWhileInExecutor();
                // TODO: find a billing batch
              }
              case REMOVE: {
                BillingDeleteBill deleteBill = billingForward.getRemove();
                // TODO: Issue the delete
                responseObserver.onNext(BillingReverse.newBuilder().setRemoved(BillingBatchRemoved.newBuilder().build()).build());
              }
            }
          }
        });
      }

      private void sendCompleteWhileInExecutor() {
        if (!sentComplete) {
          sentComplete = true;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LOGGER.convertedToErrorCode(throwable, ErrorCodes.GRPC_BILLING_UNEXPECTED_ERROR);
      }

      @Override
      public void onCompleted() {
        executor.execute(new NamedRunnable("processing-billing-next") {
          @Override
          public void execute() throws Exception {

            sendCompleteWhileInExecutor();
          }
        });
      }
    };
  }

  @Override
  public StreamObserver<StreamMessageClient> multiplexedProtocol(
      StreamObserver<StreamMessageServer> responseObserver) {
    SimpleExecutor executor = executors[rng.nextInt(executors.length)];

    ConcurrentHashMap<Long, CoreStream> streams = new ConcurrentHashMap<>();
    AtomicBoolean alive = new AtomicBoolean(true);
    responseObserver.onNext(
        StreamMessageServer.newBuilder().setEstablish(Establish.newBuilder().build()).build());
    nexus.billingPubSub.subscribe(
        (bills) -> {
          if (alive.get()) {
            ArrayList<String> spaces = new ArrayList<>();
            for (Bill bill : bills) {
              spaces.add(bill.space);
            }
            executor.execute(new NamedRunnable("handler-send-heartbeat") {
              @Override
              public void execute() throws Exception {
                responseObserver.onNext(StreamMessageServer.newBuilder()
                                   .setHeartbeat(InventoryHeartbeat.newBuilder().addAllSpaces(spaces).build())
                                   .build());
              }
            });
          }
          return alive.get();
        });
    return new StreamObserver<>() {
      @Override
      public void onNext(StreamMessageClient payload) {
        long id = payload.getId();
        CoreStream stream = null;
        if (payload.hasAct()) {
          stream = streams.get(payload.getAct());
          if (stream == null) {
            executor.execute(new NamedRunnable("error-no-stream-for-act") {
              @Override
              public void execute() throws Exception {
                responseObserver.onNext(StreamMessageServer.newBuilder()
                                   .setId(id)
                                   .setError(
                                       StreamError.newBuilder()
                                                  .setCode(ErrorCodes.GRPC_COMMON_FAILED_TO_FIND_STREAM_USING_GIVEN_ACT)
                                                  .build())
                                   .build());
              }
            });
            return;
          }
        }
        switch (payload.getByTypeCase()) {
          case CONNECT:
            StreamConnect connect = payload.getConnect();
            nexus.service.connect(
                new NtClient(connect.getAgent(), connect.getAuthority()),
                new Key(connect.getSpace(), connect.getKey()),
                new Streamback() {
                  @Override
                  public void onSetupComplete(CoreStream stream) {
                    streams.put(id, stream);
                  }

                  @Override
                  public void status(StreamStatus status) {
                    StreamStatusCode code = status == StreamStatus.Connected ? StreamStatusCode.Connected : StreamStatusCode.Disconnected;
                    executor.execute(new NamedRunnable("handler-send-status") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setStatus(
                                                   org.adamalang.grpc.proto.StreamStatus.newBuilder()
                                                                                        .setCode(code)
                                                                                        .build())
                                               .build());
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
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setData(StreamData.newBuilder().setDelta(data).build())
                                               .build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException exception) {
                    executor.execute(new NamedRunnable("handler-send-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setError(StreamError.newBuilder().setCode(exception.code).build())
                                               .build());
                        streams.remove(id);
                      }
                    });
                  }
                });
            return;
          case ASK:
            stream.canAttach(
                new Callback<>() {
                  @Override
                  public void success(Boolean value) {
                    executor.execute(new NamedRunnable("handler-can-attach-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setResponse(
                                                   StreamAskAttachmentResponse.newBuilder().setAllowed(value).build())
                                               .build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    executor.execute(new NamedRunnable("handler-can-attach-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setError(StreamError.newBuilder().setCode(ex.code).build())
                                               .build());
                      }
                    });
                  }
                });
            return;
          case ATTACH:
            StreamAttach attach = payload.getAttach();
            NtAsset asset =
                new NtAsset(
                    attach.getId(),
                    attach.getFilename(),
                    attach.getContentType(),
                    attach.getSize(),
                    attach.getMd5(),
                    attach.getSha384());
            stream.attach(
                asset,
                new Callback<>() {
                  @Override
                  public void success(Integer value) {
                    executor.execute(new NamedRunnable("handler-attach-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setResult(StreamSeqResult.newBuilder().setSeq(value).build())
                                               .build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    executor.execute(new NamedRunnable("handler-attach-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setError(StreamError.newBuilder().setCode(ex.code).build())
                                               .build());
                      }
                    });
                  }
                });
            return;
          case SEND:
            StreamSend send = payload.getSend();
            String marker = null;
            if (send.hasMarker()) {
              marker = send.getMarker();
            }
            stream.send(
                send.getChannel(),
                marker,
                send.getMessage(),
                new Callback<>() {
                  @Override
                  public void success(Integer value) {
                    executor.execute(new NamedRunnable("stream-send-success") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setResult(StreamSeqResult.newBuilder().setSeq(value).build())
                                               .build());
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException exception) {
                    executor.execute(new NamedRunnable("stream-send-failure") {
                      @Override
                      public void execute() throws Exception {
                        responseObserver.onNext(
                            StreamMessageServer.newBuilder()
                                               .setId(id)
                                               .setError(StreamError.newBuilder().setCode(exception.code).build())
                                               .build());
                      }
                    });
                  }
                });
            return;
          case DISCONNECT:
            stream.disconnect();
            streams.remove(payload.getAct());
            return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LOGGER.convertedToErrorCode(throwable, -1);
        onCompleted();
      }

      @Override
      public void onCompleted() {
        alive.set(false);
        for (CoreStream stream : streams.values()) {
          stream.disconnect();
        }
        executor.execute(new NamedRunnable("stream-on-completed") {
          @Override
          public void execute() throws Exception {
            responseObserver.onCompleted();
          }
        });
      }
    };
  }

  @Override
  public void scanDeployments(
      ScanDeploymentsRequest request, StreamObserver<ScanDeploymentsResponse> responseObserver) {
    try {
      nexus.scanForDeployments.accept(request.getSpace());
      responseObserver.onNext(ScanDeploymentsResponse.newBuilder().build());
      responseObserver.onCompleted();
    } catch (Exception ex) {
      LOGGER.convertedToErrorCode(ex, -1);
      responseObserver.onError(ex);
    }
  }
}

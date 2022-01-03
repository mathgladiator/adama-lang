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
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.BillingPubSub;
import org.adamalang.runtime.sys.CoreStream;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** the handler to answer the client's call */
public class Handler extends AdamaGrpc.AdamaImplBase {

  private final ServerNexus nexus;

  public Handler(ServerNexus nexus) {
    this.nexus = nexus;
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
  public StreamObserver<StreamMessageClient> multiplexedProtocol(
      StreamObserver<StreamMessageServer> responseObserver) {
    ConcurrentHashMap<Long, CoreStream> streams = new ConcurrentHashMap<>();
    AtomicBoolean alive = new AtomicBoolean(true);
    responseObserver.onNext(
        StreamMessageServer.newBuilder().setEstablish(Establish.newBuilder().build()).build());
    nexus.billingPubSub.subscribe(
        (bills) -> {
          if (alive.get()) {
            ArrayList<String> spaces = new ArrayList<>();
            for (BillingPubSub.Bill bill : bills) {
              spaces.add(bill.space);
            }
            // TODO: discover the thread safety of this since this is going to be on a different
            // writer thread
            responseObserver.onNext(
                StreamMessageServer.newBuilder()
                    .setHeartbeat(InventoryHeartbeat.newBuilder().addAllSpaces(spaces).build())
                    .build());
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
            responseObserver.onNext(
                StreamMessageServer.newBuilder()
                    .setId(id)
                    .setError(
                        StreamError.newBuilder()
                            .setCode(ErrorCodes.GRPC_COMMON_FAILED_TO_FIND_STREAM_USING_GIVEN_ACT)
                            .build())
                    .build());
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
                    StreamStatusCode code = StreamStatusCode.Connected;
                    switch (status) {
                      case Connected:
                        code = StreamStatusCode.Connected;
                        break;
                      case Disconnected:
                        code = StreamStatusCode.Disconnected;
                        break;
                    }
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

                  @Override
                  public void next(String data) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setData(StreamData.newBuilder().setDelta(data).build())
                            .build());
                  }

                  @Override
                  public void failure(ErrorCodeException exception) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setError(StreamError.newBuilder().setCode(exception.code).build())
                            .build());
                    streams.remove(id);
                  }
                });
            return;
          case ASK:
            stream.canAttach(
                new Callback<>() {
                  @Override
                  public void success(Boolean value) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setResponse(
                                StreamAskAttachmentResponse.newBuilder().setAllowed(value).build())
                            .build());
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setError(StreamError.newBuilder().setCode(ex.code).build())
                            .build());
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
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setResult(StreamSeqResult.newBuilder().setSeq(value).build())
                            .build());
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setError(StreamError.newBuilder().setCode(ex.code).build())
                            .build());
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
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setResult(StreamSeqResult.newBuilder().setSeq(value).build())
                            .build());
                  }

                  @Override
                  public void failure(ErrorCodeException exception) {
                    responseObserver.onNext(
                        StreamMessageServer.newBuilder()
                            .setId(id)
                            .setError(StreamError.newBuilder().setCode(exception.code).build())
                            .build());
                  }
                });
            return;
          case DISCONNECT:
            System.err.println("disconnect");
            stream.disconnect();
            streams.remove(payload.getAct());
            return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        // TODO: log throwable
        onCompleted();
      }

      @Override
      public void onCompleted() {
        alive.set(false);
        for (CoreStream stream : streams.values()) {
          stream.disconnect();
        }
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public void scanDeployments(
      ScanDeploymentsRequest request, StreamObserver<ScanDeploymentsResponse> responseObserver) {
    try {
      nexus.scanForDeployments.run();
      responseObserver.onNext(ScanDeploymentsResponse.newBuilder().build());
      responseObserver.onCompleted();
    } catch (Exception ex) {
      responseObserver.onError(ex);
    }
  }
}

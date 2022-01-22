/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.mocks;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.stub.StreamObserver;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.proto.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NaughtyServer extends AdamaGrpc.AdamaImplBase implements AutoCloseable {
  private final Server server;
  private final NaughtyBits bits;
  public final MachineIdentity identity;
  public final SimpleExecutor clientExecutor;

  public static class NaughtyBits {
    public int port;
    public boolean sendEstablish;
    public boolean happyBillingDemo;

    public NaughtyBits() {
     port = 12121;
     sendEstablish = true;
      happyBillingDemo = true;
    }

    public NaughtyBits establish(boolean sendEstablish) {
      this.sendEstablish = sendEstablish;
      return this;
    }

    public NaughtyBits billing(boolean happyBillingDemo) {
      this.happyBillingDemo = happyBillingDemo;
      return this;
    }

    public NaughtyBits port(int port) {
      this.port = port;
      return this;
    }

    public NaughtyServer build() throws Exception {
      return new NaughtyServer(port, this);
    }
  }

  public static NaughtyBits start() {
    return new NaughtyBits();
  }

  public NaughtyServer(int port, NaughtyBits bits) throws Exception {
    this.bits = bits;
    this.identity = MachineIdentity.fromFile(TestBed.prefixForLocalhost());
    this.server = NettyServerBuilder.forPort(port)
                      .addService(this)
                      .sslContext(
                          GrpcSslContexts //
                                          .forServer(identity.getCert(), identity.getKey()) //
                                          .trustManager(identity.getTrust()) //
                                          .clientAuth(ClientAuth.REQUIRE) //
                                          .build())
                      .build();
    this.server.start();
    this.clientExecutor = SimpleExecutor.create("client-space");
  }

  @Override
  public void reflect(ReflectRequest request, StreamObserver<ReflectResponse> responseObserver) {
    responseObserver.onError(new NullPointerException());
  }

  @Override
  public StreamObserver<StreamMessageClient> multiplexedProtocol(StreamObserver<StreamMessageServer> responseObserver) {
    if (bits.sendEstablish) {
      responseObserver.onNext(
          StreamMessageServer.newBuilder().setEstablish(Establish.newBuilder().build()).build());
    }
    return new StreamObserver<StreamMessageClient>() {
      @Override
      public void onNext(StreamMessageClient streamMessageClient) {

      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {

      }
    };
  }

  @Override
  public StreamObserver<BillingForward> billingExchange(StreamObserver<BillingReverse> responseObserver) {
    if (bits.happyBillingDemo) {
      AtomicInteger counts = new AtomicInteger(5);
      return new StreamObserver<BillingForward>() {
        @Override
        public void onNext(BillingForward billingForward) {
          switch (billingForward.getOperationCase()) {
            case BEGIN:
              if (counts.getAndDecrement() > 0) {
                responseObserver.onNext(BillingReverse.newBuilder().setFound(BillingBatchFound.newBuilder().setId("id").setBatch("batch:" + counts.get()).build()).build());
              } else {
                responseObserver.onCompleted();
              }
              return;
            case REMOVE:
              responseObserver.onNext(BillingReverse.newBuilder().setRemoved(BillingBatchRemoved.newBuilder().build()).build());
              return;
          }
        }

        @Override
        public void onError(Throwable throwable) {}

        @Override
        public void onCompleted() {}
      };
    } else {
      responseObserver.onError(new NullPointerException());
      return new StreamObserver<BillingForward>() {
        @Override
        public void onNext(BillingForward billingForward) {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {

        }
      };
    }
  }

  @Override
  public void close() throws Exception {
    server.shutdown().awaitTermination(1000, TimeUnit.MILLISECONDS);
    clientExecutor.shutdown();
  }
}

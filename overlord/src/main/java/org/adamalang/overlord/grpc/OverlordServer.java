/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.grpc;

import com.google.protobuf.ByteString;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.stub.StreamObserver;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.proto.*;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.web.contracts.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class OverlordServer extends OverlordGrpc.OverlordImplBase {
  private static final String webRole = "web";
  private final MachineIdentity identity;
  public final SimpleExecutor executor;
  private final int port;
  private final io.grpc.Server server;
  private final HeatTable heatTable;
  private final OverlordMetrics metrics;
  private final HashMap<String, StreamObserver<WebReverse>> connections;
  private final HashMap<String, HttpHandler.HttpResult> httpMap;
  private final Runnable heartbeat;

  public OverlordServer(MachineIdentity identity, int port, HeatTable heatTable, OverlordMetrics metrics, Runnable heartbeat) throws Exception {
    this.identity = identity;
    this.executor = SimpleExecutor.create("overlord-server");
    this.port = port;
    this.heatTable = heatTable;
    this.metrics = metrics;
    this.connections = new HashMap<>();
    this.httpMap = new HashMap<>();
    this.server = NettyServerBuilder.forPort(port).addService(this).sslContext(GrpcSslContexts //
        .forServer(identity.getCert(), identity.getKey()) //
        .trustManager(identity.getTrust()) //
        .clientAuth(ClientAuth.REQUIRE) //
        .build()).build();
    this.server.start();
    this.heartbeat = heartbeat;
    executor.execute(new NamedRunnable("heatbeat") {
      @Override
      public void execute() throws Exception {
        heartbeat.run();
        executor.schedule(this, (int) (50 + 25 * Math.random()));
      }
    });
  }

  public void shutdown() {
    executor.shutdown();
    server.shutdown();
  }

  public void put(String uri, HttpHandler.HttpResult result) {
    executor.execute(new NamedRunnable("got-new-content") {
      @Override
      public void execute() throws Exception {
        httpMap.put(uri, result);
        for (StreamObserver<WebReverse> observer : connections.values()) {
          observer.onNext(WebReverse.newBuilder().setContent(WebContent.newBuilder().setUri(uri).setContentType(result.contentType).setContent(ByteString.copyFrom(result.body)).build()).build());
        }
      }
    });
  }

  @Override
  public StreamObserver<WebForward> web(StreamObserver<WebReverse> responseObserver) {
    return new StreamObserver<WebForward>() {
      String targetName = null;
      @Override
      public void onNext(WebForward webForward) {
        switch (webForward.getOperationCase()) {
          case REGISTER:
            targetName = webForward.getRegister().getName();
            executor.execute(new NamedRunnable("register-web") {
              @Override
              public void execute() throws Exception {
                connections.put(targetName, responseObserver);
                responseObserver.onNext(WebReverse.newBuilder().setRegistered(Registered.newBuilder().build()).build());
                for (Map.Entry<String, HttpHandler.HttpResult> entry : httpMap.entrySet()) {
                  responseObserver.onNext(WebReverse.newBuilder().setContent(WebContent.newBuilder().setUri(entry.getKey()).setContentType(entry.getValue().contentType).setContent(ByteString.copyFrom(entry.getValue().body)).build()).build());
                }
              }
            });
            return;
          case HEAT:
            heatTable.onSample(targetName, webRole, webForward.getHeat().getCpu(), webForward.getHeat().getMemory());
            return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        // TODO: log error as a metric
        onCompleted();
      }

      @Override
      public void onCompleted() {
        if (targetName != null) {
          executor.execute(new NamedRunnable("unregister-web") {
            @Override
            public void execute() throws Exception {
              connections.remove(targetName);
            }
          });
        }
      }
    };
  }
}

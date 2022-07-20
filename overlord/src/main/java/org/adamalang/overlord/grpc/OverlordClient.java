/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.grpc;

import io.grpc.ChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.grpc.proto.*;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.web.contracts.HttpHandler;

public class OverlordClient implements StreamObserver<WebReverse> {
  private final MachineIdentity identity;
  private final SimpleExecutor executor;
  private final int webPort;
  private String currentTarget;
  private ManagedChannel channel;
  private OverlordGrpc.OverlordStub stub;
  private StreamObserver<WebForward> upstream;
  private boolean sendHeat;
  private boolean alive;

  public OverlordClient(MachineIdentity identity, int webPort) {
    this.identity = identity;
    this.webPort = webPort;
    this.executor = SimpleExecutor.create("overlord-http-client");
    this.currentTarget = null;
    this.channel = null;
    this.stub = null;
    this.sendHeat = false;
    this.alive = true;
    executor.schedule(new NamedRunnable("sending-heat") {
      @Override
      public void execute() throws Exception {
        if (upstream != null && sendHeat) {
          upstream.onNext(WebForward.newBuilder().setHeat(SignalHeat.newBuilder().setCpu(MachineHeat.cpu()).setMemory(MachineHeat.memory()).build()).build());
        }
        if (alive) {
          executor.schedule(this, (int) (50 + 50 * Math.random()));
        }
      }
    }, 100);
  }

  public void setTarget(String target) {
    executor.execute(new NamedRunnable("set-target") {
      @Override
      public void execute() throws Exception {
        if (currentTarget != null && !currentTarget.equals(target)) {
          disconnectWhileInExecutor();
        }
        if (target != null && !target.equals(currentTarget)) {
          currentTarget = target;
          connectWhileInExecutor();
        }
      }
    });
  }

  private void disconnectWhileInExecutor() {
    this.currentTarget = null;
    this.sendHeat = false;
    if (this.upstream != null) {
      this.upstream.onCompleted();
      this.upstream = null;
    }
    if (this.channel != null) {
      this.channel.shutdown();
      this.channel = null;
      this.stub = null;
    }
  }

  private void connectWhileInExecutor() throws Exception {
    ChannelCredentials credentials = TlsChannelCredentials.newBuilder().keyManager(identity.getCert(), identity.getKey()).trustManager(identity.getTrust()).build();
    this.channel = NettyChannelBuilder.forTarget(currentTarget, credentials).build();
    this.stub = OverlordGrpc.newStub(channel);
    this.upstream = this.stub.web(this);
    this.upstream.onNext(WebForward.newBuilder().setRegister(Register.newBuilder().setName(identity.ip + ":" + webPort).build()).build());
  }

  @Override
  public void onNext(WebReverse webReverse) {
    switch (webReverse.getOperationCase()) {
      case REGISTERED:
        executor.execute(new NamedRunnable("registered") {
          @Override
          public void execute() throws Exception {
            sendHeat = true;
          }
        });
        return;
      case CONTENT: {
        return;
      }
    }
  }

  @Override
  public void onError(Throwable throwable) {
    onCompleted();
  }

  public void shutdown() {
    setTarget(null);
    executor.execute(new NamedRunnable("shutdown-post-target-null") {
      @Override
      public void execute() throws Exception {
        alive = false;
        executor.shutdown();
      }
    });
  }

  @Override
  public void onCompleted() {
    executor.execute(new NamedRunnable("completed") {
      @Override
      public void execute() throws Exception {
        String targetToRetry = currentTarget;
        disconnectWhileInExecutor();
        executor.schedule(new NamedRunnable("retry") {
          @Override
          public void execute() throws Exception {
            if (currentTarget == null) {
              currentTarget = targetToRetry;
              connectWhileInExecutor();
            }
          }
        }, 2500);
      }
    });
  }
}

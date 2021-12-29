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
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.grpc.proto.*;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** a managed client that makes talking to the gRPC server nice */
public class InstanceClient implements AutoCloseable {
  public final ScheduledExecutorService executor;
  public final AdamaGrpc.AdamaStub stub;
  private final Lifecycle lifecycle;
  private final HashMap<Long, Events> documents;
  private final HashMap<Long, SeqCallback> outstanding;
  private final HashMap<Long, AskAttachmentCallback> asks;
  private final ManagedChannel channel;
  private final Random rng;
  private final ExceptionLogger logger;
  private AtomicLong nextId;
  private StreamObserver<StreamMessageClient> upstream;
  private Observer downstream;
  private boolean alive;
  private int backoff;

  public InstanceClient(
      MachineIdentity identity,
      String target,
      ScheduledExecutorService executor,
      Lifecycle lifecycle,
      ExceptionLogger logger)
      throws Exception {
    ChannelCredentials credentials =
        TlsChannelCredentials.newBuilder()
            .keyManager(identity.getCert(), identity.getKey())
            .trustManager(identity.getTrust())
            .build();
    this.channel = NettyChannelBuilder.forTarget(target, credentials).build();
    this.stub = AdamaGrpc.newStub(channel);
    this.rng = new Random();
    this.executor = executor;
    this.documents = new HashMap<>();
    this.outstanding = new HashMap<>();
    this.asks = new HashMap<>();
    this.nextId = new AtomicLong(1);
    this.upstream = null;
    this.downstream = null;
    this.lifecycle = lifecycle;
    this.logger = logger;
    this.alive = true;
    this.backoff = 1;
    executor.execute(
        () -> {
          downstream = new Observer();
          downstream.upstream = stub.multiplexedProtocol(downstream);
        });
  }

  /** block the current thread to ensure the client is connected right now */
  public boolean ping(int timeLimit) throws Exception {
    AtomicBoolean success = new AtomicBoolean(false);
    int backoff = 5;
    int time = 0;
    do {
      CountDownLatch latch = new CountDownLatch(1);
      this.stub.ping(
          PingRequest.newBuilder().build(),
          new StreamObserver<>() {
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
    } while (!success.get() && time < timeLimit);
    return false;
  }

  /** create a document */
  public void create(
      String agent,
      String authority,
      String space,
      String key,
      String entropy,
      String arg,
      CreateCallback callback) {
    CreateRequest.Builder builder =
        CreateRequest.newBuilder()
            .setAgent(agent)
            .setAuthority(authority)
            .setSpace(space)
            .setKey(key)
            .setArg(arg);
    if (entropy != null) {
      builder.setEntropy(entropy);
    }
    CreateRequest request = builder.build();
    stub.create(request, CreateCallback.WRAP(callback, logger));
  }

  /** connect to a document */
  public void connect(String agent, String authority, String space, String key, Events events) {
    long docId = nextId.getAndIncrement();
    executor.execute(
        () -> {
          if (this.upstream != null) {
            documents.put(docId, events);
            upstream.onNext(
                StreamMessageClient.newBuilder()
                    .setId(docId)
                    .setConnect(
                        StreamConnect.newBuilder()
                            .setAgent(agent)
                            .setAuthority(authority)
                            .setSpace(space)
                            .setKey(key)
                            .build())
                    .build());
          } else {
            events.disconnected();
          }
        });
  }

  @Override
  public void close() {
    executor.execute(
        () -> {
          alive = false;
          if (downstream != null) {
            downstream.onCompleted();
          }
        });
  }

  public class Remote {
    private final long docId;

    public Remote(long docId) {
      this.docId = docId;
    }

    public void canAttach(AskAttachmentCallback callback) {
      long askId = nextId.getAndIncrement();
      executor.execute(
          () -> {
            if (upstream != null) {
              asks.put(askId, callback);
              upstream.onNext(
                  StreamMessageClient.newBuilder()
                      .setId(askId)
                      .setAct(docId)
                      .setAsk(StreamAskAttachmentRequest.newBuilder().build())
                      .build());
            } else {
              callback.error(ErrorCodes.GRPC_ASK_FAILED_NOT_CONNECTED);
            }
          });
    }

    public void attach(
        String id,
        String name,
        String contentType,
        long size,
        String md5,
        String sha384,
        SeqCallback callback) {
      long attachId = nextId.getAndIncrement();
      executor.execute(
          () -> {
            if (upstream != null) {
              outstanding.put(attachId, callback);
              upstream.onNext(
                  StreamMessageClient.newBuilder()
                      .setId(attachId)
                      .setAct(docId)
                      .setAttach(
                          StreamAttach.newBuilder()
                              .setId(id)
                              .setFilename(name)
                              .setContentType(contentType)
                              .setSize(size)
                              .setMd5(md5)
                              .setSha384(sha384)
                              .build())
                      .build());
            } else {
              callback.error(ErrorCodes.GRPC_ATTACHED_FAILED_NOT_CONNECTED);
            }
          });
    }

    public void send(String channel, String marker, String message, SeqCallback callback) {
      long sendId = nextId.getAndIncrement();
      executor.execute(
          () -> {
            if (upstream != null) {
              outstanding.put(sendId, callback);
              StreamSend.Builder send =
                  StreamSend.newBuilder().setChannel(channel).setMessage(message);
              if (marker != null) {
                send.setMarker(marker);
              }
              upstream.onNext(
                  StreamMessageClient.newBuilder()
                      .setId(sendId)
                      .setAct(docId)
                      .setSend(send.build())
                      .build());
            } else {
              callback.error(ErrorCodes.GRPC_SEND_FAILED_NOT_CONNECTED);
            }
          });
    }

    public void disconnect() {
      executor.execute(
          () -> {
            if (upstream != null) {
              upstream.onNext(
                  StreamMessageClient.newBuilder()
                      .setId(nextId.getAndIncrement())
                      .setAct(docId)
                      .setDisconnect(StreamDisconnect.newBuilder().build())
                      .build());
            }
          });
    }
  }

  private class Observer implements StreamObserver<StreamMessageServer> {
    private StreamObserver<StreamMessageClient> upstream = null;

    @Override
    public void onNext(StreamMessageServer message) {
      switch (message.getByTypeCase()) {
        case ESTABLISH:
          executor.execute(
              () -> {
                InstanceClient.this.upstream = Observer.this.upstream;
                backoff = 1;
                lifecycle.connected(InstanceClient.this);
              });
          return;
        case DATA:
          executor.execute(
              () -> {
                Events events = documents.get(message.getId());
                if (events != null) {
                  events.delta(message.getData().getDelta());
                }
              });
          return;
        case ERROR:
          executor.execute(
              () -> {
                Events events = documents.remove(message.getId());
                if (events != null) {
                  events.error(message.getError().getCode());
                  return;
                }
                SeqCallback callback = outstanding.remove(message.getId());
                if (callback != null) {
                  callback.error(message.getError().getCode());
                  return;
                }
                AskAttachmentCallback ask = asks.remove(message.getId());
                if (ask != null) {
                  ask.error(message.getError().getCode());
                  return;
                }
              });
          return;
        case RESPONSE:
          executor.execute(
              () -> {
                AskAttachmentCallback callback = asks.remove(message.getId());
                if (callback != null) {
                  if (message.getResponse().getAllowed()) {
                    callback.allow();
                  } else {
                    callback.reject();
                  }
                }
              });
          return;
        case RESULT:
          executor.execute(
              () -> {
                SeqCallback callback = outstanding.remove(message.getId());
                if (callback != null) {
                  callback.success(message.getResult().getSeq());
                }
              });
          return;
        case STATUS:
          executor.execute(
              () -> {
                if (message.getStatus().getCode() == StreamStatusCode.Connected) {
                  Events events = documents.get(message.getId());
                  if (events != null) {
                    events.connected(new Remote(message.getId()));
                  }
                } else {
                  Events events = documents.remove(message.getId());
                  if (events != null) {
                    events.disconnected();
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
      executor.execute(
          () -> {
            boolean send = InstanceClient.this.upstream != null;
            downstream = null;
            upstream = null;
            InstanceClient.this.upstream = null;
            for (Events events : documents.values()) {
              events.disconnected();
            }
            documents.clear();
            for (AskAttachmentCallback callback : asks.values()) {
              callback.error(ErrorCodes.GRPC_DISCONNECT);
            }
            asks.clear();
            for (SeqCallback callback : outstanding.values()) {
              callback.error(ErrorCodes.GRPC_DISCONNECT);
            }
            outstanding.clear();
            if (send) {
              lifecycle.disconnected(InstanceClient.this);
            }
            if (alive) {
              executor.schedule(
                  () -> {
                    downstream = new Observer();
                    downstream.upstream = stub.multiplexedProtocol(downstream);
                  },
                  backoff,
                  TimeUnit.MILLISECONDS);
            }
          });
    }
  }
}

package org.adamalang.net.client.bidi;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;

public class DocumentExchange extends ServerCodec.StreamDocument implements Callback<ByteStream> {
  public ClientMessage.StreamConnect connectMessage;
  public Events events;
  private ByteStream upstream;

  public DocumentExchange(ClientMessage.StreamConnect connectMessage, Events events) {
    this.connectMessage = connectMessage;
    this.events = events;
  }

  @Override
  public void success(ByteStream upstream) {
    this.upstream = upstream;
    ByteBuf toWrite = upstream.create(connectMessage.agent.length() + connectMessage.authority.length() + connectMessage.viewerState.length() + connectMessage.key.length() + connectMessage.space.length() + connectMessage.origin.length() + 40);
    ClientCodec.write(toWrite, connectMessage);
    upstream.next(toWrite);
    connectMessage = null;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    events.error(ex.code);
  }

  @Override
  public void completed() {

  }

  @Override
  public void error(int errorCode) {
    events.error(errorCode);
  }

  @Override
  public void handle(ServerMessage.StreamSeqResponse payload) {

  }

  @Override
  public void handle(ServerMessage.StreamAskAttachmentResponse payload) {

  }

  @Override
  public void handle(ServerMessage.StreamError payload) {

  }

  @Override
  public void handle(ServerMessage.StreamData payload) {
    System.err.println("data|" + payload.delta);
  }

  @Override
  public void handle(ServerMessage.StreamStatus payload) {
    System.err.println("status|" + payload.code);
  }
}

/*


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
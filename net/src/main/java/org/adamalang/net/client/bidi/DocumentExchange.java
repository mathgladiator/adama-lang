package org.adamalang.net.client.bidi;

import io.netty.buffer.ByteBuf;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class DocumentExchange extends ServerCodec.StreamDocument implements Callback<ByteStream>, Remote {
  public ClientMessage.StreamConnect connectMessage;
  public Events events;
  private ByteStream upstream;
  private int nextOp;
  private HashMap<Integer, Callback<?>> opHandlers;
  private boolean dead;

  public DocumentExchange(ClientMessage.StreamConnect connectMessage, Events events) {
    this.connectMessage = connectMessage;
    this.events = events;
    nextOp = 1;
    opHandlers = new HashMap<>();
    dead = false;
  }

  private synchronized ArrayList<Callback<?>> killWithLock() {
    dead = true;
    ArrayList<Callback<?>> result = new ArrayList<>(opHandlers.values());
    opHandlers.clear();
    return result;
  }

  private void kill() {
    for (Callback<?> callback : killWithLock()) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
    }
  }

  private synchronized int bind(Callback<?> callback) {
    if (dead) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
      return -1;
    }
    int op = nextOp++;
    if (op < 1) {
      op = 1;
      nextOp = 2;
    }
    opHandlers.put(op, callback);
    return op;
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

  private void disconnected() {
    kill();
    events.disconnected();
  }

  @Override
  public void error(int errorCode) {
    events.error(errorCode);
  }

  @Override
  public void handle(ServerMessage.StreamSeqResponse payload) {
    Callback<Integer> handler;
    synchronized (opHandlers) {
      handler = (Callback<Integer>) opHandlers.remove(payload.op);
    }
    if (handler != null) {
      handler.success(payload.seq);
    }
  }

  @Override
  public void handle(ServerMessage.StreamAskAttachmentResponse payload) {
    Callback<Boolean> handler;
    synchronized (opHandlers) {
      handler = (Callback<Boolean>) opHandlers.remove(payload.op);
    }
    if (handler != null) {
      handler.success(payload.allowed);
    }
  }

  @Override
  public void handle(ServerMessage.StreamError payload) {
    Callback<Integer> handler;
    synchronized (opHandlers) {
      handler = (Callback<Integer>) opHandlers.remove(payload.op);
    }
    if (handler != null) {
      handler.failure(new ErrorCodeException(payload.code));
    }
  }

  @Override
  public void handle(ServerMessage.StreamData payload) {
    events.delta(payload.delta);
  }

  @Override
  public void handle(ServerMessage.StreamStatus payload) {
    if (payload.code == 1) {
      events.connected(this);
    } else {
      events.disconnected();
    }
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    int op = bind(callback);
    if (op > 0) {
      ClientMessage.StreamAskAttachmentRequest ask = new ClientMessage.StreamAskAttachmentRequest();
      ask.op = op;
      ByteBuf toWrite = upstream.create(8);
      ClientCodec.write(toWrite, ask);
      upstream.next(toWrite);
    }
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    int op = bind(callback);
    if (op > 0) {
      ClientMessage.StreamAttach attach = new ClientMessage.StreamAttach();
      attach.op = op;
      attach.id = id;
      attach.filename = name;
      attach.contentType = contentType;
      attach.size = size;
      attach.md5 = md5;
      attach.sha384 = sha384;
      ByteBuf toWrite = upstream.create(16 + id.length() + name.length() + contentType.length() + 8 + md5.length() + sha384.length());
      ClientCodec.write(toWrite, attach);
      upstream.next(toWrite);
    }
  }

  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    int op = bind(callback);
    if (op > 0) {
      ClientMessage.StreamSend send = new ClientMessage.StreamSend();
      send.op = op;
      send.channel = channel;
      send.marker = marker;
      send.message = message;
      ByteBuf toWrite = upstream.create(8 + channel.length() + (marker == null ? 4 : marker.length()) + message.length());
      ClientCodec.write(toWrite, send);
      upstream.next(toWrite);
    }
  }

  @Override
  public void update(String viewerState) {
    ClientMessage.StreamUpdate update = new ClientMessage.StreamUpdate();
    update.viewerState = viewerState;
    ByteBuf toWrite = upstream.create(viewerState.length() + 4);
    ClientCodec.write(toWrite, update);
    upstream.next(toWrite);
  }

  @Override
  public void disconnect() {
    kill();
    ClientMessage.StreamDisconnect disconnect = new ClientMessage.StreamDisconnect();
    ByteBuf toWrite = upstream.create(4);
    ClientCodec.write(toWrite, disconnect);
    upstream.next(toWrite);
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
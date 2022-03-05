/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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
  private final HashMap<Integer, Callback<?>> opHandlers;
  private boolean dead;

  public DocumentExchange(ClientMessage.StreamConnect connectMessage, Events events) {
    this.connectMessage = connectMessage;
    this.events = events;
    nextOp = 1;
    opHandlers = new HashMap<>();
    dead = false;
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
    disconnected();
  }

  private void disconnected() {
    kill();
    events.disconnected();
  }

  private void kill() {
    for (Callback<?> callback : killWithLock()) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
    }
  }

  private synchronized ArrayList<Callback<?>> killWithLock() {
    dead = true;
    ArrayList<Callback<?>> result = new ArrayList<>(opHandlers.values());
    opHandlers.clear();
    return result;
  }

  @Override
  public void completed() {
    disconnected();
  }

  @Override
  public void error(int errorCode) {
    events.error(errorCode);
  }

  private synchronized Callback<?> get(int op) {
    return opHandlers.remove(op);
  }
  @Override
  public void handle(ServerMessage.StreamSeqResponse payload) {
    Callback<Integer> handler =  (Callback<Integer>) get(payload.op);
    if (handler != null) {
      handler.success(payload.seq);
    }
  }

  @Override
  public void handle(ServerMessage.StreamAskAttachmentResponse payload) {
    Callback<Boolean> handler =  (Callback<Boolean>) get(payload.op);
    if (handler != null) {
      handler.success(payload.allowed);
    }
  }

  @Override
  public void handle(ServerMessage.StreamError payload) {
    Callback<Integer> handler =  (Callback<Integer>) get(payload.op);
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

  private synchronized int bind(Callback<?> callback) {
    if (dead) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
      return -1;
    }
    int op = nextOp++;
    while (opHandlers.containsKey(op)) {
      op++;
      nextOp++;
    }
    if (op < 1) {
      op = 1;
      nextOp = 2;
    }
    opHandlers.put(op, callback);
    return op;
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

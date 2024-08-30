/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  public static final int MAX_ATTEMPTS_TO_CREATE_OP = 1024;
  private final HashMap<Integer, Callback<?>> opHandlers;
  public ClientMessage.StreamConnect connectMessage;
  public Events events;
  private ByteStream upstream;
  private int nextOp;
  private boolean dead;
  private boolean shouldSendDisconnect;

  public DocumentExchange(ClientMessage.StreamConnect connectMessage, Events events) {
    this.connectMessage = connectMessage;
    this.events = events;
    nextOp = 1;
    opHandlers = new HashMap<>();
    dead = false;
    shouldSendDisconnect = true;
  }

  @Override // From Callback<ByteStream> which happens when the stream is created on both sides
  public void success(ByteStream upstream) {
    this.upstream = upstream;
    ByteBuf toWrite = upstream.create(connectMessage.agent.length() + connectMessage.authority.length() + connectMessage.viewerState.length() + connectMessage.key.length() + connectMessage.space.length() + connectMessage.origin.length() + 40);
    ClientCodec.write(toWrite, connectMessage);
    upstream.next(toWrite);
    connectMessage = null;
  }

  @Override // from Callback<ByteStream>
  public void failure(ErrorCodeException ex) {
    error(ex.code);
  }

  /** internal: kill the exchange and signal all inflight operations */
  private void kill() {
    for (Callback<?> callback : killWithLock()) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
    }
  }

  @Override // From ByteStream (the server can't really complete this except in error modes)
  public void completed() {
    kill();
    if (debounceDisconnect()) {
      events.disconnected();
    }
  }

  /** internal: kill the exchange */
  private synchronized ArrayList<Callback<?>> killWithLock() {
    dead = true;
    ArrayList<Callback<?>> result = new ArrayList<>(opHandlers.values());
    opHandlers.clear();
    return result;
  }

  /** internal: debounce the disconnect signal as it can be originate in multiple ways */
  private synchronized boolean debounceDisconnect() {
    if (shouldSendDisconnect) {
      shouldSendDisconnect = false;
      return true;
    }
    return false;
  }

  @Override // from ByteStream (a stream level problem)
  public void error(int errorCode) {
    if (errorCode != ErrorCodes.NET_DISCONNECT) {
      events.error(errorCode);
      kill();
    } else {
      // the network was disconnected, so treat like a disconnect
      completed();
    }
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamSeqResponse payload) {
    Callback<Integer> handler = (Callback<Integer>) get(payload.op);
    if (handler != null) {
      handler.success(payload.seq);
    }
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamUpdateComplete payload) {
    Callback<Void> handler = (Callback<Void>) get(payload.op);
    if (handler != null) {
      handler.success(null);
    }
  }

  /** internal: get a callback for the inflight operation */
  private synchronized Callback<?> get(int op) {
    return opHandlers.remove(op);
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamAskAttachmentResponse payload) {
    Callback<Boolean> handler = (Callback<Boolean>) get(payload.op);
    if (handler != null) {
      handler.success(payload.allowed);
    }
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamError payload) {
    Callback<Integer> handler = (Callback<Integer>) get(payload.op);
    if (handler != null) {
      handler.failure(new ErrorCodeException(payload.code));
    }
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamData payload) {
    events.delta(payload.delta);
  }

  @Override // From ServerCodec.StreamDocument
  public void handle(ServerMessage.StreamStatus payload) {
    if (payload.code == 1) {
      events.connected(this);
    } else {
      completed();
    }
  }

  @Override // From Remote
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

  /** internal: bind the callback to a local id */
  private synchronized int bind(Callback<?> callback) {
    if (dead) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CONNECTION_DONE));
      return -1;
    }
    int op = nextOp++;
    int attempts = MAX_ATTEMPTS_TO_CREATE_OP;
    while (opHandlers.containsKey(op) && attempts-- > 0) {
      op++;
      nextOp++;
      if (op < 1) {
        op = 1;
        nextOp = 2;
      }
    }
    if (attempts <= 0) {
      callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_FAILED_FINDING_SUBID));
      return -1;
    }
    opHandlers.put(op, callback);
    return op;
  }

  @Override // From Remote
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

  @Override // From Remote
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
  public void password(String password, Callback<Integer> callback) {
    int op = bind(callback);
    if (op > 0) {
      ClientMessage.StreamPassword send = new ClientMessage.StreamPassword();
      send.op = op;
      send.password = password;
      ByteBuf toWrite = upstream.create(8 + password.length());
      ClientCodec.write(toWrite, send);
      upstream.next(toWrite);
    }
  }

  @Override // From Remote
  public void update(String viewerState, Callback<Void> callback) {
    int op = bind(callback);
    if (op > 0) {
      ClientMessage.StreamUpdate update = new ClientMessage.StreamUpdate();
      update.op = op;
      update.viewerState = viewerState;
      ByteBuf toWrite = upstream.create(viewerState.length() + 4);
      ClientCodec.write(toWrite, update);
      upstream.next(toWrite);
    }
  }

  @Override // From Remote
  public void disconnect() {
    kill();
    ClientMessage.StreamDisconnect disconnect = new ClientMessage.StreamDisconnect();
    ByteBuf toWrite = upstream.create(4);
    ClientCodec.write(toWrite, disconnect);
    upstream.next(toWrite);
    upstream.completed();
  }
}

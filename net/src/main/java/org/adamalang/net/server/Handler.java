/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.server;

import io.netty.buffer.ByteBuf;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreStream;

public class Handler implements ByteStream, ClientCodec.HandlerServer, Streamback {
  private final ServerNexus nexus;
  private final ByteStream upstream;
  private CoreStream stream;

  public Handler(ServerNexus nexus, ByteStream upstream) {
    this.nexus = nexus;
    this.upstream = upstream;
  }

  @Override
  public void request(int bytes) {
    // proxy to the appropriate thing; if stream, then send to the core stream
  }

  // IGNORE
  @Override
  public ByteBuf create(int bestGuessForSize) {
    return null;
  }

  @Override
  public void next(ByteBuf buf) {
    ClientCodec.route(buf, this);
  }

  @Override
  public void completed() {
    if (stream != null) {
      stream.disconnect();
      stream = null;
    }
    if (upstream != null) {
      upstream.completed();
    }
  }

  @Override
  public void error(int errorCode) {
    if (stream != null) {
      stream.disconnect();
      stream = null;
    }
  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
  }

  @Override
  public void handle(ClientMessage.RequestHeat payload) {

  }

  @Override
  public void handle(ClientMessage.StreamDisconnect payload) {
    if (stream != null) {
      stream.disconnect();
      stream = null;
    }
  }

  @Override
  public void handle(ClientMessage.StreamAttach payload) {
    if (stream != null) {
      stream.attach(new NtAsset(payload.id, payload.filename, payload.contentType, payload.size, payload.md5, payload.sha384), new Callback<Integer>() {
        @Override
        public void success(Integer seq) {
          ServerMessage.StreamSeqResponse response = new ServerMessage.StreamSeqResponse();
          response.op = payload.op;
          response.seq = seq;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      });
    }
  }

  @Override
  public void handle(ClientMessage.StreamAskAttachmentRequest payload) {
    if (stream != null) {
      stream.canAttach(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          ServerMessage.StreamAskAttachmentResponse response = new ServerMessage.StreamAskAttachmentResponse();
          response.op = payload.op;
          response.allowed = value;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      });
    }
  }

  @Override
  public void handle(ClientMessage.StreamUpdate payload) {
    if (stream != null) {
      stream.updateView(new JsonStreamReader(payload.viewerState));
    }
  }

  @Override
  public void handle(ClientMessage.StreamSend payload) {
    if (stream != null) {
      stream.send(payload.channel, payload.marker, payload.message, new Callback<>() {
        @Override
        public void success(Integer value) {
          ServerMessage.StreamSeqResponse response = new ServerMessage.StreamSeqResponse();
          response.op = payload.op;
          response.seq = value;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      });
    }
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    this.stream = stream;
  }

  @Override
  public void status(StreamStatus status) {
    ByteBuf buffer = upstream.create(16);
    ServerMessage.StreamStatus statusToUse = new ServerMessage.StreamStatus();
    statusToUse.code = status == StreamStatus.Connected ? 1 : 0;
    ServerCodec.write(buffer, statusToUse);
    upstream.next(buffer);
    if (status == StreamStatus.Disconnected) {
      upstream.completed();
    }
  }

  @Override
  public void next(String data) {
    ByteBuf buffer = upstream.create(16 + data.length());
    ServerMessage.StreamData dataToUse = new ServerMessage.StreamData();
    dataToUse.delta = data;
    ServerCodec.write(buffer, dataToUse);
    upstream.next(buffer);
  }

  @Override
  public void failure(ErrorCodeException exception) {
    upstream.error(exception.code);
  }

  @Override
  public void handle(ClientMessage.StreamConnect payload) {
    nexus.service.connect(new NtClient(payload.agent, payload.authority), new Key(payload.space, payload.key), payload.viewerState, this);
  }

  @Override
  public void handle(ClientMessage.MeteringBegin payload) {
  }

  @Override
  public void handle(ClientMessage.MeteringDeleteBatch payload) {
  }

  @Override
  public void handle(ClientMessage.ScanDeployment payload) {
    nexus.scanForDeployments.accept(payload.space);
    ByteBuf buf = upstream.create(4);
    ServerCodec.write(buf, new ServerMessage.ScanDeploymentResponse());
    upstream.next(buf);
    upstream.completed();
  }

  @Override
  public void handle(ClientMessage.ReflectRequest payload) {
    nexus.service.reflect(new Key(payload.space, payload.key), nexus.metrics.server_reflect.wrap(new Callback<>() {
      @Override
      public void success(String value) {
        ServerMessage.ReflectResponse response = new ServerMessage.ReflectResponse();
        response.schema = value;
        ByteBuf buf = upstream.create(8 + value.length());
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    }));
  }

  private static final ServerMessage.CreateResponse SHARED_CREATE_RESPONSE_EMPTY = new ServerMessage.CreateResponse();

  @Override
  public void handle(ClientMessage.CreateRequest payload) {
    System.err.println("server-got-create");
    nexus.service.create(new NtClient(payload.agent, payload.authority), new Key(payload.space, payload.key), payload.arg, payload.entropy, nexus.metrics.server_create.wrap(new Callback<Void>() {
      @Override
      public void success(Void value) {
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, SHARED_CREATE_RESPONSE_EMPTY);
        upstream.next(buf);
        upstream.completed();
        System.err.println("sent-response");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    }));
  }

  @Override
  public void handle(ClientMessage.PingRequest payload) {
    ByteBuf buf = upstream.create(8);
    ServerCodec.write(buf, new ServerMessage.PingResponse());
    upstream.next(buf);
    upstream.completed();
  }
}

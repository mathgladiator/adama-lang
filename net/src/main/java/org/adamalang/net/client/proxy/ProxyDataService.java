/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.proxy;

import io.netty.buffer.ByteBuf;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.runtime.data.*;

import java.util.function.Consumer;

public class ProxyDataService implements DataService {
  private final ClientMetrics metrics;
  private final ItemQueue<ChannelClient> client;

  public ProxyDataService(ClientMetrics metrics, ItemQueue<ChannelClient> client) {
    this.metrics = metrics;
    this.client = client;
  }

  private ByteStream wrapLocalDataChange(Callback<LocalDocumentChange> callback) {
    return new ServerCodec.StreamProxyLocalDataChange() {
      @Override
      public void handle(ServerMessage.ProxyLocalDataChange payload) {
        callback.success(payload.toLocalDocumentChange());
      }

      @Override
      public void completed() {

      }

      @Override
      public void error(int errorCode) {
        callback.failure(new ErrorCodeException(errorCode));
      }
    };
  }

  private ByteStream wrapInt(Callback<Integer> callback) {
    return new ServerCodec.StreamProxyIntResponse() {

      @Override
      public void handle(ServerMessage.ProxyIntResponse payload) {
        callback.success(payload.value);
      }

      @Override
      public void completed() {
      }

      @Override
      public void error(int errorCode) {
        callback.failure(new ErrorCodeException(errorCode));
      }
    };
  }

  private ByteStream wrapVoid(Callback<Void> callback) {
    return new ServerCodec.StreamProxyVoidResponse() {
      @Override
      public void handle(ServerMessage.ProxyVoidResponse payload) {
        callback.success(null);
      }

      @Override
      public void completed() {

      }

      @Override
      public void error(int errorCode) {
        callback.failure(new ErrorCodeException(errorCode));
      }
    };
  }

  private <T> void execute(Consumer<ByteStream> execute, ByteStream responder) {
    client.add(new ItemAction<ChannelClient>(ErrorCodes.PROXY_TIMEOUT, ErrorCodes.PROXY_REJECTED, metrics.client_proxy.start()) {
      @Override
      protected void executeNow(ChannelClient item) {
        item.open(responder, new Callback<ByteStream>() {
          @Override
          public void success(ByteStream value) {
            execute.accept(value);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex.code);
          }
        });
      }

      @Override
      protected void failure(int code) {
        responder.error(code);
      }
    });
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length() + 8);
      ClientMessage.ProxyGet get = new ClientMessage.ProxyGet();
      get.space = key.space;
      get.key = key.key;
      ClientCodec.write(buf, get);
      channel.next(buf);
    }, wrapLocalDataChange(callback));
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length() + patch.redo.length() + patch.undo.length() + patch.request.length() + 64);
      ClientMessage.ProxyInitialize init = new ClientMessage.ProxyInitialize();
      init.space = key.space;
      init.key = key.key;
      init.initial = new ClientMessage.RemoteDocumentUpdateItem();
      init.initial.copyFrom(patch);
      ClientCodec.write(buf, init);
      channel.next(buf);
    }, wrapVoid(callback));
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length());
      ClientMessage.ProxyPatch patch = new ClientMessage.ProxyPatch();
      patch.space = key.space;
      patch.key = key.key;
      patch.patches = new ClientMessage.RemoteDocumentUpdateItem[patches.length];
      for (int k = 0; k < patches.length; k++) {
        patch.patches[k] = new ClientMessage.RemoteDocumentUpdateItem();
        patch.patches[k].copyFrom(patches[k]);
      }
      ClientCodec.write(buf, patch);
      channel.next(buf);
    }, wrapVoid(callback));
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length() + 8);
      ClientMessage.ProxyCompute compute = new ClientMessage.ProxyCompute();
      compute.space = key.space;
      compute.key = key.key;
      if (method != null) {
        compute.method = method.type;
      }
      compute.seq = seq;
      ClientCodec.write(buf, compute);
      channel.next(buf);
    }, wrapLocalDataChange(callback));
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length());
      ClientMessage.ProxyDelete delete = new ClientMessage.ProxyDelete();
      delete.space = key.space;
      delete.key = key.key;
      ClientCodec.write(buf, delete);
      channel.next(buf);
    }, wrapVoid(callback));
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length() + snapshot.json.length() + 64);
      ClientMessage.ProxySnapshot snap = new ClientMessage.ProxySnapshot();
      snap.space = key.space;
      snap.key = key.key;
      snap.seq = snapshot.seq;
      snap.document = snapshot.json;
      snap.history = snapshot.history;
      snap.assetBytes = snapshot.assetBytes;
      ClientCodec.write(buf, snap);
      channel.next(buf);
    }, wrapInt(callback));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    execute((channel) -> {
      ByteBuf buf = channel.create(key.space.length() + key.key.length() + 16);
      ClientMessage.ProxyClose close = new ClientMessage.ProxyClose();
      close.space = key.space;
      close.key = key.key;
      ClientCodec.write(buf, close);
      channel.next(buf);
    }, wrapVoid(callback));
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.metrics.StreamMonitor;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.web.*;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Handler implements ByteStream, ClientCodec.HandlerServer, Streamback {
  private static final ServerMessage.CreateResponse SHARED_CREATE_RESPONSE_EMPTY = new ServerMessage.CreateResponse();
  private static final ServerMessage.DeleteResponse SHARED_DELETE_RESPONSE_EMPTY = new ServerMessage.DeleteResponse();
  private final ServerNexus nexus;
  private final ByteStream upstream;
  private final AtomicBoolean alive;
  private CoreStream stream;
  private ScheduledFuture<?> futureHeat;
  private StreamMonitor.StreamMonitorInstance monitorStreamback;
  private Runnable cancelWatch;

  public Handler(ServerNexus nexus, ByteStream upstream) {
    this.nexus = nexus;
    this.upstream = upstream;
    this.futureHeat = null;
    this.alive = new AtomicBoolean(true);
    nexus.metrics.server_handlers_active.up();
    this.monitorStreamback = null;
    this.cancelWatch = null;
  }

  @Override
  public void request(int bytes) {
    // proxy to the appropriate thing; if stream, then send to the core stream
    if (stream != null) {
      // TODO: proxy flow control to the stream
    }
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
    nexus.metrics.server_handlers_active.down();
    alive.set(false);
    if (stream != null) {
      stream.close();
      stream = null;
    }
    if (cancelWatch != null) {
      cancelWatch.run();
      cancelWatch = null;
    }
    if (upstream != null) {
      upstream.completed();
    }
    if (futureHeat != null) {
      futureHeat.cancel(false);
      futureHeat = null;
    }
  }

  @Override
  public void error(int errorCode) {
    nexus.metrics.server_channel_error.run();
    completed();
  }

  public Callback<Integer> respondViaInteger() {
    return new Callback<Integer>() {
      @Override
      public void success(Integer value) {
        ServerMessage.ProxyIntResponse response = new ServerMessage.ProxyIntResponse();
        response.value = value;
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    };
  }

  public Callback<Void> respondViaVoid() {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        ServerMessage.ProxyVoidResponse response = new ServerMessage.ProxyVoidResponse();
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    };
  }

  public Callback<LocalDocumentChange> respondViaLocalDataChange() {
    return new Callback<LocalDocumentChange>() {
      @Override
      public void success(LocalDocumentChange value) {
        ServerMessage.ProxyLocalDataChange response = ServerMessage.ProxyLocalDataChange.copyFrom(value);
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();

      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    };
  }

  @Override
  public void handle(ClientMessage.ProxySnapshot payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.snapshot(key, new DocumentSnapshot(payload.seq, payload.document, payload.history, payload.assetBytes), respondViaInteger());
  }

  @Override
  public void handle(ClientMessage.ProxyDelete payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.delete(key, respondViaVoid());
  }

  @Override
  public void handle(ClientMessage.ProxyCompute payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.compute(key, ComputeMethod.fromType(payload.method), payload.seq, respondViaLocalDataChange());
  }

  @Override
  public void handle(ClientMessage.ExecuteQuery payload) {
    TreeMap<String, String> query = new TreeMap<>();
    for (ClientMessage.Header header : payload.headers) {
      query.put(header.key, header.value);
    }
    nexus.service.query(query, new Callback<>() {
      @Override
      public void success(String value) {
        ServerMessage.QueryResult response = new ServerMessage.QueryResult();
        response.result = value;
        ByteBuf buf = upstream.create(response.result.length() + 60);
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  private void commonWebHandle(WebResponse value) {
    ServerMessage.WebResponseNet response = new ServerMessage.WebResponseNet();
    response.body = value.body;
    response.contentType = value.contentType;
    if (value.asset != null) {
      response.assetId = value.asset.id;
      response.assetName = value.asset.name;
      response.assetMD5 = value.asset.md5;
      response.assetSHA384 = value.asset.sha384;
      response.assetSize = value.asset.size;
    }
    response.cors = value.cors;
    response.cache_ttl_seconds = value.cache_ttl_seconds;
    response.asset_transform = value.asset_transform;
    ByteBuf buf = upstream.create(1024);
    ServerCodec.write(buf, response);
    upstream.next(buf);
    upstream.completed();
  }

  @Override
  public void handle(ClientMessage.WebPut payload) {
    Key key = new Key(payload.space, payload.key);
    TreeMap<String, String> headers = new TreeMap<>();
    for (ClientMessage.Header header : payload.headers) {
      headers.put(header.key, header.value);
    }
    WebPut put = new WebPut(new WebContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip), payload.uri, headers, new NtDynamic(payload.parametersJson), payload.bodyJson);
    nexus.service.webPut(key, put, new Callback<>() {
      @Override
      public void success(WebResponse value) {
        commonWebHandle(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  @Override
  public void handle(ClientMessage.WebDelete payload) {
    Key key = new Key(payload.space, payload.key);
    TreeMap<String, String> headers = new TreeMap<>();
    for (ClientMessage.Header header : payload.headers) {
      headers.put(header.key, header.value);
    }
    WebDelete delete = new WebDelete(new WebContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip), payload.uri, headers, new NtDynamic(payload.parametersJson));
    nexus.service.webDelete(key, delete, new Callback<>() {
      @Override
      public void success(WebResponse value) {
        commonWebHandle(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  @Override
  public void handle(ClientMessage.WebOptions payload) {
    Key key = new Key(payload.space, payload.key);
    TreeMap<String, String> headers = new TreeMap<>();
    for (ClientMessage.Header header : payload.headers) {
      headers.put(header.key, header.value);
    }
    WebGet get = new WebGet(new WebContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip), payload.uri, headers, new NtDynamic(payload.parametersJson));
    nexus.service.webOptions(key, get, new Callback<>() {
      @Override
      public void success(WebResponse value) {
        commonWebHandle(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  @Override
  public void handle(ClientMessage.WebGet payload) {
    Key key = new Key(payload.space, payload.key);
    TreeMap<String, String> headers = new TreeMap<>();
    for (ClientMessage.Header header : payload.headers) {
      headers.put(header.key, header.value);
    }
    WebGet get = new WebGet(new WebContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip), payload.uri, headers, new NtDynamic(payload.parametersJson));
    nexus.service.webGet(key, get, new Callback<>() {
      @Override
      public void success(WebResponse value) {
        commonWebHandle(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  @Override
  public void handle(ClientMessage.ProxyClose payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.close(key, respondViaVoid());
  }

  @Override
  public void handle(ClientMessage.ProxyPatch payload) {
    Key key = new Key(payload.space, payload.key);
    RemoteDocumentUpdate[] patches = new RemoteDocumentUpdate[payload.patches.length];
    for (int k = 0; k < patches.length; k++) {
      patches[k] = payload.patches[k].toRemoteDocumentUpdate();
    }
    nexus.service.dataService.patch(key, patches, respondViaVoid());
  }

  @Override
  public void handle(ClientMessage.ProxyInitialize payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.initialize(key, payload.initial.toRemoteDocumentUpdate(), respondViaVoid());
  }

  @Override
  public void handle(ClientMessage.ProxyGet payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.dataService.get(key, respondViaLocalDataChange());
  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
    nexus.meteringPubSub.subscribe((bills) -> {
      if (alive.get()) {
        ArrayList<String> spaces = new ArrayList<>();
        for (MeterReading meterReading : bills) {
          spaces.add(meterReading.space);
        }
        ServerMessage.InventoryHeartbeat inventoryHeartbeat = new ServerMessage.InventoryHeartbeat();
        inventoryHeartbeat.spaces = spaces.toArray(new String[spaces.size()]);
        ByteBuf buf = upstream.create(24);
        ServerCodec.write(buf, inventoryHeartbeat);
        upstream.next(buf);
      }
      return alive.get();
    });
  }

  @Override
  public void handle(ClientMessage.RequestHeat payload) {
    futureHeat = nexus.base.workerGroup.scheduleAtFixedRate(() -> {
      ServerMessage.HeatPayload heat = new ServerMessage.HeatPayload();
      heat.cpu = MachineHeat.cpu();
      heat.mem = MachineHeat.memory();
      ByteBuf buf = upstream.create(24);
      ServerCodec.write(buf, heat);
      upstream.next(buf);
    }, 25, 250, TimeUnit.MILLISECONDS);
  }

  @Override
  public void handle(ClientMessage.StreamAttach payload) {
    if (stream != null) {
      stream.attach(payload.id, payload.filename, payload.contentType, payload.size, payload.md5, payload.sha384, nexus.metrics.server_stream_attach.wrap(new Callback<Integer>() {
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
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamAskAttachmentRequest payload) {
    if (stream != null) {
      stream.canAttach(nexus.metrics.server_stream_ask.wrap(new Callback<Boolean>() {
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
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamDisconnect payload) {
    if (stream != null) {
      nexus.metrics.server_stream_disconnect.run();
      stream.close();
      stream = null;
    }
  }

  @Override
  public void handle(ClientMessage.StreamUpdate payload) {
    if (stream != null) {
      nexus.metrics.server_stream_update.run();
      stream.update(payload.viewerState);
    }
  }

  @Override
  public void handle(ClientMessage.StreamSend payload) {
    if (stream != null) {
      stream.send(payload.channel, payload.marker, payload.message, nexus.metrics.server_stream_send.wrap(new Callback<>() {
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
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamConnect payload) {
    monitorStreamback = nexus.metrics.server_stream.start();
    CoreRequestContext context = new CoreRequestContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip, payload.key);
    try {
      AssetIdEncoder assetIdEncoder = payload.assetKey != null ? new AssetIdEncoder(payload.assetKey) : null;
      nexus.service.connect(context, new Key(payload.space, payload.key), payload.viewerState, assetIdEncoder, this);
    } catch (ErrorCodeException ex) {
      failure(ex);
    }
  }

  @Override
  public void handle(ClientMessage.ReplicaConnect payload) {
    DataObserver observer = new DataObserver() {
      @Override
      public void start(String snapshot) {
        ByteBuf toWrite = upstream.create(16 + snapshot.length());
        ServerMessage.ReplicaData data = new ServerMessage.ReplicaData();
        data.reset = true;
        data.change = snapshot;
        ServerCodec.write(toWrite, data);
        upstream.next(toWrite);
      }

      @Override
      public void change(String delta) {
        ByteBuf toWrite = upstream.create(16 + delta.length());
        ServerMessage.ReplicaData data = new ServerMessage.ReplicaData();
        data.reset = false;
        data.change = delta;
        ServerCodec.write(toWrite, data);
        upstream.next(toWrite);
      }

      @Override
      public void failure(ErrorCodeException exception) {
        upstream.error(exception.code);
      }
    };
    Key key = new Key(payload.space, payload.key);
    nexus.service.watch(key, observer);
    cancelWatch = () -> {
      nexus.service.unwatch(key, observer);
    };
  }

  @Override
  public void handle(ClientMessage.ReplicaDisconnect payload) {
    if (cancelWatch != null) {
      cancelWatch.run();
      cancelWatch = null;
    }
  }

  @Override
  public void handle(ClientMessage.MeteringDeleteBatch payload) {
    try {
      nexus.metrics.server_metering_delete_batch.run();
      nexus.meteringBatchMaker.deleteBatch(payload.id);
      ByteBuf toWrite = upstream.create(4);
      ServerCodec.write(toWrite, new ServerMessage.MeteringBatchRemoved());
      upstream.next(toWrite);
    } catch (Exception ex) {
      upstream.error(-1);
    }
  }

  @Override
  public void handle(ClientMessage.MeteringBegin payload) {
    try {
      String id = nexus.meteringBatchMaker.getNextAvailableBatchId();
      nexus.metrics.server_metering_begin.run();
      if (id != null) {
        String batch = nexus.meteringBatchMaker.getBatch(id);
        ServerMessage.MeteringBatchFound found = new ServerMessage.MeteringBatchFound();
        found.id = id;
        found.batch = batch;
        ByteBuf toWrite = upstream.create(id.length() + batch.length() + 8);
        ServerCodec.write(toWrite, found);
        upstream.next(toWrite);
      } else {
        upstream.completed();
      }
    } catch (Exception ex) {
      upstream.error(-1);
    }
  }

  @Override
  public void handle(ClientMessage.ScanDeployment payload) {
    try {
      nexus.metrics.server_scan_deployment.run();
      nexus.capacityRequestor.requestCodeDeployment(payload.space, new Callback<Void>() {
        @Override
        public void success(Void value) {
          ByteBuf buf = upstream.create(4);
          ServerCodec.write(buf, new ServerMessage.ScanDeploymentResponse());
          upstream.next(buf);
          upstream.completed();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          upstream.error(ex.code);
        }
      });
    } catch (Exception ex) {
      upstream.error(ErrorCodes.NET_HANDLER_SCAN_EXCEPTION);
    }
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

  @Override
  public void handle(ClientMessage.CreateRequest payload) {
    CoreRequestContext context = new CoreRequestContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip, payload.key);
    nexus.service.create(context, new Key(payload.space, payload.key), payload.arg, payload.entropy, nexus.metrics.server_create.wrap(new Callback<Void>() {
      @Override
      public void success(Void value) {
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, SHARED_CREATE_RESPONSE_EMPTY);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    }));
  }

  @Override
  public void handle(ClientMessage.DirectSend payload) {
    CoreRequestContext context = new CoreRequestContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip, payload.key);
    nexus.service.directSend(context, new Key(payload.space, payload.key), payload.marker, payload.channel, payload.message, new Callback<>() {
      @Override
      public void success(Integer seq) {
        ByteBuf buf = upstream.create(24);
        ServerMessage.DirectSendResponse response = new ServerMessage.DirectSendResponse();
        response.seq = seq;
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    });
  }

  @Override
  public void handle(ClientMessage.DeleteRequest payload) {
    CoreRequestContext context = new CoreRequestContext(new NtPrincipal(payload.agent, payload.authority), payload.origin, payload.ip, payload.key);
    nexus.service.delete(context, new Key(payload.space, payload.key),  nexus.metrics.server_delete.wrap(new Callback<Void>() {
      @Override
      public void success(Void value) {
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, SHARED_DELETE_RESPONSE_EMPTY);
        upstream.next(buf);
        upstream.completed();
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
      monitorStreamback.finish();
      upstream.completed();
    }
  }

  @Override
  public void next(String data) {
    monitorStreamback.progress();
    ByteBuf buffer = upstream.create(16 + data.length());
    ServerMessage.StreamData dataToUse = new ServerMessage.StreamData();
    dataToUse.delta = data;
    ServerCodec.write(buffer, dataToUse);
    upstream.next(buffer);
  }

  @Override
  public void failure(ErrorCodeException exception) {
    monitorStreamback.failure(exception.code);
    upstream.error(exception.code);
    completed();
  }
}

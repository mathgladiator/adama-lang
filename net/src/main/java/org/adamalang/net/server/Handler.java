/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.runtime.sys.TriggerDeployment;
import org.adamalang.runtime.sys.capacity.CurrentLoad;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.web.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Handler implements ByteStream, ClientCodec.HandlerServer, Streamback {
  private static final Logger LOG = LoggerFactory.getLogger(Handler.class);
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

  @Override
  public void handle(ClientMessage.DrainRequest payload) {
    nexus.drain(new Callback<>() {
      @Override
      public void success(Void value) {
        ByteBuf buf = upstream.create(32);
        ServerMessage.DrainResponse response = new ServerMessage.DrainResponse();
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
  public void handle(ClientMessage.LoadRequest payload) {
    nexus.service.getLoad(new Callback<CurrentLoad>() {
      @Override
      public void success(CurrentLoad load) {
        ByteBuf buf = upstream.create(32);
        ServerMessage.LoadResponse response = new ServerMessage.LoadResponse();
        response.documents = load.documents;
        response.connections = load.connections;
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
  public void handle(ClientMessage.RateLimitTestRequest payload) {
    // a very dumb implementation, but testable!
    // TODO: hook up to some kind of store with configuration (and expiry policy)
    ByteBuf buf = upstream.create(32);
    ServerMessage.RateLimitResult response = new ServerMessage.RateLimitResult();
    response.tokens = 5;
    response.milliseconds = 250;
    ServerCodec.write(buf, response);
    upstream.next(buf);
    upstream.completed();
  }

  @Override
  public void handle(ClientMessage.FindRequest payload) {
    nexus.finder.find(new Key(payload.space, payload.key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation value) {
        ServerMessage.FindResponse response = new ServerMessage.FindResponse();
        response.archive = value.archiveKey;
        response.id = value.id;
        response.deleted = value.deleted;
        response.machine = value.machine;
        response.region = value.region;
        if (value.location == LocationType.Archive) {
          response.machine = nexus.finder.machine;
          response.region = nexus.finder.region;
          response.location = LocationType.Machine.type;
        } else {
          response.location = value.location.type;
        }
        ByteBuf buf = upstream.create(256);
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (ex.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED) {
          ServerMessage.FindResponse response = new ServerMessage.FindResponse();
          response.archive = null;
          response.id = 0;
          response.deleted = false;
          response.machine = nexus.finder.machine;
          response.region = nexus.finder.region;
          response.location = LocationType.Machine.type;
          ByteBuf buf = upstream.create(256);
          ServerCodec.write(buf, response);
          upstream.next(buf);
          upstream.completed();
        } else {
          upstream.error(ex.code);
        }
      }
    });
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


  @Override
  public void handle(ClientMessage.Authorize payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.authorize(payload.origin, payload.ip, key, payload.username, payload.password, payload.new_password, new Callback<String>() {
      @Override
      public void success(String agent) {
        ServerMessage.AuthResponse response = new ServerMessage.AuthResponse();
        response.agent = agent;
        ByteBuf buf = upstream.create(response.agent.length() + 32);
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
  public void handle(ClientMessage.AuthorizationRequest payload) {
    Key key = new Key(payload.space, payload.key);
    nexus.service.authorization(payload.origin, payload.ip, key, payload.payload, new Callback<AuthResponse>() {
      @Override
      public void success(AuthResponse fromCore) {
        ServerMessage.AuthorizationResponse response = new ServerMessage.AuthorizationResponse();
        response.hash = fromCore.hash;
        response.agent = fromCore.agent;
        response.success = fromCore.success;
        response.channel = fromCore.channel;
        ByteBuf buf = upstream.create(response.agent.length() + 32);
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
    response.status = value.status;
    response.cors = value.cors;
    response.cacheTimeToLiveSeconds = value.cache_ttl_seconds;
    response.assetTransform = value.asset_transform;
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
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
    nexus.meteringPubSub.subscribe((bills) -> {
      if (alive.get()) {
        ArrayList<String> spaces = new ArrayList<>();
        if (!nexus.isDrained()) {
          // when we are draining, we signal to stop routing to this host via inventory
          for (MeterReading meterReading : bills) {
            spaces.add(meterReading.space);
          }
        }
        ServerMessage.InventoryHeartbeat inventoryHeartbeat = new ServerMessage.InventoryHeartbeat();
        inventoryHeartbeat.spaces = spaces.toArray(new String[spaces.size()]);
        ByteBuf buf = upstream.create(24 + spaces.size() * 32);
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
  public void handle(ClientMessage.StreamPassword payload) {
    if (stream != null) {
      stream.password(payload.password, nexus.metrics.server_stream_password.wrap(new Callback<>() {
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
    nexus.service.connect(context, new Key(payload.space, payload.key), payload.viewerState, this);
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
  public void handle(ClientMessage.ScanDeployment payload) {
    try {
      nexus.metrics.server_scan_deployment.run();
      nexus.deployer.deploy(payload.space, new TriggerDeployment(nexus.service, new Callback<Void>() {
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
      }));
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
  public void handle(ClientMessage.ProbeCommandRequest payload) {
    ByteBuf buf = upstream.create(0);
    ServerMessage.ProbeCommandResponse response = new ServerMessage.ProbeCommandResponse();
    if ("query".equals(payload.command)) {
      if (payload.args != null && payload.args.length == 2) {
        TreeMap<String, String> query = new TreeMap<>();
        query.put("space", payload.args[0]);
        query.put("key", payload.args[1]);
        nexus.service.query(query, new Callback<String>() {
          @Override
          public void success(String json) {
            response.json = json;
            ServerCodec.write(buf, response);
            upstream.next(buf);
            upstream.completed();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            response.json = "{\"error\":" + ex.code + "}";
            ServerCodec.write(buf, response);
            upstream.next(buf);
            upstream.completed();
          }
        });
        return;
      } else {
        response.json = "{}";
        response.errors = new String[] { "query requires two args" };
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
        return;
      }
    }
    response.json = "null";
    ServerCodec.write(buf, response);
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

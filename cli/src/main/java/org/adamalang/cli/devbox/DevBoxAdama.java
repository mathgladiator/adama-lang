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
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class DevBoxAdama extends DevBoxRouter implements ServiceConnection {
  private static final Logger PERF_LOG = LoggerFactory.getLogger("perf");
  private final SimpleExecutor executor;
  private final ConnectionContext context;
  private final DynamicControl control;
  private final TerminalIO io;
  private final ConcurrentHashMap<Long, LocalStream> streams;
  private final DevBoxAdamaMicroVerse verse;
  private final Runnable death;

  private class LocalStream {
    public final Key key;
    public final CoreStream ref;

    public LocalStream(Key key, CoreStream ref) {
      this.key = key;
      this.ref = ref;
    }
  }

  @Override
  public void handle_ConfigureMakeOrGetAssetKey(long requestId, AssetKeyResponder responder) {
    responder.complete(SecureAssetUtil.makeAssetKeyHeader());
  }

  public DevBoxAdama(SimpleExecutor executor, ConnectionContext context, DynamicControl control, TerminalIO io, DevBoxAdamaMicroVerse verse, Runnable death) {
    this.executor = executor;
    this.context = context;
    this.control = control;
    this.io = io;
    this.verse = verse;
    this.streams = new ConcurrentHashMap<>();
    this.death = death;
  }

  public static NtPrincipal principalOf(String identity) {
    if (identity.startsWith("document/")) {
      String[] parts = identity.split(Pattern.quote("/"));
      return new NtPrincipal(parts[3], "doc/" + parts[1] + "/" + parts[2]);
    }
    if (identity.startsWith("anonymous:")) {
      return new NtPrincipal(identity.substring(10), "anonymous");
    }
    // TODO: parse identity and then resolve against a table
    return NtPrincipal.NO_ONE;
  }

  @Override
  public void handle_SpaceReflect(long requestId, String identity, String space, String key, ReflectionResponder responder) {
    verse.service.reflect(new Key(space, key), new Callback<>() {
      @Override
      public void success(String value) {
        responder.complete(Json.parseJsonObject(value));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle_DomainGetVapidPublicKey(long requestId, String identity, String domain, DomainVapidResponder responder) {
    responder.complete(verse.vapidPublicKey);
  }

  @Override
  public void handle_PushRegister(long requestId, String identity, String domain, ObjectNode subscription, ObjectNode deviceInfo, SimpleResponder responder) {
    verse.devPush.register(principalOf(identity), domain, subscription, deviceInfo);
    responder.complete();
  }

  @Override
  public void handle_DomainReflect(long requestId, String identity, String domain, ReflectionResponder responder) {
    verse.service.reflect(verse.domainKeyToUse, new Callback<>() {
      @Override
      public void success(String value) {
        responder.complete(Json.parseJsonObject(value));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  // public void authorize(String origin, String ip, Key key, String username, String password, Callback<String> callback) {
  private void commonAuthorize(Key key, String username, String password, String new_password, InitiationResponder responder) {
    verse.service.authorize(context.origin, context.remoteIp, key, username, password, new_password, new Callback<String>() {
      @Override
      public void success(String value) {
        responder.complete("document/" + key.space + "/" + key.key + "/" + value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle_DocumentAuthorize(long requestId, String space, String key, String username, String password, InitiationResponder responder) {
    commonAuthorize(new Key(space, key), username, password, null, responder);
  }

  @Override
  public void handle_DocumentAuthorizeDomain(long requestId, String domain, String username, String password, InitiationResponder responder) {
    if (verse.domainKeyToUse != null) {
      commonAuthorize(verse.domainKeyToUse, username, password, null, responder);
    } else {
      responder.error(new ErrorCodeException(1));
    }
  }

  @Override
  public void handle_DocumentAuthorizeWithReset(long requestId, String space, String key, String username, String password, String new_password, InitiationResponder responder) {
    commonAuthorize(new Key(space, key), username, password, new_password, responder);
  }

  @Override
  public void handle_DocumentAuthorizeDomainWithReset(long requestId, String domain, String username, String password, String new_password, InitiationResponder responder) {
    if (verse.domainKeyToUse != null) {
      commonAuthorize(verse.domainKeyToUse, username, password, new_password, responder);
    } else {
      responder.error(new ErrorCodeException(1));
    }
  }

  private void internalConnect(long requestId, String identity, Key key, ObjectNode viewerState, DataResponder responder) {
    long started = System.currentTimeMillis();
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), this.context.origin, this.context.remoteIp, key.key);
    verse.service.connect(context, key, viewerState != null ? viewerState.toString() : "{}", null, new Streamback() {
      private CoreStream got = null;
      @Override
      public void onSetupComplete(CoreStream stream) {
        this.got = stream;
        streams.put(requestId, new LocalStream(key, stream));
        io.info("adama|connected to " + key.space + "/" +key.key);
        ObjectNode entry = Json.newJsonObject();
        entry.put("type", "devbox");
        entry.put("task", "connect");
        long delta = (System.currentTimeMillis() - started);
        entry.put("time", delta);
        if (delta > 10000) {
          io.error("adama|connection; It took over " + Math.round((delta / 100.0)) / 10.0 + " seconds to establish a connection");
        }
        PERF_LOG.error(entry.toString());
      }

      @Override
      public void status(StreamStatus status) {
      }

      @Override
      public void next(String data) {
        io.info("adama|connection[" + key.space + "/" + key.key + "]:" + data);
        ObjectNode delta = Json.parseJsonObject(data);
        responder.next(delta);
        JsonNode force = delta.get("force-disconnect");
        if (force != null && force.isBoolean() && force.booleanValue()) {
          responder.error(new ErrorCodeException(ErrorCodes.AUTH_DISCONNECTED));
          if (got != null) {
            io.info("adama|forced disconnect");
            got.close();
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle_ConnectionCreate(long requestId, String identity, String space, String key, ObjectNode viewerState, DataResponder responder) {
    internalConnect(requestId, identity, new Key(space, key), viewerState, responder);
  }

  @Override
  public void handle_ConnectionCreateViaDomain(long requestId, String identity, String domain, ObjectNode viewerState, DataResponder responder) {
    if (verse != null) {
      internalConnect(requestId, identity, verse.domainKeyToUse, viewerState, responder);
    } else {
      responder.error(new ErrorCodeException(10023));
    }
  }

  private static Callback<Integer> wrap(String task, SeqResponder responder) {
    long started = System.currentTimeMillis();
    return new Callback<>() {
      @Override
      public void success(Integer seq) {
        ObjectNode entry = Json.newJsonObject();
        entry.put("type", "devbox");
        entry.put("task", task);
        entry.put("time", (System.currentTimeMillis() - started));
        PERF_LOG.error(entry.toString());
        responder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }

  @Override
  public void handle_ConnectionSend(long requestId, Long connection, String channel, JsonNode message, SeqResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      stream.ref.send(channel, null, message.toString(), wrap("send", responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionPassword(long requestId, Long connection, String username, String password, String new_password, SimpleResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      verse.service.authorize(context.origin, context.remoteIp, stream.key, username, password, new_password, new Callback<String>() {
        @Override
        public void success(String value) {
          responder.complete();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionSendOnce(long requestId, Long connection, String channel, String dedupe, JsonNode message, SeqResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      stream.ref.send(channel, dedupe, message.toString(), wrap("send-once", responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionCanAttach(long requestId, Long connection, YesResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      stream.ref.canAttach(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          responder.complete(value);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionAttach(long requestId, Long connection, String assetId, String filename, String contentType, Long size, String digestMd5, String digestSha384, SeqResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      stream.ref.attach(assetId, filename, contentType, size, digestMd5, digestSha384, wrap("attach", responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionUpdate(long requestId, Long connection, ObjectNode viewerState, SimpleResponder responder) {
    long started = System.currentTimeMillis();
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      if (control.slowViewerStateUpdates.get()) {
        executor.schedule(new NamedRunnable("slow update") {
          @Override
          public void execute() throws Exception {
            stream.ref.update(viewerState.toString());
            responder.complete();
            ObjectNode entry = Json.newJsonObject();
            entry.put("type", "devbox");
            entry.put("task", "update");
            entry.put("time", (System.currentTimeMillis() - started));
            PERF_LOG.error(entry.toString());
          }
        }, 1000);
      } else {
        stream.ref.update(viewerState.toString());
        responder.complete();
      }
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionEnd(long requestId, Long connection, SimpleResponder responder) {
    LocalStream stream = streams.remove(connection);
    if (stream != null) {
      stream.ref.close();
    }
    responder.complete();
  }

  @Override
  public void handle_DocumentsHashPassword(long requestId, String password, HashedPasswordResponder responder) {
    responder.complete(SCryptUtil.scrypt(password, 16384, 8, 1));
  }

  @Override
  public void execute(JsonRequest request, JsonResponder responder) {
    if (verse != null) {
      route(request, responder);
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public boolean keepalive() {
    return true;
  }

  public void diagnostics(ObjectNode dump) {
    dump.put("connections", streams.size());
  }

  @Override
  public void kill() {
    for(LocalStream stream : streams.values()) {
      stream.ref.close();
    }
    streams.clear();
    death.run();
  }
}

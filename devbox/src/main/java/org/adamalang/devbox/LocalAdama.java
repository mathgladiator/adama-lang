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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class LocalAdama extends DevBoxRouter implements ServiceConnection {
  private static final Logger PERF_LOG = LoggerFactory.getLogger("perf");
  private final static ConcurrentHashMap<String, String> LOCALHOST_COOKIES = new ConcurrentHashMap<>();
  private final SimpleExecutor executor;
  private final ConnectionContext context;
  private final DynamicControl control;
  private final TerminalIO io;
  private final ConcurrentHashMap<Long, LocalStream> streams;
  private final AdamaMicroVerse verse;
  private final Runnable death;
  private final RxPubSub rxPubSub;

  public LocalAdama(SimpleExecutor executor, ConnectionContext context, DynamicControl control, TerminalIO io, AdamaMicroVerse verse, Runnable death, RxPubSub rxPubSub) {
    this.executor = executor;
    this.context = context;
    this.control = control;
    this.io = io;
    this.verse = verse;
    this.streams = new ConcurrentHashMap<>();
    this.death = death;
    this.rxPubSub = rxPubSub;
    if (context.identities != null) {
      for (Map.Entry<String, String> entry : context.identities.entrySet()) {
        LOCALHOST_COOKIES.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public static NtPrincipal principalOf(String identity) {
    if (identity.startsWith("cookie:")) {
      String name = identity.substring(7);
      String value = LOCALHOST_COOKIES.get(name);
      if (value != null) {
        return principalOf(value);
      }
    }
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
  public void handle_Stats(long requestId, StatsResponder responder) {
    responder.next("stream-count", streams.size() + "", "int");
    for (Map.Entry<String, String> entry : LOCALHOST_COOKIES.entrySet()) {
      responder.next("identity-" + entry.getKey(), entry.getValue(), "string");
    }
    responder.finish();
  }

  @Override
  public void handle_IdentityHash(long requestId, String identity, IdentityHashResponder responder) {
    NtPrincipal p = principalOf(identity);
    String stringToHash = p.agent + ":" + p.authority + "/" + p.agent;
    MessageDigest digest = Hashing.sha384();
    digest.update(stringToHash.getBytes(StandardCharsets.UTF_8));
    responder.complete(Hashing.finishAndEncode(digest));
  }

  @Override
  public void handle_IdentityStash(long requestId, String identity, String name, SimpleResponder responder) {
    if (identity.startsWith("cookie:")) {
      responder.error(new ErrorCodeException(ErrorCodes.AUTH_COOKIE_CANT_STASH_COOKIE));
      return;
    }
    LOCALHOST_COOKIES.put(name, identity);
    responder.complete();
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

  @Override
  public void handle_DomainGetVapidPublicKey(long requestId, String identity, String domain, DomainVapidResponder responder) {
    responder.complete(verse.vapidPublicKey);
  }

  @Override
  public void handle_DocumentAuthorization(long requestId, String space, String key, JsonNode message, InitiationResponder responder) {
    commonAuthorizatiopn(new Key(space, key), message, responder);
  }

  // public void authorize(String origin, String ip, Key key, String username, String password, Callback<String> callback) {
  private void commonAuthorizatiopn(Key key, JsonNode payload, InitiationResponder responder) {
    ObjectNode message = (ObjectNode) payload;
    String pw = message.remove("password").textValue();
    verse.service.authorization(context.origin, context.remoteIp, key, message.toString(), new Callback<AuthResponse>() {
      @Override
      public void success(AuthResponse response) {
        if (response != null) {
          if (SCryptUtil.check(pw, response.hash)) {
            if (response.channel != null && response.success != null) {
              CoreRequestContext newUser = new CoreRequestContext(new NtPrincipal(response.agent, "doc/" + key.space + "/" + key.key), context.origin, context.remoteIp, key.key);
              verse.service.directSend(newUser, key, null, response.channel, response.success, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  responder.complete("document/" + key.space + "/" + key.key + "/" + response.agent);
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  responder.error(ex);
                }
              });
            } else {
              responder.complete("document/" + key.space + "/" + key.key + "/" + response.agent);
            }
            return;
          }
        }
        responder.error(new ErrorCodeException(ErrorCodes.DOCUMENT_AUTHORIIZE_FAILURE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle_DocumentAuthorizationDomain(long requestId, String domain, JsonNode message, InitiationResponder responder) {
    if (verse.domainKeyToUse != null) {
      commonAuthorizatiopn(verse.domainKeyToUse, message, responder);
    } else {
      responder.error(new ErrorCodeException(0));
    }
  }

  @Override
  public void handle_DocumentAuthorize(long requestId, String space, String key, String username, String password, InitiationResponder responder) {
    commonAuthorize(new Key(space, key), username, password, null, responder);
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

  @Override
  public void handle_ConnectionSend(long requestId, Long connection, String channel, JsonNode message, SeqResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      stream.ref.send(channel, null, message.toString(), wrap("send", responder));
    } else {
      responder.error(new ErrorCodeException(-1));
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

  private void internalConnect(long requestId, String identity, Key key, ObjectNode viewerState, DataResponder responder) {
    long started = System.currentTimeMillis();
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), this.context.origin, this.context.remoteIp, key.key);

    verse.service.connect(context, key, viewerState != null ? viewerState.toString() : "{}", new Streamback() {
      Runnable unsub = null;
      private CoreStream got = null;

      @Override
      public void onSetupComplete(CoreStream stream) {
        this.got = stream;
        streams.put(requestId, new LocalStream(key, stream));
        io.info("adama|connected to " + key.space + "/" + key.key);
        ObjectNode entry = Json.newJsonObject();
        entry.put("type", "devbox");
        entry.put("task", "connect");
        long delta = (System.currentTimeMillis() - started);
        entry.put("time", delta);
        if (delta > 10000) {
          io.error("adama|connection; It took over " + Math.round((delta / 100.0)) / 10.0 + " seconds to establish a connection");
        }
        this.unsub = rxPubSub.subscribe(responder);
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
            if (unsub != null) {
              unsub.run();
            }
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

  @Override
  public void kill() {
    for (LocalStream stream : streams.values()) {
      stream.ref.close();
    }
    streams.clear();
    death.run();
  }

  public void diagnostics(ObjectNode dump) {
    dump.put("connections", streams.size());
  }

  private class LocalStream {
    public final Key key;
    public final CoreStream ref;

    public LocalStream(Key key, CoreStream ref) {
      this.key = key;
      this.ref = ref;
    }
  }
}

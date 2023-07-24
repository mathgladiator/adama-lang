/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

import java.util.concurrent.ConcurrentHashMap;

public class DevBoxAdama extends DevBoxRouter implements ServiceConnection {
  private final SimpleExecutor executor;
  private final ConnectionContext context;
  private final DynamicControl control;
  private final TerminalIO io;
  private final ConcurrentHashMap<Long, LocalStream> streams;
  private final DevBoxAdamaMicroVerse verse;

  private class LocalStream {
    public final Key key;
    public final CoreStream ref;

    public LocalStream(Key key, CoreStream ref) {
      this.key = key;
      this.ref = ref;
    }
  }

  public DevBoxAdama(SimpleExecutor executor, ConnectionContext context, DynamicControl control, TerminalIO io, DevBoxAdamaMicroVerse verse) {
    this.executor = executor;
    this.context = context;
    this.control = control;
    this.io = io;
    this.verse = verse;
    this.streams = new ConcurrentHashMap<>();
  }

  private NtPrincipal principalOf(String identity) {
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
  public void handle_ConnectionCreate(long requestId, String identity, String space, String key, ObjectNode viewerState, DataResponder responder) {
    // public void connect(CoreRequestContext context, Key key, String viewerState, AssetIdEncoder assetIdEncoder, Streamback stream) {
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), this.context.origin, this.context.remoteIp, key);
    verse.service.connect(context, new Key(space, key), viewerState != null ? viewerState.toString() : "{}", null, new Streamback() {
      @Override
      public void onSetupComplete(CoreStream stream) {
        streams.put(requestId, new LocalStream(new Key(space, key), stream));
        io.info("connected to " + space + "/" + key);
      }

      @Override
      public void status(StreamStatus status) {
      }

      @Override
      public void next(String data) {
        io.info("[" + space + "/" + key + "]:" + data);
        responder.next(Json.parseJsonObject(data));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle_ConnectionCreateViaDomain(long requestId, String identity, String domain, ObjectNode viewerState, DataResponder responder) {
    Key key = null; // TODO: resolve from config
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), "localhost", "127.0.0.1", key.key);

    verse.service.connect(context, key, viewerState != null ? viewerState.toString() : "{}", null, new Streamback() {
      @Override
      public void onSetupComplete(CoreStream stream) {
        streams.put(requestId, new LocalStream(key, stream));
      }

      @Override
      public void status(StreamStatus status) {
      }

      @Override
      public void next(String data) {
        responder.next(Json.parseJsonObject(data));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  private static Callback<Integer> wrap(SeqResponder responder) {
    return new Callback<>() {
      @Override
      public void success(Integer seq) {
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
      stream.ref.send(channel, null, message.toString(), wrap(responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionPassword(long requestId, Long connection, String username, String password, String new_password, SeqResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      verse.service.authorize(context.origin, context.remoteIp, stream.key, username, password, new Callback<String>() {
        @Override
        public void success(String value) {
          stream.ref.password(new_password, wrap(responder));
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
      stream.ref.send(channel, dedupe, message.toString(), wrap(responder));
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
      stream.ref.attach(assetId, filename, contentType, size, digestMd5, digestSha384, wrap(responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionUpdate(long requestId, Long connection, ObjectNode viewerState, SimpleResponder responder) {
    LocalStream stream = streams.get(connection);
    if (stream != null) {
      if (control.slowViewerStateUpdates.get()) {
        executor.schedule(new NamedRunnable("slow update") {
          @Override
          public void execute() throws Exception {
            stream.ref.update(viewerState.toString());
            responder.complete();
          }
        }, 5000);
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

  @Override
  public void kill() {
    for(LocalStream stream : streams.values()) {
      stream.ref.close();
    }
  }
}

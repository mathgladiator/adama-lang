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
import org.adamalang.api.*;
import org.adamalang.cli.interactive.TerminalIO;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

import java.util.concurrent.ConcurrentHashMap;

public class DevBoxAdama extends DevBoxRouter implements ServiceConnection {
  private final TerminalIO io;
  private final CoreService service;
  private final ConcurrentHashMap<Long, CoreStream> streams;

  public DevBoxAdama(TerminalIO io, CoreService service) {
    this.io = io;
    this.service = service;
    this.streams = new ConcurrentHashMap<>();
  }

  private NtPrincipal principalOf(String identity) {
    // TODO: parse identity and then resolve against a table
    return NtPrincipal.NO_ONE;
  }

  @Override
  public void handle_SpaceReflect(long requestId, String identity, String space, String key, ReflectionResponder responder) {
    service.reflect(new Key(space, key), new Callback<>() {
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
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), "localhost", "127.0.0.1", key);

    service.connect(context, new Key(space, key), viewerState != null ? viewerState.toString() : "{}", null, new Streamback() {
      @Override
      public void onSetupComplete(CoreStream stream) {
        streams.put(requestId, stream);
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

  @Override
  public void handle_ConnectionCreateViaDomain(long requestId, String identity, String domain, ObjectNode viewerState, DataResponder responder) {
    Key key = null; // TODO: resolve from config
    CoreRequestContext context = new CoreRequestContext(principalOf(identity), "localhost", "127.0.0.1", key.key);

    service.connect(context, key, viewerState != null ? viewerState.toString() : "{}", null, new Streamback() {
      @Override
      public void onSetupComplete(CoreStream stream) {
        streams.put(requestId, stream);
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
    CoreStream stream = streams.get(connection);
    if (stream != null) {
      stream.send(channel, null, message.toString(), wrap(responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionPassword(long requestId, Long connection, String username, String password, String new_password, SeqResponder responder) {

  }

  @Override
  public void handle_ConnectionSendOnce(long requestId, Long connection, String channel, String dedupe, JsonNode message, SeqResponder responder) {
    CoreStream stream = streams.get(connection);
    if (stream != null) {
      stream.send(channel, dedupe, message.toString(), wrap(responder));
    } else {
      responder.error(new ErrorCodeException(-1));
    }
  }

  @Override
  public void handle_ConnectionCanAttach(long requestId, Long connection, YesResponder responder) {

  }

  @Override
  public void handle_ConnectionAttach(long requestId, Long connection, String assetId, String filename, String contentType, Long size, String digestMd5, String digestSha384, SeqResponder responder) {

  }

  @Override
  public void handle_ConnectionUpdate(long requestId, Long connection, ObjectNode viewerState, SimpleResponder responder) {

  }

  @Override
  public void handle_ConnectionEnd(long requestId, Long connection, SimpleResponder responder) {

  }

  @Override
  public void handle_DocumentsHashPassword(long requestId, String password, HashedPasswordResponder responder) {

  }

  @Override
  public void execute(JsonRequest request, JsonResponder responder) {
    route(request, responder);
  }

  @Override
  public boolean keepalive() {
    return true;
  }

  @Override
  public void kill() {
    for(CoreStream stream : streams.values()) {
      stream.close();
    }
  }
}

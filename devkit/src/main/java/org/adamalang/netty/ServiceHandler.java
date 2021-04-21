/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.adamalang.api.commands.CommandFactory;
import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.contracts.Backbone;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.operations.CounterFactory;
import org.adamalang.api.session.ImpersonatedSession;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.api.GameSpace;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ServiceHandler implements JsonHandler {
  private static JsonNode node(final ObjectNode request, final String field,  final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !fieldNode.isObject()) {
      throw new ErrorCodeException(errorIfDoesnt);
    }
    return fieldNode;
  }

  private static String str(final ObjectNode request, final String field, final boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !fieldNode.isTextual()) {
      if (mustExist) { throw new ErrorCodeException(errorIfDoesnt); }
      return null;
    }
    return fieldNode.textValue();
  }

  private static long lng(final ObjectNode request, final String field, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
      throw new ErrorCodeException(errorIfDoesnt);
    }
    if (fieldNode.isTextual()) {
      try {
        return Long.parseLong(fieldNode.textValue());
      } catch (NumberFormatException nfe) {
        throw new ErrorCodeException(errorIfDoesnt);
      }
    }
    return fieldNode.longValue();
  }

  private final GameSpaceDB db;
  private final ScheduledExecutorService executorDEMO;
  private final Backbone backbone;
  private final CommandFactory commandFactory;

  public ServiceHandler(final GameSpaceDB db) {
    this.db = db;
    executorDEMO = Executors.newSingleThreadScheduledExecutor();
    this.backbone = new DevKitBackbone(db, executorDEMO);
    this.commandFactory = new CommandFactory(backbone, new CounterFactory());
  }

  private String extractSpaceParameter(final ObjectNode request) throws ErrorCodeException {
    return str(request, "space", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
  }

  private GameSpace findGamespace(final ObjectNode request) throws ErrorCodeException {
    return db.getOrCreate(extractSpaceParameter(request));
  }

  @Override
  public void handle(final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    if (session == null) { throw new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_HAS_NO_SESSION); }
    final var executor = pinAndFixRequest(request);
    executor.execute(() -> {
      try {
        handleInThread(executor, session, request, responder);
      } catch (Throwable ex) {
        ex.printStackTrace();
        responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E5_REQUEST_UNKNOWN_EXCEPTION, ex));
      }
    });
  }

  public static NtClient whoFrom(final JsonNode node) {
    if (node == null) { return NtClient.NO_ONE; }
    if (node.isObject()) {
      final var data = (ObjectNode) node;
      return new NtClient(data.get("agent").textValue(), data.get("authority").textValue());
    }
    return NtClient.NO_ONE;
  }

  public void handleInThread(final ScheduledExecutorService executor, final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    final var method = str(request, "method", true, ErrorCodes.USERLAND_REQUEST_NO_METHOD_PROPERTY);
    boolean imp = request.has("devkit_who");
    final var who = imp ? whoFrom(request.get("devkit_who")) : session.who;
    switch (method) {
      case "reflect":
      case "reserve":
      case "disconnect":
      case "send":
      case "connect":
      case "create": {
        Request req = new Request(request);
        Command cmd = commandFactory.findAndInstrument(req, imp ? new ImpersonatedSession(who, session.session) : session.session, DevKitBackbone.wrap(responder));
        cmd.execute();
        return;
      }

      case "load_code": {
        String gamespace = extractSpaceParameter(request);
        try {
          JsonStreamWriter writer = new JsonStreamWriter();
          writer.beginObject();
          writer.writeObjectFieldIntro("result");
          writer.writeString(db.getCode(gamespace));
          writer.endObject();
          responder.respond(writer.toString(), true, null);
        } catch (Exception ex) {
          throw new ErrorCodeException(ErrorCodes.DEVKIT_CANTLOAD_SCRIPT, ex);
        }
        return;
      }
      case "save_code": {
        String gamespace = extractSpaceParameter(request);
        String code = str(request, "code", true, ErrorCodes.DEVKIT_REQUEST_HAS_NO_SCRIPT);
        try {
          db.saveCode(gamespace, code);
          responder.respond("{\"result\":true}", true, null);
        } catch (Exception ex) {
          throw new ErrorCodeException(ErrorCodes.DEVKIT_CANTSAVE_SCRIPT, ex);
        }
        return;
      }
      case "deploy": {
        GameSpace gs = findGamespace(request);
        gs.deploy();
        // TODO: return data about compilation... etc
        responder.respond("{\"result\":true}", true, null);
        return;
      }
      default:
        throw new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_INVALID_METHOD_PROPERTY);
    }
  }

  private ScheduledExecutorService pinAndFixRequest(final ObjectNode request) throws ErrorCodeException {
    // get the gamespace
    str(request, "space", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
    // final var method = str(request, "method", true, ErrorCodeException.USERLAND_NO_METHOD_PROPERTY);
    // based on the method, extract the game or use 0
    // hash (gamepsace, game) and pick an executor
    return executorDEMO;
  }

  @Override
  public void shutdown() {
    executorDEMO.shutdown();
  }

  private void witness(DurableLivingDocument document, ScheduledExecutorService exector) {
    Integer ms = document.getAndCleanRequiresInvalidateMilliseconds();
    if (ms != null) {
      exector.schedule(() -> {
        document.invalidate(new Callback<Integer>() {
          @Override
          public void success(Integer value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
      }, ms, TimeUnit.MILLISECONDS);
    }
  }
}

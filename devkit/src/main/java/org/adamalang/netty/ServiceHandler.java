/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.api.GameSpace;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
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

  public ServiceHandler(final GameSpaceDB db) {
    this.db = db;
    executorDEMO = Executors.newSingleThreadScheduledExecutor();
  }

  private GameSpace findGamespace(final ObjectNode request) throws ErrorCodeException {
    final var name = str(request, "gamespace", true, ErrorCodeException.USERLAND_NO_GAMESPACE_PROPERTY);
    return db.getOrCreate(name);
  }

  @Override
  public void handle(final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    if (session == null) { throw new ErrorCodeException(ErrorCodeException.USERLAND_NO_SESSION); }
    final var executor = pinAndFixRequest(request);
    executor.execute(() -> {
      try {
        handleInThread(executor, session, request, responder);
      } catch (final ErrorCodeException e) {
        e.printStackTrace();
        responder.failure(e.code, e);
      } catch (Exception e) {
        e.printStackTrace();
        responder.failure(ErrorCodeException.LIVING_DOCUMENT_TRANSACTION_SUPER_FAILURE, e);
      }
    });
  }

  public void handleInThread(final ScheduledExecutorService executor, final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    final var method = str(request, "method", true, ErrorCodeException.USERLAND_NO_METHOD_PROPERTY);
    final var who = request.has("devkit_who") ? NtClient.from(request.get("devkit_who")) : session.who;
    switch (method) {
      case "generate": {
        final var gs = findGamespace(request);
        final var result = Utility.createObjectNode();
        result.put("game", String.valueOf(gs.generate()));
        responder.respond(result, true, null);
        return;
      }
      case "reflect": {
        final var gs = findGamespace(request);
        final var result = Utility.createObjectNode();
        result.set("result", Utility.parseJsonObject(gs.reflect()));
        responder.respond(result, true, null);
        return;
      }
      case "create": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var transactor = gs.create(id, who, (ObjectNode) node(request, "arg", ErrorCodeException.USERLAND_NO_CONSTRUCTOR_ARG), str(request, "entropy", false, 0));
        final var result = Utility.createObjectNode();
        result.put("game", String.valueOf(id));
        witness(executor, transactor, responder);
        responder.respond(result, true, null);
        return;
      }
      case "connect": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var key = gs.name + ":" + id + ":" + who.agent;
        if (session.checkNotUnique(key)) {
          throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_CONNECT_AGAIN);
        }
        final var transactor = gs.get(id);
        if (transactor == null) { throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_FIND_GAME); }
        final var alreadyConnected = transactor.isConnected(who);
        if (!alreadyConnected) {
          transactor.connect(who);
        }
        Perspective perspective = new Perspective() {
          @Override
          public void data(String data) {
            responder.respond(Utility.parseJsonObject(data), false, null);
          }

          @Override
          public void disconnect() {
            // Now, this is a fun task...
          }
        };
        final var pv = transactor.createView(who, perspective);
        witness(executor, transactor, responder);
        session.subscribeToSessionDeath(key, () -> {
          // session death happens in HTTP land, so let's return to the executor to talk
          // to transactor
          executor.execute(() -> {
            pv.kill();
            if (transactor.gcViewsFor(who) == 0) {
              transactor.disconnect(who);
            }
          });
          // TODO: this does not indicate whether or not the responder failed... need to think about errors? maybe
          responder.respond(Utility.parseJsonObject("{}"), true, null);
        });
        return;
      }
      case "disconnect": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var key = gs.name + ":" + id + ":" + who.agent;
        final var result = Utility.createObjectNode();
        result.put("game", String.valueOf(id));
        result.put("success", session.unbind(key));
        responder.respond(result, true, null);
        return;
      }
      case "send": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var channel = str(request, "channel", true, ErrorCodeException.USERLAND_NO_CHANNEL_PROPERTY);
        final var msg = node(request, "message", ErrorCodeException.USERLAND_NO_MESSAGE_PROPERTY);
        final var transactor = gs.get(id);
        if (transactor == null) { throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_FIND_GAME); }
        final var result = transactor.send(who, channel, msg.toString());
        responder.respond(Utility.parseJsonObject("{\"success\":" + result.seq + "}"), true, null);
        witness(executor, transactor, responder);
        return;
      }
      default:
        throw new ErrorCodeException(ErrorCodeException.USERLAND_INVALID_METHOD_PROPERTY);
    }
  }

  private ScheduledExecutorService pinAndFixRequest(final ObjectNode request) throws ErrorCodeException {
    // get the gamespace
    str(request, "gamespace", true, ErrorCodeException.USERLAND_NO_GAMESPACE_PROPERTY);
    // final var method = str(request, "method", true, ErrorCodeException.USERLAND_NO_METHOD_PROPERTY);
    // based on the method, extract the game or use 0
    // hash (gamepsace, game) and pick an executor
    return executorDEMO;
  }

  @Override
  public void shutdown() {
    executorDEMO.shutdown();
  }

  private void witness(final ScheduledExecutorService executor, final Transactor transactor, final JsonResponder responder) {
    try {
      final var result = transactor.drive();
      if (result.needsInvalidation && result.whenToInvalidMilliseconds > 0) {
        executor.schedule(() -> {
          witness(executor, transactor, responder);
        }, result.whenToInvalidMilliseconds, TimeUnit.MILLISECONDS);
      }
    } catch (final Exception e) {
      e.printStackTrace();
      responder.failure(ErrorCodeException.LIVING_DOCUMENT_CRASHED, e);
    }
  }
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.api.GameSpace;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ServiceHandler implements JsonHandler {
  private static JsonNode node(final ObjectNode request, final String field, final boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull()) {
      if (mustExist) { throw new ErrorCodeException(errorIfDoesnt); }
      return Utility.createObjectNode();
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
      } catch (final ErrorCodeException ece) {
        responder.failure(ece.code, ece);
      }
    });
  }

  public void handleInThread(final ScheduledExecutorService executor, final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    final var method = str(request, "method", true, ErrorCodeException.USERLAND_NO_METHOD_PROPERTY);
    switch (method) {
      case "create": {
        final var gs = findGamespace(request);
        final var id = str(request, "game", true, ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var transactor = gs.create(id, session.who, (ObjectNode) node(request, "payload", false, 0), str(request, "entropy", false, 0));
        final var result = Utility.createObjectNode();
        result.put("game", id);
        witness(executor, transactor, responder);
        responder.respond(result, true, null);
      }
        return;
      case "connect": {
        final var gs = findGamespace(request);
        final var id = str(request, "game", true, ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var transactor = gs.get(id);
        if (transactor == null) { throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_FIND_GAME); }
        final var alreadyConnected = transactor.isConnected(session.who);
        if (!alreadyConnected) {
          transactor.connect(session.who);
        }
        final var pv = transactor.createView(session.who, str -> {
          responder.respond(Utility.parseJsonObject(str), false, null);
        });
        witness(executor, transactor, responder);
        session.subscribeToSessionDeath(() -> {
          // session death happens in HTTP land, so let's return to the executor to talk
          // to transactor
          executor.execute(() -> {
            pv.kill();
            if (transactor.gcViewsFor(session.who) == 0) {
              transactor.disconnect(session.who);
            }
          });
        });
      }
        return;
      case "send": {
        final var gs = findGamespace(request);
        final var id = str(request, "game", true, ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
        final var channel = str(request, "channel", true, ErrorCodeException.USERLAND_NO_CHANNEL_PROPERTY);
        final var msg = node(request, "message", true, ErrorCodeException.USERLAND_NO_MESSAGE_PROPERTY);
        final var transactor = gs.get(id);
        if (transactor == null) { throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_FIND_GAME); }
        final var result = transactor.send(session.who, channel, msg.toString());
        responder.respond(Utility.parseJsonObject("{\"success\":" + result.seq + "}"), true, null);
        witness(executor, transactor, responder);
      }
        return;
      default:
        throw new ErrorCodeException(ErrorCodeException.USERLAND_INVALID_METHOD_PROPERTY);
    }
  }

  private ScheduledExecutorService pinAndFixRequest(final ObjectNode request) throws ErrorCodeException {
    str(request, "gamespace", true, ErrorCodeException.USERLAND_NO_GAMESPACE_PROPERTY);
    final var method = str(request, "method", true, ErrorCodeException.USERLAND_NO_METHOD_PROPERTY);
    final var isGenerate = "generate".equals(method);
    final var id = str(request, "game", !isGenerate, ErrorCodeException.USERLAND_NO_GAME_PROPERTY);
    if (id == null && isGenerate) {
      request.put("method", "create");
      request.put("game", UUID.randomUUID().toString());
    }
    // hash this and pick an executor
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
    } catch (final Exception ex) {
      responder.failure(ErrorCodeException.LIVING_DOCUMENT_CRASHED, ex);
    }
  }
}

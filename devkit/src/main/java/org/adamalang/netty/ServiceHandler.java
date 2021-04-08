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
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
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

  public ServiceHandler(final GameSpaceDB db) {
    this.db = db;
    executorDEMO = Executors.newSingleThreadScheduledExecutor();
  }

  private GameSpace findGamespace(final ObjectNode request) throws ErrorCodeException {
    final var name = str(request, "gamespace", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
    return db.getOrCreate(name);
  }

  @Override
  public void handle(final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    if (session == null) { throw new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_HAS_NO_SESSION); }
    final var executor = pinAndFixRequest(request);
    executor.execute(() -> {
      try {
        handleInThread(executor, session, request, responder);
      } catch (Throwable ex) {
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
    final var who = request.has("devkit_who") ? whoFrom(request.get("devkit_who")) : session.who;
    switch (method) {
      case "generate": {
        final var gs = findGamespace(request);
        gs.generate(Callback.bind(executor, ErrorCodes.E5_REQUEST_GENERATE_CRASHED, new Callback<>() {
          @Override
          public void success(Long value) {
            responder.respond("{\"game\":\"" + value + "\"}", true, null);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.failure(ex);
          }
        }));
        return;
      }
      case "reflect": {
        responder.respond("{\"result\":" + findGamespace(request).reflect() + "}", true, null);
        return;
      }
      case "create": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
        ObjectNode arg = (ObjectNode) node(request, "arg", ErrorCodes.USERLAND_REQUEST_NO_CONSTRUCTOR_ARG);
        Callback<DurableLivingDocument> onCreate = Callback.bind(executor, ErrorCodes.E5_REQUEST_CREATE_CRASHED, new Callback<>() {
          @Override
          public void success(DurableLivingDocument value) {
            responder.respond("{\"game\":\"" + value.documentId + "\"}", true, null);
            witness(value, executor);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("FAILED TO CREATE GAME.. OK, BUT WHAT?");
            responder.failure(ex);
          }
        });
        gs.create(id, who, arg, str(request, "entropy", false, 0), onCreate);
        return;
      }
      case "connect": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
        final var key = gs.name + ":" + id + ":" + who.agent;
        if (session.checkNotUnique(key)) {
          throw new ErrorCodeException(ErrorCodes.USERLAND_SESSION_CANT_CONNECT_AGAIN);
        }
        Callback<DurableLivingDocument> onGet = Callback.bind(executor, ErrorCodes.E5_REQUEST_CONNECT_CRASHED_GET, new Callback<DurableLivingDocument>() {
          @Override
          public void success(DurableLivingDocument doc) {
            Callback<PrivateView> postPrivateView = Callback.bind(executor, ErrorCodes.E5_REQUEST_CONNECT_CRASHED_PV, new Callback<PrivateView>() {
              @Override
              public void success(PrivateView pv) {
                session.subscribeToSessionDeath(key, () -> {
                  // session death happens in HTTP land, so let's return to the executor to talk
                  // to transactor
                  executor.execute(() -> {
                    pv.kill();
                    if (doc.garbageCollectPrivateViewsFor(who) == 0) {
                      doc.disconnect(who, Callback.bind(executor, ErrorCodes.E5_REQUEST_CONNECT_CRASHED_GC, new Callback<Integer>() {
                        @Override
                        public void success(Integer value) {
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          responder.failure(ex);
                        }
                      }));
                    }
                  });
                  responder.respond("{}", true, null);
                });
                witness(doc, executor);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                responder.failure(ex);
              }
            });

            Callback<Void> postConnect = Callback.bind(executor, ErrorCodes.E5_REQUEST_CONNECT_CRASHED_POST_CONNECT, new Callback<Void>() {
              @Override
              public void success(Void value) {
                Perspective perspective = new Perspective() {
                  @Override
                  public void data(String data) {
                    executor.execute(() -> {
                      responder.respond(data, false, null);
                    });
                  }

                  @Override
                  public void disconnect() {
                    // tell the client to go away
                  }
                };
                doc.createPrivateView(who, perspective, postPrivateView);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                responder.failure(ex);
              }
            });

            final var alreadyConnected = doc.isConnected(who);
            if (!alreadyConnected) {
              doc.connect(who, Callback.transform(postConnect, ErrorCodes.E5_REQUEST_CONNECT_CRASHED_CONNECT, (x) -> null));
            } else {
              postConnect.success(null);
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.failure(ex);
          }
        });
        gs.get(id, onGet);
        return;
      }
      case "disconnect": {
        try {
          final var gs = findGamespace(request);
          final var id = lng(request, "game", ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
          final var key = gs.name + ":" + id + ":" + who.agent;
          responder.respond("{\"game\":\"" + id + "\",\"success\":"+(session.unbind(key) ? "true":"false")+"}", true, null);
          // TODO: witness!
        } catch (Throwable ex) {
          responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E5_REQUEST_DISCONNECT_CRASHED, ex));
        }
        return;
      }
      case "send": {
        final var gs = findGamespace(request);
        final var id = lng(request, "game", ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
        final var channel = str(request, "channel", true, ErrorCodes.USERLAND_REQUEST_NO_CHANNEL_PROPERTY);
        final var msg = node(request, "message", ErrorCodes.USERLAND_REQUEST_NO_MESSAGE_PROPERTY);
        Callback<DurableLivingDocument> onGet = Callback.bind(executor, ErrorCodes.E5_REQUEST_SEND_CRASHED, new Callback<DurableLivingDocument>() {
          @Override
          public void success(DurableLivingDocument value) {
            value.send(who, channel, msg.toString(), Callback.bind(executor, ErrorCodes.E5_REQUEST_SEND_CRASHED_ACTUAL, new Callback<Integer>() {
              @Override
              public void success(Integer seq) {
                responder.respond("{\"success\":" + seq + "}", true, null);
                witness(value, executor);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                responder.failure(ex);
              }
            }));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.failure(ex);
          }
        });
        gs.get(id, onGet);
        return;
      }
      default:
        throw new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_INVALID_METHOD_PROPERTY);
    }
  }

  private ScheduledExecutorService pinAndFixRequest(final ObjectNode request) throws ErrorCodeException {
    // get the gamespace
    str(request, "gamespace", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
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

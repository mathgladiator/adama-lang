package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.PrivateView;

/** command: connect a user to a document */
public class Connect implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final int id;
  private final String space;
  private long key;

  public static Connect validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Connect(context, request.id(), request.space(), request.key());
  }

  private Connect(RequestContext context, int id, String space, long key) {
    this.context = context;
    this.id = id;
    this.space = space;
    this.key = key;
  }

  // step 1: go find the data service
  @Override
  public void execute() {
    context.backbone.findDocument(space, key, this, context.responder);
  }

  // step 2: find the document, and then connect if possible
  @Override
  public void onDurableDocumentFound(DurableLivingDocument document) {
    if (!document.isConnected(context.session.who())) {
      document.connect(context.session.who(), new Callback<>() {
        @Override
        public void success(Integer value) {
          afterConnect(document);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          context.responder.error(ex);
        }
      });
    } else {
      afterConnect(document);
    }
  }

  // step 3: connect a stream
  private void afterConnect(DurableLivingDocument document) {
    document.createPrivateView(context.session.who(), new Perspective() {
      @Override
      public void data(String data) {
        context.responder.stream(data);
      }

      @Override
      public void disconnect() {
        context.responder.finish("{}");
      }
    }, new Callback<>() {
      @Override
      public void success(PrivateView pv) {
        onPrivateView(document, pv);
      }
      @Override
      public void failure(ErrorCodeException ex) {
        context.responder.error(ex);
      }
    });
  }

  // step 4: hook up disconnect
  private void onPrivateView(DurableLivingDocument document, PrivateView pv) {
    context.session.attach(id, () -> {
      pv.kill();
      if (document.garbageCollectPrivateViewsFor(context.session.who()) == 0) {
        document.disconnect(context.session.who(), Callback.DONT_CARE_INTEGER);
      } else {
        document.invalidate(Callback.DONT_CARE_INTEGER);
      }
      pv.perspective.disconnect();
    });
    context.backbone.invalidateAndSchedule(document);
  }
}


  /*
  case "connect": {
    final var gs = findGamespace(request);
    final var id = lng(request, "key", ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
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

      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.failure(ex);
      }
    });
    gs.get(id, onGet);
    return;
  }
*/
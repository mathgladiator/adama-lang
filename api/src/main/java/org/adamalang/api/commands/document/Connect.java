/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.PrivateView;

/** command: connect a user to a document */
public class Connect implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final int id;
  private final String space;
  private final String key;

  public static Connect validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Connect(context, request.id(), request.space(), request.key());
  }

  private Connect(RequestContext context, int id, String space, String key) {
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
      document.connect(context.session.who(), CommandResponder.TO_CALLBACK((iv) -> afterConnect(document), context.responder));
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
    }, CommandResponder.TO_CALLBACK((pv) -> onPrivateView(document, pv), context.responder));
  }

  // step 4: hook up disconnect
  private void onPrivateView(DurableLivingDocument document, PrivateView pv) {
    context.session.attach(id, () -> {
      pv.kill();
      if (document.garbageCollectPrivateViewsFor(context.session.who()) == 0) {
        document.disconnect(context.session.who(), Callback.DONT_CARE_INTEGER);
      }
      document.invalidate(Callback.DONT_CARE_INTEGER);
      pv.perspective.disconnect();
    });
    context.backbone.invalidateAndSchedule(document);
  }
}

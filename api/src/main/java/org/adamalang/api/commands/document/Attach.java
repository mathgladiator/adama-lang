package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: attach an asset to a document */
public class Attach implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final String space;
  private final long key;

  public static Attach validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Attach(context, request.space(), request.key());
  }

  private Attach(RequestContext context, String space, long key) {
    this.context = context;
    this.space = space;
    this.key = key;
  }

  @Override
  public void execute() {
    context.backbone.findDocument(space, key, this, context.responder);
  }

  @Override
  public void onDurableDocumentFound(DurableLivingDocument document) {
    // document.attach();
  }
}

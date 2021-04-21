package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: delete a document */
public class Delete implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final String space;
  private long key;

  public static Delete validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Delete(context, request.space(), request.key());
  }

  private Delete(RequestContext context, String space, long key) {
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
    // document.send();
  }
}

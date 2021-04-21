package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: send a message to a document */
public class Send implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final String marker;
  private final String space;
  private final long key;
  private final String channel;
  private final String message;

  public static Send validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Send(context, request.marker(), request.space(), request.key(), request.channel(), request.json_message());
  }

  private Send(RequestContext context, String marker, String space, long key, String channel, String message) {
    this.context = context;
    this.marker = marker;
    this.space = space;
    this.key = key;
    this.channel = channel;
    this.message = message;
  }

  @Override
  public void execute() {
    context.backbone.findDocument(space, key, this, context.responder);
  }

  @Override
  public void onDurableDocumentFound(DurableLivingDocument document) {
    document.send(context.session.who(), marker, channel, message, new Callback<Integer>() {
      @Override
      public void success(Integer seq) {
        context.responder.finish("{\"seq\":" + seq + "}");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        context.responder.error(ex);
      }
    });
  }
}

package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDataService;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: unsend a message to a document */
public class Unsend implements Command, CommandRequiresDataService, CommandRequiresDocument {
  private final RequestContext context;
  private final String marker;
  private final String space;
  private final long key;
  private DataService.LocalDocumentChange change;

  public static Unsend validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Unsend(context, request.marker(), request.space(), request.key());
  }

  private Unsend(RequestContext context, String marker, String space, long key) {
    this.context = context;
    this.marker = marker;
    this.space = space;
    this.key = key;
  }

  // step 1: get the data service
  @Override
  public void execute() {
    context.backbone.findDataService(space, this, context.responder);
  }

  // step 2: get the patch from the data service
  @Override
  public void onDataServiceFound(DataService service) {
    service.unsend(key, context.session.who(), marker, new Callback<DataService.LocalDocumentChange>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Unsend.this.change = value;
        context.backbone.findDocument(space, key, Unsend.this, context.responder);
      }
      @Override
      public void failure(ErrorCodeException ex) {
        context.responder.error(ex);
      }
    });
  }

  // step 3: apply the patch
  @Override
  public void onDurableDocumentFound(DurableLivingDocument document) {
    document.apply(context.session.who(), change.patch, new Callback<Integer>() {
      @Override
      public void success(Integer value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        context.responder.error(ex);
      }
    });
  }
}

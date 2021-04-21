package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresDataService;
import org.adamalang.api.commands.contracts.CommandRequiresLivingDocumentFactory;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** command: create a document */
public class Create implements Command, CommandRequiresDataService, CommandRequiresLivingDocumentFactory {
  private final RequestContext context;
  private final String space;
  private long key;
  private final String entropy;
  private String args;
  private LivingDocumentFactory factory;

  public static Create validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Create(context, request.space(), request.key(), request.entropy(), request.json_arg());
  }

  private Create(RequestContext context, String space, long key, String entropy, String args) {
    this.context = context;
    this.space = space;
    this.key = key;
    this.entropy = entropy;
    this.args = args;
  }

  @Override
  public void execute() {
    context.backbone.findLivingDocumentFactory(space, key, this, context.responder);
  }

  @Override
  public void onLivingDocumentFactory(LivingDocumentFactory factory) {
    this.factory = factory;
    context.backbone.findDataService(space, this, context.responder);
  }

  @Override
  public void onDataServiceFound(DataService service) {
    DocumentMonitor monitor = context.backbone.monitorFor(space, key);
    DurableLivingDocument.fresh(key, factory, context.session.who(), args, entropy, monitor, context.backbone.getTimeSource(), service, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument value) {
        value.invalidate(new Callback<Integer>() {
          @Override
          public void success(Integer seq) {
            context.responder.finish("{\"key\":\"" + value.documentId + "\",\"seq\":" + seq + "}");
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        context.responder.error(ex);
      }
    });
  }
}
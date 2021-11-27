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
import org.adamalang.api.commands.contracts.CommandRequiresDataService;
import org.adamalang.api.commands.contracts.CommandRequiresDocument;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: unsend a message to a document */
public class Unsend implements Command, CommandRequiresDataService, CommandRequiresDocument {
  private final RequestContext context;
  private final String marker;
  private final String space;
  private final String key;
  private DataService.LocalDocumentChange change;

  public static Unsend validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Unsend(context, request.marker(), request.space(), request.key());
  }

  private Unsend(RequestContext context, String marker, String space, String key) {
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
    service.compute(new Key(space, key), DataService.ComputeMethod.Unsend, 0, CommandResponder.TO_CALLBACK((value) -> {
      Unsend.this.change = value;
      context.backbone.findDocument(space, key, Unsend.this, context.responder);
    }, context.responder));
  }

  // step 3: apply the patch
  @Override
  public void onDurableDocumentFound(DurableLivingDocument document) {
    // TODO: only apply if at a specific sequencer, this should be applied within the durable document thread
    document.apply(context.session.who(), change.patch, CommandResponder.TO_CALLBACK((seq) -> {
      context.responder.finish("{\"seq\":" + seq + "}");
    }, context.responder));
  }
}

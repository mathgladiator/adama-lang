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
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: attach an asset to a document */
public class Attach implements Command, CommandRequiresDocument {
  private final RequestContext context;
  private final String space;
  private final String key;

  public static Attach validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Attach(context, request.space(), request.key());
  }

  private Attach(RequestContext context, String space, String key) {
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

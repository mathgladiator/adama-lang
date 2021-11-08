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
import org.adamalang.api.commands.contracts.CommandCreatesDocument;
import org.adamalang.api.commands.contracts.CommandRequiresDataService;
import org.adamalang.api.commands.contracts.CommandRequiresLivingDocumentFactory;
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** command: create a document */
public class Create implements Command, CommandRequiresDataService, CommandRequiresLivingDocumentFactory, CommandCreatesDocument {
  private final RequestContext context;
  private final String space;
  private final String key;
  private final String entropy;
  private String arg;
  private DataService service;
  private LivingDocumentFactory factory;

  public static Create validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Create(context, request.space(), request.key(), request.entropy(), request.json_arg());
  }

  private Create(RequestContext context, String space, String key, String entropy, String arg) {
    this.context = context;
    this.space = space;
    this.key = key;
    this.entropy = entropy;
    this.arg = arg;
    this.service = null;
    this.factory = null;
  }

  // step 1: find the data service
  @Override
  public void execute() {
    context.backbone.findDataService(space, this, context.responder);
  }

  // step 2: find the factory
  @Override
  public void onDataServiceFound(DataService service) {
    this.service = service;
    context.backbone.findLivingDocumentFactory(space, key, this, context.responder);
  }

  // step 3: make the document
  @Override
  public void onLivingDocumentFactory(LivingDocumentFactory factory) {
    context.backbone.makeDocument(space, key, context.session.who(), arg, entropy, service, factory, this, context.responder);
  }

  // step 4: the document is made, tell people about it
  @Override
  public void onDurableDocumentCreated(DurableLivingDocument document, int seq) {
    context.responder.finish("{\"seq\":" + seq + "}");
  }
}

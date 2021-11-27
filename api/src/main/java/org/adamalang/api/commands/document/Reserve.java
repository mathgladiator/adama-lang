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
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: reserve an id for a document */
public class Reserve implements Command, CommandRequiresDataService {
  private final RequestContext context;
  private final String space;

  public static Reserve validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Reserve(context, request.space());
  }

  private Reserve(RequestContext context, String space) {
    this.context = context;
    this.space = space;
  }

  @Override
  public void execute() {
    context.backbone.findDataService(space, this, context.responder);
  }

  @Override
  public void onDataServiceFound(DataService dataService) {
    // TODO: rethink this command, it may be best to figure out a better way
  }
}

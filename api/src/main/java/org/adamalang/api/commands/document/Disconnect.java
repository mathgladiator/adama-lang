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
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** command: disconnect a user from a document */
public class Disconnect implements Command {
  private final RequestContext context;
  private final int stream;

  public static Disconnect validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Disconnect(context, request.stream());
  }

  private Disconnect(RequestContext context, int stream) {
    this.context = context;
    this.stream = stream;
  }

  @Override
  public void execute() {
    boolean result = context.session.detach(stream);
    context.responder.finish("{\"result\":" + result + "}");
  }
}

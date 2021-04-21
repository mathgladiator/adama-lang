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

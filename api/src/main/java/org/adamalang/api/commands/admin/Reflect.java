package org.adamalang.api.commands.admin;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.RequestContext;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandRequiresLivingDocumentFactory;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** reflect on a game's source code */
public class Reflect implements Command, CommandRequiresLivingDocumentFactory {
  private final RequestContext context;
  private final String space;
  private final long key;

  public static Reflect validateAndParse(RequestContext context, Request request) throws ErrorCodeException {
    return new Reflect(context, request.space(), request.key());
  }

  private Reflect(RequestContext context, String space, long key) {
    this.context = context;
    this.space = space;
    this.key = key;
  }

  @Override
  public void execute() {
    context.backbone.findLivingDocumentFactory(space, key, this, context.responder);
  }

  @Override
  public void onLivingDocumentFactory(LivingDocumentFactory factory) {
    context.responder.finish("{\"result\":" + factory.reflection + "}");
  }
}

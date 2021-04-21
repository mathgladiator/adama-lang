package org.adamalang.api.commands;

import org.adamalang.api.commands.contracts.Backbone;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.session.Session;

/** wrapper around everything one needs to respond to a request */
public class RequestContext {
  public final Backbone backbone;
  public final Session session;
  public final CommandResponder responder;

  public RequestContext(Backbone backbone, Session session, CommandResponder responder) {
    this.backbone = backbone;
    this.session = session;
    this.responder = responder;
  }
}

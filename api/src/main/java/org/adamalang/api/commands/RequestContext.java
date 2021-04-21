/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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

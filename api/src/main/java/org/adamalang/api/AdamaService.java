/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api;

import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.session.Session;

/** the central entry point for an api */
public interface AdamaService {

  /** handle the request */
  public void handle(Session session, Request request, CommandResponder responder);
}

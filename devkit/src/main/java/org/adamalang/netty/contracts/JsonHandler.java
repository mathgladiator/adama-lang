/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.contracts;

import org.adamalang.netty.api.AdamaSession;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonHandler {
  public void handle(AdamaSession session, ObjectNode request, JsonResponder responder) throws ErrorCodeException;
  public void shutdown();
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.contracts;

import org.adamalang.netty.api.AdamaSession;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonHandler {
  public void handle(AdamaSession session, ObjectNode request, JsonResponder responder) throws ErrorCodeException;
  public void shutdown();
}

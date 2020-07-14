/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import java.util.HashMap;
import org.adamalang.runtime.api.AdamaSession;
import org.adamalang.runtime.api.ApiMethod;
import org.adamalang.runtime.api.GameSpace;
import org.adamalang.runtime.api.QueryVariant;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** contract for defining how external people should communicate to Adama via a network protocol */
public interface ApiHandler {
  public void handle(AdamaSession session, ApiMethod method, GameSpace gamespace, String id, HashMap<String, QueryVariant> query, ObjectNode data, ApiResponder responder);
}

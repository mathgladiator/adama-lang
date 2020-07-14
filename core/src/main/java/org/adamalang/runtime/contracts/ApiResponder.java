/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.ApiErrorReason;
import com.fasterxml.jackson.databind.JsonNode;

/** illustrates how the system will respond to any external api */
public interface ApiResponder {
  public void error(ApiErrorReason reason);
  public void respond(JsonNode data, boolean done);
}

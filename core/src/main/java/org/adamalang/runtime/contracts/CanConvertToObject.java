/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;

@FunctionalInterface
public interface CanConvertToObject {
  /** This is a Hack for decide/choose/fetch on channels, so those functions can
   * be polymoprhic */
  public ObjectNode convertToObjectNode();
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.JsonNode;

/** this allows the construction of privacy documents */
public interface HasPrivacyCheckAndExtract {

  /** return a private view of the item for the given person */
  public JsonNode getPrivateViewFor(NtClient __who);

  // TODO THIS SHOULD BE CODE-GEN in way to not need the interface
}

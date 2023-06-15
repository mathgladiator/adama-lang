/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: machine-start */
public class ClientMachineStartResponse {
  public final ObjectNode _original;
  public final String masterKey;
  public final String hostKey;

  public ClientMachineStartResponse(ObjectNode response) {
    this._original = response;
    this.masterKey = Json.readString(response, "master-key");
    this.hostKey = Json.readString(response, "host-key");
  }
}

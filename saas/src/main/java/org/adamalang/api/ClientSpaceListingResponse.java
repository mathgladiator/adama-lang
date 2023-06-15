/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: space-listing */
public class ClientSpaceListingResponse {
  public final ObjectNode _original;
  public final String space;
  public final String role;
  public final String created;
  public final Boolean enabled;
  public final Long storageBytes;

  public ClientSpaceListingResponse(ObjectNode response) {
    this._original = response;
    this.space = Json.readString(response, "space");
    this.role = Json.readString(response, "role");
    this.created = Json.readString(response, "created");
    this.enabled = Json.readBool(response, "enabled");
    this.storageBytes = Json.readLong(response, "storage-bytes");
  }
}

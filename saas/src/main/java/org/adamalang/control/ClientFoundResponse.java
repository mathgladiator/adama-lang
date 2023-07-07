/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: found */
public class ClientFoundResponse {
  public final ObjectNode _original;
  public final Long id;
  public final Integer locationType;
  public final String archive;
  public final String region;
  public final String machine;

  public ClientFoundResponse(ObjectNode response) {
    this._original = response;
    this.id = Json.readLong(response, "id");
    this.locationType = Json.readInteger(response, "location-type");
    this.archive = Json.readString(response, "archive");
    this.region = Json.readString(response, "region");
    this.machine = Json.readString(response, "machine");
  }
}

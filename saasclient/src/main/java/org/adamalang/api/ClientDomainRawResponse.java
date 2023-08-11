/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: domain-raw */
public class ClientDomainRawResponse {
  public final ObjectNode _original;
  public final String domain;
  public final Integer owner;
  public final String space;
  public final String key;
  public final Boolean route;
  public final String certificate;
  public final Long timestamp;

  public ClientDomainRawResponse(ObjectNode response) {
    this._original = response;
    this.domain = Json.readString(response, "domain");
    this.owner = Json.readInteger(response, "owner");
    this.space = Json.readString(response, "space");
    this.key = Json.readString(response, "key");
    this.route = Json.readBool(response, "route");
    this.certificate = Json.readString(response, "certificate");
    this.timestamp = Json.readLong(response, "timestamp");
  }
}

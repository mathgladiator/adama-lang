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

/** generated class for the responder: billing-usage */
public class ClientBillingUsageResponse {
  public final ObjectNode _original;
  public final Integer hour;
  public final Long cpu;
  public final Long memory;
  public final Integer connections;
  public final Integer documents;
  public final Integer messages;
  public final Long storageBytes;
  public final Long bandwidth;
  public final Long firstPartyServiceCalls;
  public final Long thirdPartyServiceCalls;

  public ClientBillingUsageResponse(ObjectNode response) {
    this._original = response;
    this.hour = Json.readInteger(response, "hour");
    this.cpu = Json.readLong(response, "cpu");
    this.memory = Json.readLong(response, "memory");
    this.connections = Json.readInteger(response, "connections");
    this.documents = Json.readInteger(response, "documents");
    this.messages = Json.readInteger(response, "messages");
    this.storageBytes = Json.readLong(response, "storage-bytes");
    this.bandwidth = Json.readLong(response, "bandwidth");
    this.firstPartyServiceCalls = Json.readLong(response, "first-party-service-calls");
    this.thirdPartyServiceCalls = Json.readLong(response, "third-party-service-calls");
  }
}

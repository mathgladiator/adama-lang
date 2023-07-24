/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.api.ClientDocumentCreateRequest;
import org.adamalang.api.ClientSimpleResponse;
import org.adamalang.api.SelfClient;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.function.Consumer;

public class Adama extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Adama.class);
  private final FirstPartyMetrics metrics;
  private final SelfClient client;

  public Adama(FirstPartyMetrics metrics, SelfClient client, ServiceConfig config) throws ErrorCodeException {
    super("adama", new NtPrincipal("adama", "service"), true);
    this.client = client;
    this.metrics = metrics;
  }
  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message AdamaCreateDocument_").append(uniqueId).append(" { string space; string key; maybe<long> entropy; dynamic arg; }\n");
    sb.append("message SimpleResponse_").append(uniqueId).append(" { }\n");
    sb.append("service adama {\n");
    sb.append("  class=\"adama\";\n");
    sb.append("  method secured<AdamaCreateDocument_").append(uniqueId).append(", SimpleResponse_").append(uniqueId).append("> documentCreate;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    // TODO: create an identity from who
    String identity = "anonymous:nope";
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "documentCreate": {
        ClientDocumentCreateRequest req = new ClientDocumentCreateRequest();
        req.identity = identity;
        req.space = Json.readString(node, "space");
        req.key = Json.readString(node, "key");
        if (node.has("entropy")) {
          req.entropy = "" + Json.readLong(node, "entropy");
        }
        req.arg = Json.readObject(node, "arg");
        client.documentCreate(req, new Callback<ClientSimpleResponse>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success("{}");
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      }
      case "sendDirect":
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
        return;
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}

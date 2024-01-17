/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.services.billing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.XWWWFormUrl;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Payrix  extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Payrix.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String apikey;
  private final String endpoint;

  public Payrix(FirstPartyMetrics metrics, WebClientBase base, String apikey, String endpoint) {
    super("payrix", new NtPrincipal("payrix", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.apikey = apikey;
    this.endpoint = endpoint.endsWith("/") ? endpoint : (endpoint + "/");
  }

  public static Payrix build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    String apikey = config.getDecryptedSecret("apikey");
    String endpoint = config.getString("endpoint", null);
    return new Payrix(metrics, base, apikey, endpoint);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();

    sb.append("message PayrixTxResponse {");
    sb.append("  bool success;");
    sb.append("  maybe<string> tx_id;");
    sb.append("  maybe<dynamic> error;");
    sb.append("}");

    sb.append("service payrix {\n");
    sb.append("  class=\"payrix\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("apikey")) {
      error.accept("Payrix requires an 'apikey' (and it should be encrypted)");
    }
    if (!names.contains("endpoint")) {
      error.accept("Payrix requires an 'endpoint' for either sandbox or production");
    }
    sb.append("  method<dynamic, PayrixTxResponse> PostTransaction;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "PostTransaction": {
        // remove the idempotent_key to be used as part of the header, this must exist
        JsonNode idempotentKey = node.remove("idempotent_key");
        if (idempotentKey == null || idempotentKey.isNull() || !idempotentKey.isTextual()) {
          metrics.payrix_post_tx.start().failure(ErrorCodes.FIRST_PARTY_SERVICES_PAYRIX_REQUIRES_IDEMPOTENCE_KEY);
          callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_PAYRIX_REQUIRES_IDEMPOTENCE_KEY));
          return;
        }
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("APIKEY", apikey);
        headers.put("REQUEST-TOKEN", idempotentKey.textValue());
        headers.put("Content-Type", "application/json");
        SimpleHttpRequest req = new SimpleHttpRequest("POST", endpoint + "txns", headers, SimpleHttpRequestBody.WRAP(node.toString().getBytes(StandardCharsets.UTF_8)));
        Callback<String> transformCallback = new Callback<String>() {
          @Override
          public void success(String value) {
            ObjectNode resultFromPayrix = Json.parseJsonObject(value);
            ObjectNode handoffResult = Json.newJsonObject();
            // isolate
            String txId = detectSuccessAndReturnTransactionId(resultFromPayrix);
            if (txId != null) {
              handoffResult.put("tx_id", txId);
              handoffResult.put("success", true);
            } else {
              handoffResult.set("error", resultFromPayrix);
              handoffResult.put("success", false);
            }
            callback.success(handoffResult.toString());
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        };
        base.executeShared(req, new StringCallbackHttpResponder(LOGGER, metrics.payrix_post_tx.start(), transformCallback));
        return;
      }
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  /** ultimately, the system only cares about success -> transaction id and the payrix side has all the details */
  private static String detectSuccessAndReturnTransactionId(ObjectNode resultFromPayrix) {
    try {
      JsonNode at = resultFromPayrix.get("response");
      if (!(at != null && at.isObject() && at.has("data"))) {
        return null;
      }
      at = at.get("data");
      if (!(at != null && at.isArray() && at.size() == 1)) {
        return null;
      }
      at = at.get(0);
      if (!(at != null && at.isObject() && at.has("id"))) {
        return null;
      }
      return at.get("id").textValue();
    } catch (Exception shrug) {
      return null;
    }
  }
}

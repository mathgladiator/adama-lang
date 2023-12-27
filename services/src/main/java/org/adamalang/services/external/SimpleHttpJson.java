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
package org.adamalang.services.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.metrics.ThirdPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.WebClientBase;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleHttpJson extends SimpleService {
  private static final TreeMap<String, String> METHOD_INFER = buildMethodInferenceTable(new String[] { "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"});

  private static TreeMap<String, String> buildMethodInferenceTable(String[] methods) {
    TreeMap<String, String> map = new TreeMap<>();
    for (String method : methods) {
      map.put(method, method);
      map.put(method.toLowerCase(Locale.ENGLISH), method);
      map.put(method + "_", method);
      map.put(method.toLowerCase(Locale.ENGLISH) + "_", method);
    }
    return map;
  }

  private final WebClientBase webClientBase;
  private final ThirdPartyMetrics metrics;
  private final TreeMap<String, String> headers;
  private final String endpoint;
  private final TreeSet<String> secretHeaders;

  public SimpleHttpJson(ThirdPartyMetrics metrics, WebClientBase webClientBase, TreeMap<String, String> headers, TreeSet<String> secretHeaders, String endpoint) {
    super("httpjson", new NtPrincipal("httpjson", "service"), true);
    this.headers = headers;
    this.webClientBase = webClientBase;
    this.endpoint = endpoint;
    this.metrics = metrics;
    this.secretHeaders = secretHeaders;
  }

  public static SimpleHttpJson build(ThirdPartyMetrics metrics, WebClientBase webClientBase, ServiceConfig config) throws ErrorCodeException {
    TreeMap<String, String> headers = new TreeMap<>();
    TreeSet<String> secretHeaders = new TreeSet<>();
    String endpoint = config.getString("endpoint", null);
    headers.put("Content-Type", "application/json");
    for (String key : config.getKeys()) {
      if (key.startsWith("secret_header_")) {
        String headerName = key.substring("secret_header_".length());
        headers.put(headerName, config.getDecryptedSecret(key));
        secretHeaders.add(headerName);
      } else if (key.startsWith("header_")) {
        headers.put(key.substring("header_".length()), config.getString(key, null));
      }
    }
    return new SimpleHttpJson(metrics, webClientBase, headers, secretHeaders, endpoint);
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    String httpMethod = Json.readString(node, "method");
    if (httpMethod == null) {
      httpMethod = "GET";
    }
    int kUnder = method.indexOf('_');
    if (kUnder > 0) {
      String infer = METHOD_INFER.get(method.substring(0, kUnder + 1));
      if (infer != null) {
        httpMethod = infer;
      }
    } else {
      String infer = METHOD_INFER.get(method);
      if (infer != null) {
        httpMethod = infer;
      }
    }
    String httpUri = Json.readString(node, "uri");
    if (httpUri == null) {
      httpUri = "/";
    }
    TreeMap<String, String> httpHeaders = new TreeMap<>(headers);
    JsonNode nodeHeaders = node.get("headers");
    if (nodeHeaders != null && nodeHeaders.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> it = nodeHeaders.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> newHeader = it.next();
        httpHeaders.put(newHeader.getKey(), newHeader.getValue().textValue());
      }
    }
    byte[] body = null;
    JsonNode nodeBody = node.get("body");
    if (nodeBody != null && nodeBody.isObject()) {
      body = nodeBody.toString().getBytes(StandardCharsets.UTF_8);
    }
    SimpleHttpRequest httpRequest = new SimpleHttpRequest(httpMethod, endpoint + httpUri, httpHeaders, body == null ? SimpleHttpRequestBody.EMPTY : SimpleHttpRequestBody.WRAP(body));
    webClientBase.executeShared(httpRequest, new SimpleHttpJsonResponder(metrics.tpm_json_http.start(), callback));
  }
}

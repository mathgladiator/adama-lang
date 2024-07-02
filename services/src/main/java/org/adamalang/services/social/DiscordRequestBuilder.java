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
package org.adamalang.services.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.URL;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashSet;

public class DiscordRequestBuilder {
  public static SimpleHttpRequest build(String method, String[] uri, String[] requiredParams, TreeMap<String, String> headers, ObjectNode requestNode, String body, Callback<String> callback) {
    HashSet<String> URIParams = new HashSet<>();
    for (String params : requiredParams) {
      String hasParam = params;
      if (params.startsWith("_")) {
        hasParam = params.substring(1);
      } else {
        URIParams.add(hasParam);
      }
      if (!requestNode.has(hasParam)) {
        callback.failure(new ErrorCodeException(888890, "Method is missing parameter: " + hasParam));
        return null; // Not sure if this is correct approach, or to just throw an exception?
      }
    }
    StringBuilder uriBuilder = new StringBuilder();
    uriBuilder.append("https://discord.com/api");
    int pos = 0;
    while (pos < (requiredParams.length)) {
      if (requiredParams[pos].startsWith("_")) { // Parameters starting with _ are required in the body, but not in the uri
        break;
      }
      pos++;
      if (pos - 1 < uri.length) {
        uriBuilder.append(uri[pos - 1]);
      }
      uriBuilder.append("/" + requestNode.get(requiredParams[pos - 1]).textValue());
    }
    for (int i = pos; i < uri.length; i++) {
      uriBuilder.append(uri[i]);
    }
    SimpleHttpRequestBody requestBody;
    if (method.toUpperCase().equals("GET") || method.toUpperCase().equals("DELETE")) {
      if (method.toUpperCase().equals("DELETE")) {
        requestBody = SimpleHttpRequestBody.WRAP("{}".getBytes(StandardCharsets.UTF_8));
      } else {
        requestBody = SimpleHttpRequestBody.EMPTY;
      }
      HashMap<String, String> QueryParams = new HashMap<>(requestNode.size());
      for (Iterator<Map.Entry<String, JsonNode>> it = requestNode.fields(); it.hasNext(); ) {
        Map.Entry<String, JsonNode> field = it.next();
        if (!URIParams.contains(field.getKey())) {
          QueryParams.put(field.getKey(), field.getValue().asText());
        }
      }
      uriBuilder.append(URL.parameters(QueryParams));
    } else {
      requestBody = SimpleHttpRequestBody.WRAP(body.getBytes(StandardCharsets.UTF_8));
    }
    SimpleHttpRequest req = new SimpleHttpRequest(method, uriBuilder.toString(), headers, requestBody);
    return req;
  }
}

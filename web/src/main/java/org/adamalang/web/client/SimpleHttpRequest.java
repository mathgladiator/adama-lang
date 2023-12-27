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
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.LogTimestamp;

import java.util.Map;
import java.util.TreeSet;

/** a simplified http request */
public class SimpleHttpRequest {
  public final String method;
  public final String url;
  public final Map<String, String> headers;
  public final SimpleHttpRequestBody body;

  public SimpleHttpRequest(String method, String url, Map<String, String> headers, SimpleHttpRequestBody body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }

  public ObjectNode toJsonLongEntry(TreeSet<String> secretHeadersToIgnore) {
    ObjectNode result = Json.newJsonObject();
    result.put("@timestamp", LogTimestamp.now());
    result.put("method", method);
    result.put("url", url);
    ObjectNode headersNode = result.putObject("headers");
    for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
      if (!secretHeadersToIgnore.contains(headerEntry.getKey())) {
        headersNode.put(headerEntry.getKey(), headerEntry.getValue());
      }
    }
    body.pumpLogEntry(result.putObject("body"));
    return result;
  }
}

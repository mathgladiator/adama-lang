/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.adamalang.common.Json;
import org.adamalang.web.io.ConnectionContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AdamaWebRequest {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final String parameters;
  public final String body;
  public final String identity;

  public AdamaWebRequest(final FullHttpRequest req, ChannelHandlerContext ctx) {
    headers = new TreeMap<>();
    String cookieIdentity = null;
    String bearerIdentity = null;
    for (Map.Entry<String, String> entry : req.headers()) {
      String headerName = entry.getKey().toLowerCase(Locale.ROOT);
      if (headerName.equals("cookie")) {
        for (Cookie cookie : ServerCookieDecoder.STRICT.decodeAll(entry.getValue())) {
          if ("id_default".equals(cookie.name())) {
            cookieIdentity = cookie.value();
          }
        }
        continue;
      } else if (headerName.equals("authorization")) {
        String testForIdentity = entry.getValue().stripLeading();
        if (testForIdentity.startsWith("Bearer ")) {
          bearerIdentity = testForIdentity.substring(7).trim();
        }
      }
      headers.put(headerName, entry.getValue());
    }

    String getIdentity = null;

    ConnectionContext context = ConnectionContextFactory.of(ctx, req.headers());
    headers.put("origin", context.origin + "");
    headers.put("remote-ip", context.remoteIp + "");
    QueryStringDecoder qsd = new QueryStringDecoder(req.uri());
    this.uri = qsd.path();
    {
      ObjectNode parametersJson = Json.newJsonObject();
      for (Map.Entry<String, List<String>> param : qsd.parameters().entrySet()) {
        String key = param.getKey();
        List<String> values = param.getValue();
        if ("__IDENTITY_TOKEN".equals(key)) {
          if (values.size() == 1) {
            getIdentity = values.get(0);
          }
        } else {
          if (values.size() == 0) {
            parametersJson.put(key, "");
          } else {
            parametersJson.put(key, values.get(0));
            if (values.size() > 1) {
              ArrayNode options = parametersJson.putArray(key + "*");
              for (String val : values) {
                options.add(val);
              }
            }
          }
        }
      }
      this.identity = getIdentity != null ? getIdentity : (bearerIdentity != null ? bearerIdentity :  cookieIdentity);
      this.parameters = parametersJson.toString();
    }

    if (req.method() == HttpMethod.POST || req.method() == HttpMethod.PUT) {
      byte[] memory = new byte[req.content().readableBytes()];
      req.content().readBytes(memory);
      if (memory.length == 0) {
        this.body = "{}";
      } else {
        String bodyString = new String(memory, StandardCharsets.UTF_8);
        String bodyTest = detectBodyAsQueryString(bodyString);
        if (bodyTest != null) {
          this.body = bodyTest;
        } else {
          this.body = Json.parseJsonObject(bodyString).toString();
        }
      }
    } else {
      this.body = null;
    }
  }

  public static String detectBodyAsQueryString(String body) {
    try {
      if (body.startsWith("{") || body.startsWith("[")) {
        return null;
      }
      QueryStringDecoder test = new QueryStringDecoder("/?" + body);
      ObjectNode bodyJson = Json.newJsonObject();
      for (Map.Entry<String, List<String>> param : test.parameters().entrySet()) {
        if (param.getValue().size() == 1) {
          bodyJson.put(param.getKey(), param.getValue().get(0));
        }
      }
      return bodyJson.toString();
    } catch (Exception ex) {
      return null;
    }
  }
}

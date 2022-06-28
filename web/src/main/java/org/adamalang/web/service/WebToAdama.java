/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.adamalang.common.Json;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class WebToAdama {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final String parameters;
  public final String body;

  public WebToAdama(final FullHttpRequest req) {
    headers = new TreeMap<>();
    for (Map.Entry<String, String> entry : req.headers()) {
      String headerName = entry.getKey().toLowerCase(Locale.ROOT);
      if (headerName.equals("cookie")) {
        continue;
      }
      headers.put(headerName, entry.getValue());
    }

    QueryStringDecoder qsd = new QueryStringDecoder(req.uri());
    this.uri = qsd.uri();
    {
      ObjectNode parametersJson = Json.newJsonObject();
      for (Map.Entry<String, List<String>> param : qsd.parameters().entrySet()) {
        String key = param.getKey();
        List<String> values = param.getValue();
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
      this.parameters = parametersJson.toString();
    }

    if (req.method() == HttpMethod.POST || req.method() == HttpMethod.PUT) {
      byte[] memory = new byte[req.content().readableBytes()];
      req.content().readBytes(memory);

      if (memory.length == 0) {
        this.body = "{}";
      } else {
        // TODO: DETECT application/x-www-form-urlencoded

        // Otherwise, the body is JSON
        this.body = Json.parseJsonObject(new String(memory, StandardCharsets.UTF_8)).toString();
      }
    } else {
      this.body = null;
    }
  }
}

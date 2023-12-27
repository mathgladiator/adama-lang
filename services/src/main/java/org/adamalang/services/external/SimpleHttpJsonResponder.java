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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.web.client.SimpleHttpResponder;
import org.adamalang.web.client.SimpleHttpResponseHeader;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SimpleHttpJsonResponder implements SimpleHttpResponder {
  private final RequestResponseMonitor.RequestResponseMonitorInstance monitor;
  private final Callback<String> callback;
  private ByteArrayOutputStream memory;
  private SimpleHttpResponseHeader header;
  private boolean emissionPossible;

  public SimpleHttpJsonResponder(RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<String> callback) {
    this.monitor = monitor;
    this.callback = callback;
    this.header = null;
    this.emissionPossible = true;
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    this.header = header;
    this.memory = new ByteArrayOutputStream();
  }

  @Override
  public void bodyStart(long size) {
    if (size > 64) {
      this.memory = new ByteArrayOutputStream((int) size);
    }
  }

  @Override
  public void bodyFragment(byte[] chunk, int offset, int len) {
    memory.write(chunk, offset, len);
  }

  @Override
  public void bodyEnd() {
    ObjectNode response = Json.newJsonObject();
    response.put("status", header.status);
    ObjectNode headers = response.putObject("headers");
    for (Map.Entry<String, String> headerEntry : header.headers.entrySet()) {
      headers.put(headerEntry.getKey(), headerEntry.getValue());
    }
    String body = new String(memory.toByteArray(), StandardCharsets.UTF_8);
    if (body.length() > 0) {
      response.put("has_body", true);
      try {
        response.set("body", Json.parse(body));
        response.put("parsed", true);
      } catch (Exception ex) {
        response.put("failed_body", body);
        response.put("parsed", false);
      }
    } else {
      response.put("has_body", false);
    }
    if (emissionPossible) {
      emissionPossible = false;
      monitor.success();
      System.err.println(response.toPrettyString());
      callback.success(response.toString());
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    if (emissionPossible) {
      emissionPossible = false;
      monitor.failure(ex.code);
      ObjectNode response = Json.newJsonObject();
      response.put("status", 999);
      if (header != null) {
        response.put("partial_status", header.status);
      } else {
        response.put("partial_status", 0);
      }
      response.put("error_code", ex.code);
      callback.success(response.toString());
    }
  }
}

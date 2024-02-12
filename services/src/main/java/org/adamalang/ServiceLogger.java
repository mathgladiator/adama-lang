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
package org.adamalang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.LogTimestamp;
import org.adamalang.runtime.natives.NtPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** a logger for services */
public class ServiceLogger {
  private static final Logger LOG = LoggerFactory.getLogger("services");

  public class LogInstance {
    private final ObjectNode line;

    public LogInstance() {
      line = Json.newJsonObject();
    }

    public void annotate(String key, String value) {
      line.put(key, value);
    }

    public void annotate(String key, JsonNode value) {
      line.set(key, value);
    }

    public Callback<String> wrap(Callback<String> callback) {
      long started = System.currentTimeMillis();
      return new Callback<>() {
        @Override
        public void success(String value) {
          line.set("response", Json.parse(value));
          line.put("success", false);
          line.put("latency", System.currentTimeMillis() - started);
          LOG.error(line.toString());
          callback.success(value);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          line.set("response", null);
          line.put("success", false);
          line.put("latency", System.currentTimeMillis() - started);
          line.put("failure", ex.code);
          LOG.error(line.toString());
          callback.failure(ex);
        }
      };
    }
  }

  public class ScopedServiceLogger {
    private final String service;
    private final String space;

    public ScopedServiceLogger(String service, String space) {
      this.service = service;
      this.space = space;
    }

    public LogInstance instance(NtPrincipal who, String method, String request) {
      LogInstance instance = new LogInstance();
      instance.line.put("start", LogTimestamp.now());
      instance.line.put("space", space);
      instance.line.put("service", service);
      instance.line.put("agent", who.agent);
      instance.line.put("authority", who.authority);
      instance.line.put("method", method);
      instance.line.set("request", Json.parse(request));
      return instance;
    }
  }

  public ScopedServiceLogger scope(String service, String space) {
    return new ScopedServiceLogger(service, space);
  }
}

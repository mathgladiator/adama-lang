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
package org.adamalang.system.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.runtime.sys.ServiceHeatEstimator;

import java.util.ArrayList;

/** configuration values for the service */
public class JsonConfig {
  public static class BadException extends RuntimeException {
    public BadException(String msg) {
      super(msg);
    }
  }

  protected ObjectNode cache;

  public JsonConfig(ObjectNode cache) {
    this.cache = cache;
  }

  public String get_string(String field, String defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull()) {
      if (defaultValue == null) {
        if ("identity".equals(field)) {
          throw new BadException("The config has no identity; this means you are unable to talk to adama until run: adama init (or java -jar adama.jar init)");
        }
        throw new NullPointerException("expected an '" + field + "' within the config");
      } else {
        return defaultValue;
      }
    }
    return node.textValue();
  }

  public ObjectNode read() {
    return cache;
  }

  public int get_int(String field, int defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull() || !node.isInt()) {
      return defaultValue;
    }
    return node.intValue();
  }

  public ServiceHeatEstimator.HeatVector get_heat(String suffix, int cpu_m, int messages, int mem_mb, int connections) {
    return new ServiceHeatEstimator.HeatVector(
        get_int(suffix + "-cpu-m", cpu_m) * 1000L * 1000L,
        get_int(suffix + "-messages", messages),
        get_int(suffix + "-mem-mb", mem_mb) * 1024L * 1024,
        get_int(suffix + "-connections", connections));
  }

  public ObjectNode get_or_create_child(String field) {
    JsonNode node = read().get(field);
    if (node instanceof ObjectNode) {
      return (ObjectNode) node;
    }
    return Json.newJsonObject();
  }

  public ArrayList<String> get_str_list(String field) {
    JsonNode node = read().get(field);
    if (node instanceof ArrayNode) {
      ArrayList<String> results = new ArrayList<>(node.size());
      for (int k = 0; k < node.size(); k++) {
        if (node.get(k).isTextual()) {
          results.add(node.get(k).textValue());
        }
      }
      return results;
    }
    return new ArrayList<>();
  }
}

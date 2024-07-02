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
package org.adamalang.rxhtml.preprocess.expand;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.util.HashMap;
import java.util.Map;

public class ObjectWrite {
  public final StaticConfig config;
  public final String ordering;
  public final String code;
  public final String push;
  public final String parent;
  public final String id;
  public final HashMap<String, String> properties;

  private ObjectNode cached;

  public ObjectWrite(StaticConfig config, HashMap<String, String> properties) {
    this.cached = null;
    this.config = config;
    String _id;
    if (properties.containsKey(config.order)) {
      this.ordering = properties.remove(config.order);
    } else {
      this.ordering = "";
    }
    if (properties.containsKey(config.code)) {
      this.code = properties.get(config.code);
    } else {
      this.code = null;
    }
    if (properties.containsKey(config.push)) {
      this.push = properties.remove(config.push);
    } else {
      this.push = null;
    }
    _id = this.code;
    if (properties.containsKey(config.id)) {
      _id = properties.get(config.id);
    }
    this.id = _id;
    if (properties.containsKey(config.parent)) {
      this.parent = properties.remove(config.parent);
    } else {
      this.parent = null;
    }
    properties.remove(config.children);
    properties.remove("has_" + config.children);
    this.properties = properties;
  }

  public ObjectNode convertToNode() {
    if (cached == null) {
      cached = materialize();
    }
    return cached;
  }

  private ObjectNode materialize() {
    ObjectNode child = Json.newJsonObject();
    for (Map.Entry<String, String> property : properties.entrySet()) {
      try {
        child.put(property.getKey(), Double.parseDouble(property.getValue()));
      } catch (NumberFormatException nfeD) {
        try {
          child.put(property.getKey(), Integer.parseInt(property.getValue()));
        } catch (NumberFormatException nfeI) {
          if (property.getValue().equals("true")) {
            child.put(property.getKey(), true);
          } else if (property.getValue().equals("false")) {
            child.put(property.getKey(), false);
          } else {
            child.put(property.getKey(), property.getValue());
          }
        }
      }
    }
    return child;
  }
}

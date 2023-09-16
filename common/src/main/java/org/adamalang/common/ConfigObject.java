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
package org.adamalang.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

/** handy way of interacting with JSON config */
public class ConfigObject {
  public final ObjectNode node;

  public ConfigObject(ObjectNode node) {
    this.node = node;
  }

  public int intOf(String key, int defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isInt()) {
      node.put(key, defaultValue);
      return defaultValue;
    } else {
      return v.intValue();
    }
  }

  public String strOf(String key, String defaultValue) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isTextual()) {
      node.put(key, defaultValue);
      return defaultValue;
    } else {
      return v.textValue();
    }
  }

  public String strOfButCrash(String key, String errorMessage) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isTextual()) {
      throw new NullPointerException(errorMessage);
    } else {
      return v.textValue();
    }
  }

  public ConfigObject childSearchMustExist(String message, String... keys) {
    for (String key : keys) {
      JsonNode v = node.get(key);
      if (!(v == null || v.isNull() || !v.isObject())) {
        return new ConfigObject((ObjectNode) v);
      }
    }
    throw new NullPointerException(message);
  }

  public ConfigObject child(String key) {
    JsonNode v = node.get(key);
    if (v == null || v.isNull() || !v.isObject()) {
      return new ConfigObject(node.putObject(key));
    }
    return new ConfigObject((ObjectNode) v);
  }

  public String[] stringsOf(String key, String errorMessage) {
    JsonNode vs = node.get(key);
    if (vs.isArray()) {
      ArrayList<String> strings = new ArrayList<>();
      for (int k = 0; k < vs.size(); k++) {
        if (vs.get(k).isTextual()) {
          strings.add(vs.get(k).textValue());
        }
      }
      return strings.toArray(new String[strings.size()]);
    }
    throw new NullPointerException(errorMessage);
  }

  public String[] stringsOf(String key, String[] defaultStrings) {
    JsonNode vs = node.get(key);
    if (vs != null && vs.isArray()) {
      ArrayList<String> strings = new ArrayList<>();
      for (int k = 0; k < vs.size(); k++) {
        if (vs.get(k).isTextual()) {
          strings.add(vs.get(k).textValue());
        }
      }
      return strings.toArray(new String[strings.size()]);
    }
    return defaultStrings;
  }
}

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
package org.adamalang.rxhtml.preprocess.expand;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** utility classes for ExpandStaticObjects */
public class Extraction {
  /** convert the page into a map of object names to the associated properties */
  public static HashMap<String, HashMap<String, String>> propertiesByObject(Element page, Feedback feedback, AtomicInteger genId) {
    HashMap<String, HashMap<String, String>> properties = new HashMap<>();
    for (Attribute attr : page.attributes()) {
      if (attr.getKey().startsWith("static[")) {
        String key = attr.getKey();
        int bracketStart = key.indexOf('[');
        int bracketEnd = key.indexOf(']');
        if (bracketEnd > bracketStart) {
          String objectKey = key.substring(bracketStart + 1, bracketEnd);
          HashMap<String, String> staticObjectValues = properties.get(objectKey);
          if (staticObjectValues == null) {
            staticObjectValues = new HashMap<>();
            properties.put(objectKey, staticObjectValues);
            staticObjectValues.put("uri", page.attr("uri"));
          }
          int colon = key.indexOf(':');
          String path = key.substring(colon + 1);
          String val = attr.getValue();
          if (val == null) {
            val = "";
          } else {
            val = val.trim();
          }
          if ("%%%GENERATE%%%".equals(val)) {
            val = "G" + genId.incrementAndGet();
          }
          staticObjectValues.put(path, val);
        } else {
          feedback.warn(page, "missing ] on attribute:" + key);
        }
      }
    }
    return properties;
  }

  /** extract the static configs from a document */
  public static HashMap<String, StaticConfig> staticConfigs(Document document) {
    HashMap<String, StaticConfig> configs = new HashMap<>();
    for (Element configElement : document.getElementsByTag("static-config")) {
      StaticConfig config = new StaticConfig(configElement);
      if (config.name != null) {
        configs.put(config.name, config);
      }
    }
    return configs;
  }

  public static HashMap<String, ObjectNode> staticObjects(Document document) {
    HashMap<String, ObjectNode> objects = new HashMap<>();
    for (Element configElement : document.getElementsByTag("static-object")) {
      objects.put(configElement.attr("name"), Json.parseJsonObject(configElement.text()));
    }
    return objects;
  }
}

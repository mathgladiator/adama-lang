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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Expand {
  private static final String STATIC_GUARD_PREFIX = "static:guard:";
  private static final Pattern FIND_VARIABLES = Pattern.compile("%%%([:A-Za-z_]*)%%%");

  public static void expand(Element element, String name, ObjectNode properties) {
    ArrayList<Node> replaceWith = new ArrayList<>();
    for (Node child : element.childNodes()) {
      if (child instanceof Element) {
        Node maybeAdd = eval(((Element) child).clone(), name, properties);
        if (maybeAdd != null) {
          replaceWith.add(maybeAdd);
        }
      } else {
        replaceWith.add(child);
      }
    }
    Replacement.replace(element, replaceWith);
  }

  private static String extractPath(String name, String val) {
    if (val != null) {
      int colon = val.indexOf(':');
      if (colon > 0) {
        if (val.substring(0, colon).equals(name)) {
          return val.substring(colon + 1);
        } else {
          return null;
        }
      } else {
        return val;
      }
    }
    return null;
  }

  private static boolean keepStaticIf(Element element, String name, ObjectNode properties) {
    String condition = element.attr("static:if");
    String toCheck = extractPath(name, condition);
    if (toCheck == null) {
      return true;
    }
    JsonNode node = properties.get(toCheck);
    if (node == null) {
      return false;
    }
    if (node.isBoolean()) {
      return node.asBoolean();
    }
    if (node.isIntegralNumber()) {
      return node.intValue() != 0;
    }
    return false;
  }

  private static ArrayList<Node> cloneIterateChildren(Element element, String name, ObjectNode properties) {
    ArrayList<Node> result = new ArrayList<>();
    String path = extractPath(name, element.attr("static:iterate"));
    element.removeAttr("static:iterate");
    if (path == null) {
      return result;
    }
    JsonNode node = properties.get(path);
    if (node == null || !node.isArray()) {
      return result;
    }
    ArrayNode nodeArr = (ArrayNode) node;
    for (int k = 0; k < nodeArr.size(); k++) {
      JsonNode nodeChild = nodeArr.get(k);
      if (nodeChild.isObject()) {
        ObjectNode nodeChildObject = (ObjectNode) nodeChild;
        for (Node childNode : element.childNodes()) {
          if (childNode instanceof Element) {
            Node insert = eval(((Element) childNode).clone(), name, nodeChildObject);
            if (insert != null) {
              result.add(insert);
            }
          } else {
            result.add(childNode);
          }
        }
      }
    }
    return result;
  }

  private static String lookup(ObjectNode properties, String toLookup) {
    if (toLookup != null) {
      JsonNode node = properties.get(toLookup);
      if (node != null) {
        if (node.isTextual()) {
          return node.textValue();
        }
        return node.toString();
      }
    }
    return "";
  }

  public static Node eval(Element element, String name, ObjectNode properties) {
    TreeSet<String> removals = new TreeSet<>();
    TreeMap<String, String> additions = new TreeMap<>();
    for (Attribute attr : element.attributes()) {
      if (attr.hasDeclaredValue()) {
        String value = attr.getValue();
        if (value != null) {
          value = FIND_VARIABLES.matcher(value).replaceAll((mr) -> {
            String path = extractPath(name, mr.group(1));
            if (path == null) {
              return mr.group();
            }
            return lookup(properties, path);
          });
          attr.setValue(value);
        }
        String key = attr.getKey();
        if (key.toLowerCase().startsWith(STATIC_GUARD_PREFIX)) {
          String conditionColonRealAttributeKey = key.substring(STATIC_GUARD_PREFIX.length());
          int kColon = conditionColonRealAttributeKey.indexOf(':');
          if (kColon > 0) {
            String condition = conditionColonRealAttributeKey.substring(0, kColon);
            String realKey = conditionColonRealAttributeKey.substring(kColon + 1);
            removals.add(key);
            JsonNode toEval = properties.get(condition);
            if (toEval != null && toEval.isBoolean() && toEval.asBoolean()) {
              additions.put(realKey, attr.getValue());
            }
          }
        }
      }
    }
    for (String removal : removals) {
      element.removeAttr(removal);
    }
    for (Map.Entry<String, String> addition : additions.entrySet()) {
      element.attr(addition.getKey(), addition.getValue());
    }
    if (element.tagName().equals("static:lookup")) {
      String toLookup = extractPath(name, element.attr("name"));
      if (toLookup != null) {
        return new TextNode(lookup(properties, toLookup));
      }
      return element;
    }
    if (element.hasAttr("static:if")) {
      if (!keepStaticIf(element, name, properties)) {
        return null;
      }
      element.removeAttr("static:if");
    }
    if (element.hasAttr("static:iterate")) {
      ArrayList<Node> childrenExploded = cloneIterateChildren(element, name, properties);
      Element exploded = element.shallowClone();
      for (Node child : childrenExploded) {
        exploded.appendChild(child);
      }
      return exploded;
    }
    return children(element, name, properties);
  }

  public static Element children(Element element, String name, ObjectNode properties) {
    Element rewrite = element.shallowClone();
    for (Node child : element.childNodes()) {
      if (child instanceof Element) {
        Node next = eval((Element) child, name, properties);
        if (next != null) {
          rewrite.appendChild(next);
        }
      } else {
        rewrite.appendChild(child);
      }
    }
    return rewrite;
  }
}

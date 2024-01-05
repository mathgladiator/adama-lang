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
package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Responder {
  public final String name;
  public final String camelName;
  public final boolean stream;
  public final FieldDefinition[] fields;

  public Responder(String name, boolean stream, FieldDefinition[] fields) {
    this.name = name;
    this.camelName = Common.camelize(name);
    this.stream = stream;
    this.fields = fields;
  }

  public static Map<String, Responder> respondersOf(Document document, Map<String, FieldDefinition> fields) throws Exception {
    TreeMap<String, Responder> responders = new TreeMap<>();
    NodeList list = document.getElementsByTagName("responder");
    for (int k = 0; k < list.getLength(); k++) {
      Node node = list.item(k);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element element = (Element) node;
      String name = element.getAttribute("name");
      if (name == null) {
        throw new Exception("responder needs a name");
      }
      boolean stream = "true".equals(element.getAttribute("stream"));
      ArrayList<FieldDefinition> fieldsForThisResponder = new ArrayList<>();
      NodeList children = node.getChildNodes();
      for (int j = 0; j < children.getLength(); j++) {
        Node childNode = children.item(j);
        if (childNode.getNodeType() != Node.ELEMENT_NODE) {
          continue;
        }
        Element childElement = (Element) childNode;
        switch (childElement.getTagName()) {
          case "field": {
            String fieldName = childElement.getAttribute("name");
            if (fieldName == null) {
              throw new Exception("field must have a name");
            }
            FieldDefinition fieldToUse = fields.get(fieldName);
            if (fieldToUse == null) {
              throw new Exception("field must be valid; failed to find: '" + fieldName + "'");
            }
            fieldsForThisResponder.add(fieldToUse);
          }
          break;
        }
      }
      Responder responder = new Responder(name, stream, fieldsForThisResponder.toArray(new FieldDefinition[fieldsForThisResponder.size()]));
      responders.put(responder.name, responder);
    }
    return responders;
  }

  public TreeSet<String> imports() {
    TreeSet<String> set = new TreeSet<>();
    set.add("org.adamalang.common.ErrorCodeException");
    set.add("org.adamalang.web.io.*");
    set.add("org.adamalang.common.Callback");
    set.add("com.fasterxml.jackson.databind.node.ObjectNode");
    set.add("com.fasterxml.jackson.databind.json.JsonMapper");
    return set;
  }
}

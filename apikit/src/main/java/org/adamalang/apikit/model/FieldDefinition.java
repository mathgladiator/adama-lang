/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

import org.adamalang.apikit.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.TreeMap;

public class FieldDefinition {
  public final String name;
  public final String camelName;
  public final Type type;
  public final String documentation;

  public FieldDefinition(final String name, Type type, String documentation) {
    this.name = name;
    this.camelName = Common.camelize(name, true);
    this.type = type;
    this.documentation = documentation;
  }

  public static Map<String, FieldDefinition> buildMap(Document document) throws Exception {
    TreeMap<String, FieldDefinition> fields = new TreeMap<>();
    NodeList list = document.getElementsByTagName("field-definition");
    for (int k = 0; k < list.getLength(); k++) {
      Node node = list.item(k);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element element = (Element) node;
      String name = DocumentHelper.attribute(element, "name");
      String rawType = DocumentHelper.attribute(element, "type");
      Type type = Type.of(rawType);
      String documentation = null;
      NodeList children = node.getChildNodes();
      for (int j = 0; j < children.getLength(); j++) {
        Node childNode = children.item(j);
        if (childNode.getNodeType() != Node.ELEMENT_NODE) {
          continue;
        }
        Element childElement = (Element) childNode;
        switch (childElement.getTagName()) {
          case "documentation": {
            documentation = childElement.getTextContent();
          }
          break;
        }
      }
      if (documentation == null) {
        throw new Exception("field has no documentation");
      }
      FieldDefinition definition = new FieldDefinition(name, type, documentation);
      if (fields.containsKey(name)) {
        throw new Exception("field already defined: " + name);
      }
      fields.put(name, definition);
    }
    return fields;
  }
}

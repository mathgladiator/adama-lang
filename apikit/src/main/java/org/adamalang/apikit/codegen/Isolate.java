/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.ParameterDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Isolate {

  public static TreeSet<String> scopesOf(Document doc) {
    TreeSet<String> scopes = new TreeSet<>();
    NodeList nodes = doc.getElementsByTagName("method");
    for (int k = 0; k < nodes.getLength(); k++) {
      Element node = (Element) nodes.item(k);
      if (!node.hasAttribute("scope")) {
        throw new RuntimeException(node.getAttribute("name") + " lacks a scope attribute");
      }
      String scope = node.getAttribute("scope");
      if (!(scope.equals("global") || scope.equals("region") || scope.equals("super"))) {
        throw new RuntimeException("unknown scope:[" + scope + "]");
      }
      scopes.add(scope);
    }
    return scopes;
  }

  public static Map<String, ParameterDefinition> scopeParameters(Document doc, Map<String, ParameterDefinition> all, String scope) {
    NodeList nodes = doc.getElementsByTagName("method");
    TreeMap<String, ParameterDefinition> scoped = new TreeMap<>();
    for (int k = 0; k < nodes.getLength(); k++) {
      Element node = (Element) nodes.item(k);
      if (scope.equals(node.getAttribute("scope"))) {
        NodeList params = node.getElementsByTagName("parameter");
        for (int j = 0; j < params.getLength(); j++) {
          Element param = (Element) params.item(j);
          String name = param.getAttribute("name");
          scoped.put(name, all.get(name));
        }
      }
    }
    return scoped;
  }


}

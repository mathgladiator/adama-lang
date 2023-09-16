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
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
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
      if (!(scope.equals("global") || scope.equals("region"))) {
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

  public static Method[] scopeMethods(Method[] methods, String scope) {
    ArrayList<Method> next = new ArrayList<>();
    for (Method method : methods) {
      if (scope.equals(method.scope)) {
        next.add(method);
      }
    }
    return next.toArray(new Method[next.size()]);
  }


}

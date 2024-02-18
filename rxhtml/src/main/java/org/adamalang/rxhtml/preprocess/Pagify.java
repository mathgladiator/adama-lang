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
package org.adamalang.rxhtml.preprocess;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** work across pages to stamp common properties, simplify page construction, and build useful indices */
public class Pagify {
  public static void pagify(Document document) {
    // Index all <common-page> elements and join the attributes that we can stamp out
    HashMap<String, HashMap<String, String>> apply = new HashMap<>();
    for (Element element : document.getElementsByTag("common-page")) {
      if (element.hasAttr("uri:prefix")) {
        String prefix = element.attr("uri:prefix");
        HashMap<String, String> clone = apply.get(prefix);
        if (clone == null) {
          clone = new HashMap<>();
          apply.put(prefix, clone);
        }
        for (Attribute attr : element.attributes()) {
          String key = attr.getKey();
          if (key.startsWith("static:") || key.startsWith("template:") || key.startsWith("init:") || "bundle".equals(key) || "authenticate".equals(key) || "privacy".equals(key)) {
            clone.put(key, attr.getValue());
          }
        }
      }
    }

    // TODO: look into using a treemap for the prefix matching
    Function<String, HashMap<String, String>> find = (uri) -> {
      for (Map.Entry<String, HashMap<String, String>> kvp : apply.entrySet()) {
        if (uri.startsWith(kvp.getKey())) {
          return kvp.getValue();
        }
      }
      return null;
    };

    // enforce templates easy mode
    for (Element element : document.getElementsByTag("page")) {
      // STEP 1: apply a <common-page> rule
      String uri = element.attr("uri");
      HashMap<String, String> clone = find.apply(uri);
      if (clone != null) {
        for (Map.Entry<String, String> a : clone.entrySet()) {
          if (!element.hasAttr(a.getKey())) {
            element.attr(a.getKey(), a.getValue());
          }
        }
      }

      // STEP 2: inject a template
      if (element.hasAttr("template:use")) {
        String tag = element.hasAttr("template:tag") ? element.attr("template:tag") : "div";
        Element newRoot = new Element(tag);
        newRoot.attr("rx:template", element.attr("template:use"));
        for (Node node : element.childNodes()) {
          newRoot.appendChild(node.clone());
          node.remove();
        }
        element.appendChild(newRoot);
      }
    }
  }
}

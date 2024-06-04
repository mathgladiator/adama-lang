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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.preprocess.expand.*;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExpandStaticObjects {


  public static void expand(Document document, Feedback feedback) {
    // for generating unique throw-away values
    AtomicInteger genId = new AtomicInteger(1);

    { // phase 0: static tree cloning and stamping
      // extract all static-tree fragment elements
      HashMap<String, Element> treeFragments = new HashMap<>();
      for (Element element : document.getElementsByTag("static-tree-fragment")) {
        String name = element.attr("name");
        if (name != null) {
          treeFragments.put(name, element.clone());
        }
        element.remove();
      }

      // execute replacements
      for (Element element : document.getElementsByTag("static-tree-replace")) {
        String name = element.attr("name");
        if (name != null) {
          Element fragment = treeFragments.get(name);
          if (fragment != null) {
            Replacement.replace(element, fragment.clone().childNodes());
          }
        } else {
          element.remove();
        }
      }
    }

    // extract the configs from the document and provide a way to find/invent them
    HashMap<String, StaticConfig> configs = Extraction.staticConfigs(document);
    Function<String, StaticConfig> findConfig = (String name) -> {
      StaticConfig found = configs.get(name);
      if (found == null) {
        found = new StaticConfig(null);
        configs.put(name, found);
      }
      return found;
    };

    // convert the pages into object writes
    HashMap<String, ArrayList<ObjectWrite>> writes = new HashMap<>();

    Consumer<Element> handlePage = (page) -> {
      HashMap<String, HashMap<String, String>> properties = Extraction.propertiesByObject(page, feedback, genId);
      for (Map.Entry<String, HashMap<String, String>> entry : properties.entrySet()) {
        ArrayList<ObjectWrite> writesToObject = writes.get(entry.getKey());
        if (writesToObject == null) {
          writesToObject = new ArrayList<>();
          writes.put(entry.getKey(), writesToObject);
        }
        writesToObject.add(new ObjectWrite(findConfig.apply(entry.getKey()), entry.getValue()));
      }
    };
    for (Element page : document.getElementsByTag("page")) {
      handlePage.accept(page);
    }
    for (Element page : document.getElementsByTag("pseudo-page")) {
      handlePage.accept(page);
    }

    // assemble the objects
    TreeMap<String, ObjectNode> staticObjects = new TreeMap<>();
    for (Map.Entry<String, ArrayList<ObjectWrite>> entry : writes.entrySet()) {
      ArrayList<ObjectWrite> objectWrites = entry.getValue();
      objectWrites.sort(Comparator.comparing(o -> o.ordering));
      ObjectNode rootObject = Json.newJsonObject();
      staticObjects.put(entry.getKey(), rootObject);
      ArrayNode root = rootObject.putArray("pages");
      HashMap<String, ObjectNode> parents = new HashMap<>();
      // establish parents
      for (ObjectWrite write : objectWrites) {
        ObjectNode child = write.convertToNode();
        if (write.id != null) {
          parents.put(write.id, child);
        }
      }
      // construct the hierarchy
      for (ObjectWrite write : objectWrites) {
        if (write.parent != null) {
          ObjectNode parent = parents.get(write.parent);
          if (parent != null) {
            if (parent.has(write.config.children)) {
              ((ArrayNode) parent.get(write.config.children)).add(write.convertToNode());
            } else {
              parent.putArray(write.config.children).add(write.convertToNode());
              parent.put("has_" + write.config.children, true);
            }
          }
        } else {
          root.add(write.convertToNode());
        }
      }
    }
    staticObjects.putAll(Extraction.staticObjects(document));

    for (Element element : document.getElementsByTag("static-expand")) {
      String source = element.attr("source");
      if (source != null && staticObjects.containsKey(source)) {
        ObjectNode node = staticObjects.get(source);
        Expand.expand(element, source, node);
      }
    }
  }
}

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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/** The base environment for checking types */
public class RxRootEnvironment {
  public final Document document;
  public final Feedback feedback;
  public final HashMap<String, ObjectNode> reflections;
  public final HashSet<DedupeTemplateCheck> dedupeTemplates;
  public final String defaultBackend;
  public final HashMap<String, Element> templates;

  public RxRootEnvironment(String forest, File root, Feedback feedback) {
    this.document = Jsoup.parse(forest);
    this.feedback = feedback;
    this.reflections = new HashMap<>();
    this.dedupeTemplates = new HashSet<>();
    String _defaultBackend = null;
    BiFunction<Element, String, Boolean> load = (element, name) -> {
      if (!reflections.containsKey(name)) {
        try {
          File fileToLoad = new File(root, name);
          if (!fileToLoad.exists()) {
            fileToLoad = new File(root, name + ".json");
          }
          String json = Files.readString(fileToLoad.toPath());
          reflections.put(name, Json.parseJsonObject(json));
          return true;
        } catch (Exception ex) {
          feedback.warn(element, "The input file for reflection '" + name + "' failed with " + ex.getMessage());
        }
      }
      return false;
    };
    for (Element element : this.document.getElementsByTag("shell")) {
      if (element.hasAttr("default-backend")) {
        String candidate = element.attr("default-backend");
        if (load.apply(element, candidate)) {
          _defaultBackend = candidate;
        }
      }
    }
    this.defaultBackend = _defaultBackend;
    for (Element element : this.document.getElementsByTag("connection")) {
      if (element.hasAttr("backend")) {
        load.apply(element, element.attr("backend"));
      }
    }
    for (Element element : this.document.getElementsByTag("domain-get")) {
      if (element.hasAttr("backend")) {
        load.apply(element, element.attr("backend"));
      }
    }
    this.templates = new HashMap<>();
    for (Element element : this.document.getElementsByTag("template")) {
      String name = element.attr("name");
      if (name != null) {
        templates.put(name, element);
      }
    }
  }

  private class StateNode {
    private final String struct;

    public StateNode(String struct) {
      this.struct = struct;
    }
  }

  private class StatePage {
    private final String privacy;
    private final Stack<StateNode> data;
    private String currentStruct;
    private ObjectNode dataTypesForest;

    private StatePage(String privacy, String struct, ObjectNode dataTypesForest) {
      this.privacy = privacy;
      this.data = new Stack<>();
      this.data.push(new StateNode(struct));
      this.currentStruct = struct;
      this.dataTypesForest = null;
    }

    public StatePage fork(ObjectNode forest) {
      return new StatePage(privacy, "__Root", forest);
    }
  }

  private void children(Element parent, StatePage state) {
    for (Element child : parent.children()) {
      check(child, state);
    }
  }

  private void base(Element element, StatePage state) {
  }

  public void connection(Element connection, StatePage state) {
    String backend = defaultBackend;
    if (connection.hasAttr("backend")) {
      backend = connection.attr("backend");
    }
    ObjectNode reflect = reflections.get(backend);
    if (reflect == null) {
      if (backend != null) {
        feedback.warn(connection, "The backend '" + backend + "' was not available");
      }
      children(connection, state);
    } else {
      children(connection, state.fork(reflect));
    }
  }

  public void lookup(Element lookup, StatePage state) {
    String path = lookup.attr("path");
  }

  private void check(Element element, StatePage state) {
    String tag = element.tagName().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote("_"), "").toLowerCase(Locale.ENGLISH);
    try {
      Method method = RxRootEnvironment.class.getMethod(tag, Element.class, StatePage.class);
      method.invoke(this, element, state);
    } catch (NoSuchMethodException nme) {
      base(element, state);
      children(element, state);
    } catch (Exception ex) {
      feedback.warn(element, "problem:" + ex.getMessage());
    }
  }

  public void check() {
    for (Element element : document.getElementsByTag("page")) {
      String privacy = "unknown";
      if (element.hasAttr("privacy")) {
        privacy = element.attr("privacy");
      }
      String struct = null;
      check(element, new StatePage(privacy, struct, null));
    }
  }
}

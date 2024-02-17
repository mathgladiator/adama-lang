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
import org.adamalang.rxhtml.preprocess.Pagify;
import org.adamalang.rxhtml.template.Base;
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
    Pagify.pagify(document);
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

  private void children(Element parent, PageEnvironment env) {
    for (Element child : parent.children()) {
      check(child, env);
    }
  }

  private void childrenCase(Element parent, String caseValue, PageEnvironment env) {
    for (Element child : parent.children()) {
      if (child.hasAttr("rx:case")) {
        if (!child.attr("rx:case").equals(caseValue)) {
          continue;
        }
      }
      check(child, env);
    }
  }

  private void checkCondition(String condition, PageEnvironment env) {
    // TODO: parse the condition
    // TODO: validate condition logic
  }

  private void base(Element element, PageEnvironment env) {
    PageEnvironment next = env;
    boolean expand = element.hasAttr("rx:expand-view-state");

    if (element.hasAttr("rx:scope")) {
      // TODO: do scoping rules
    }
    if (element.hasAttr("rx:iterate")) {
      // TODO: do scoping rules
    }
    if (element.hasAttr("rx:repeat")) {
      // TODO: validate the result is an integer
    }
    if (element.hasAttr("rx:if")) {
      checkCondition(element.attr("rx:if"), env);
    }
    if (element.hasAttr("rx:ifnot")) {
      checkCondition(element.attr("rx:ifnot"), env);
    }
    if (element.hasAttr("rx:switch")) {
      // TODO: validate EXISTS as either INT, LONG, ENUM, STRING, BOOL
    }
    if (element.hasAttr("rx:repeat")) {
      // TODO: validate EXISTS as either INT, LONG
    }

    if (element.hasAttr("rx:template")) {
      String templateToUse = element.attr("rx:template");
      Element template = next.findTemplate(templateToUse);
      if (template != null) {
        children(template, next.withFragmentProvider(element));
        return;
      } else {
        // TODO: warn about template not found
      }
    }

    children(element, env);
  }

  public void inlinetemplate(Element inlinetemplate, PageEnvironment env) {
    String templateToUse = inlinetemplate.attr("name");
    Element template = env.findTemplate(templateToUse);
    if (template != null) {
      children(template, env.withFragmentProvider(inlinetemplate));
      return;
    } else {
      // TODO: warn about template not found
    }
    children(inlinetemplate, env);
  }

  public void fragment(Element fragment, PageEnvironment env) {
    if (fragment.hasAttr("case")) {
      String caseValue = fragment.attr("case");
      childrenCase(env.getFragmentProvider(), caseValue, env);
    } else {
      children(env.getFragmentProvider(), env);
    }
  }

  public void todotask(Element fragment, PageEnvironment env) {
    // ignore
  }

  public void connection(Element connection, PageEnvironment env) {
    String backend = defaultBackend;
    if (connection.hasAttr("backend")) {
      backend = connection.attr("backend");
    }
    String name = "default";
    if (connection.hasAttr("name")) {
      name = connection.attr("name");
    }
    ObjectNode reflect = reflections.get(backend);
    if (reflect == null) {
      if (backend != null) {
        feedback.warn(connection, "The backend '" + backend + "' was not available");
      }
      children(connection, env);
    } else {
      DataScope child = DataScope.root(reflect);
      env.registerConnection(name, child);
      children(connection, env.withDataScope(child));
    }
  }

  public void pick(Element pick, PageEnvironment env) {
    String name = "default";
    if (pick.hasAttr("name")) {
      name = pick.attr("name");
    }
    PageEnvironment next = env.maybePickConnection(name);
    if (next != null) {
      children(pick, next);
    } else {
      // TODO: WARN
      children(pick, env);
    }
  }

  public void lookup(Element lookup, PageEnvironment env) {
    String path = lookup.attr("path");
  }

  private void check(Element element, PageEnvironment env) {
    String tag = Base.normalizeTag(element.tagName());
    try {
      Method method = RxRootEnvironment.class.getMethod(tag, Element.class, PageEnvironment.class);
      method.invoke(this, element, env);
    } catch (NoSuchMethodException nme) {
      base(element, env);
    } catch (Exception ex) {
      feedback.warn(element, "problem:" + ex.getMessage());
    }
  }

  public void check() {
    for (Element element : document.getElementsByTag("page")) {
      String privacy = "";
      if (element.hasAttr("privacy")) {
        privacy = element.attr("privacy");
      }
      check(element, PageEnvironment.newPage(privacy, templates));
    }
  }
}

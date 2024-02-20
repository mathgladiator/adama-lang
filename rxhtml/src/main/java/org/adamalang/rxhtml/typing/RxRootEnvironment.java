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
import org.adamalang.rxhtml.Loader;
import org.adamalang.rxhtml.ProductionMode;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/** The base environment for checking types */
public class RxRootEnvironment {
  public final Document document;
  public final Feedback feedback;
  public final HashMap<String, ObjectNode> reflections;
  public final HashSet<DedupeTemplateCheck> dedupeTemplates;
  public final String defaultBackend;
  public final HashMap<String, Element> templates;
  public final HashMap<String, HashSet<String>> templateUsageByPage;

  public RxRootEnvironment(String forest, File root, Feedback feedback) {
    this.document = Loader.parseForest(forest, ProductionMode.Web);
    this.feedback = feedback;
    this.reflections = new HashMap<>();
    this.dedupeTemplates = new HashSet<>();
    this.templateUsageByPage = new HashMap<>();
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
        if (templates.containsKey(name)) {
          feedback.warn(element, "template '" + name + "' already defined");
        }
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

  private void checkCondition(String condition, PageEnvironment env, BiFunction<PageEnvironment, String, String> findScopable, Consumer<String> reportError) {
    if (condition.startsWith("decide:")) {
      String decider = condition.substring(7);
      // TODO
      return;
    }
    if (condition.startsWith("choose:") || condition.startsWith("chosen:")) {
      String choose = condition.substring(7);
      // TODO
      return;
    }
    if (condition.startsWith("finalize:")) {
      String choose = condition.substring(9);
      // TODO:
      return;
    }

    if (condition.startsWith("eval:")) {
      // TODO:
      return;
    }
    Consumer<String> checkBranch = (branch) -> {
      if (branch != null) {
        DataSelector selector = env.scope.select(env.privacy, branch, reportError);
        if (selector != null) {
          selector.validateAttribute(reportError);
        }
      }
    };


    int kEq = condition.indexOf('=');
    if (kEq > 0) {
      checkBranch.accept(findScopable.apply(env, condition.substring(0, kEq).trim()));
      checkBranch.accept(findScopable.apply(env, condition.substring(kEq + 1).trim()));
    } else {
      String b = findScopable.apply(env, condition);
      if (b != null) {
        DataSelector selector = env.scope.select(env.privacy, b, reportError);
        if (selector != null) {
          selector.validateBoolean(reportError);
        }
      }
    }

    // TODO: parse the condition
    // TODO: validate condition logic
    /*
    DataSelector selector = env.scope.select(env.privacy, condition, reportError);
    if (selector != null) {

    }
     */
  }

  private void _checkValue(Element element, PageEnvironment env, String... attributes) {
    Consumer<String> reportError = (err) -> feedback.warn(element, err);
    for (String attr : attributes) {
      if (element.attributes().hasDeclaredValueForKey(attr)) {
        String valueToCheck = element.attr(attr);
        try {
          Tree tree = Parser.parse(valueToCheck);
          for (String query : tree.queries()) {
            DataSelector selector = env.scope.select(env.privacy, query, reportError);
            if (selector != null) {
              selector.validateAttribute(reportError);
            }
          }
        } catch (ParseException pe) {
          reportError.accept("failed to parse ATL: " + valueToCheck);
        }
      }
    }
  }

  private void base(Element element, PageEnvironment env) {
    PageEnvironment next = env;
    boolean expand = element.hasAttr("rx:expand-view-state");
    Consumer<String> reportError = (err) -> feedback.warn(element, err);

    BiFunction<PageEnvironment, String, String> findScopable = (e, key) -> {
      if (e.scope == null) {
        return null;
      }
      if (element.hasAttr(key)) {
        String value = element.attr(key).trim();
        if ("".equals(value)) {
          feedback.warn(element, "attribute '" + key + "' is present but empty");
          return null;
        }
        return value;
      }
      return null;
    };
    String toScopeInto = findScopable.apply(next, "rx:scope");
    if (toScopeInto != null) {
      DataSelector selector = next.scope.select(next.privacy, toScopeInto, reportError);
      if (selector != null) {
        DataScope scopedInto = selector.scopeInto(reportError);
        if (scopedInto != null) {
          next = next.withDataScope(scopedInto);
        }
      }
    }
    String toIterateInto = findScopable.apply(next, "rx:iterate");
    if (toIterateInto != null) {
      DataSelector selector = next.scope.select(next.privacy, toIterateInto, reportError);
      if (selector != null) {
        DataScope scopedIterate = selector.iterateInto(reportError);
        if (scopedIterate != null) {
          next = next.withDataScope(scopedIterate);
        }
      }
    }
    String toRepeat = findScopable.apply(next, "rx:repeat");
    if (toRepeat != null) {
      DataSelector selector = next.scope.select(next.privacy, toRepeat, reportError);
      if (selector != null) {
        selector.validateIntegral(reportError);
      }
    }
    String toMonitor = findScopable.apply(next, "rx:monitor");
    if (toMonitor != null) {
      DataSelector selector = next.scope.select(next.privacy, toMonitor, reportError);
      if (selector != null) {
        selector.validateIntegral(reportError);
      }
    }
    String toIfCheck = findScopable.apply(next, "rx:if");
    if (toIfCheck != null) {
      checkCondition(toIfCheck, next, findScopable, reportError);
    }
    String toIfNotCheck = findScopable.apply(next, "rx:ifnot");
    if (toIfNotCheck != null) {
      checkCondition(toIfNotCheck, env, findScopable, reportError);
    }
    String toSwitch = findScopable.apply(next, "rx:switch");
    if (toSwitch != null) {
      DataSelector selector = next.scope.select(next.privacy, toSwitch, reportError);
      if (selector != null) {
        selector.validateSwitchable(reportError);
      }
    }
    if (element.hasAttr("rx:template")) {
      String templateToUse = element.attr("rx:template");
      Element template = next.findTemplate(templateToUse);
      if (template != null) {
        children(template, next.withFragmentProvider(element));
        return;
      } else {
        reportError.accept("template '" + templateToUse + "' not found");
      }
    }

    for (Attribute attr : element.attributes()) {
      if (attr.hasDeclaredValue()) {
        _checkValue(element, env, attr.getValue());
      }
    }
    children(element, next);
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
    children(env.getFragmentProvider(), env);
  }

  public void lookup(Element lookup, PageEnvironment env) {
    _checkValue(lookup, env, "path");
  }

  public void monitor(Element monitor, PageEnvironment env) {
    _checkValue(monitor, env, "path");
  }

  public void title(Element title, PageEnvironment env) {
    _checkValue(title, env, "title");
  }

  public void viewwrite(Element viewwrite, PageEnvironment env) {
    _checkValue(viewwrite, env, "value");
  }

  public void todotask(Element fragment, PageEnvironment env) {
    // ignore
  }

  public void viewstateparams(Element vsp, PageEnvironment env) {
    ArrayList<String> sync = new ArrayList<>();
    for (Attribute attr : vsp.attributes()) {
      if (attr.hasDeclaredValue() && attr.getKey().startsWith("sync:")) {
        sync.add(attr.getValue());
      }
    }
    _checkValue(vsp, env, sync.toArray(new String[sync.size()]));
  }

  public void form(Element form, PageEnvironment env) {
    if (form.hasAttr("rx:action")) {
      String action = form.attr("rx:action");
      if (action.startsWith("send:")) {
        String channel = action.substring(5);
        if (env.scope == null) {
          feedback.warn(form, "no data channel for a form to send channel '" + channel + "' on");
        } else {
          if(!env.scope.hasChannel(channel)) {
            feedback.warn(form, "channel '" + channel + "' was not found");
          }
        }
      }
      // TODO: all the strange things
    }
    children(form, env);
  }

  public void exitgate(Element exitgate, PageEnvironment env) {
    // TODO: both guard and set are related to the view:
  }

  public void domainget(Element domainGet, PageEnvironment env) {
    children(domainGet, env.withDataScope(null));
  }

  public void documentget(Element domainGet, PageEnvironment env) {
    children(domainGet, env.withDataScope(null));
  }

  public void localstoragepoll(Element domainGet, PageEnvironment env) {
    children(domainGet, env.withDataScope(null));
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
    _checkValue(connection, env, "identity", "redirect", "name");
    if (!(connection.hasAttr("use-domain") || connection.hasAttr("billing"))) {
      _checkValue(connection, env, "space", "key");
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
    HashSet<String> templatesUnused = new HashSet<>(templates.keySet());
    for (Element element : document.getElementsByTag("page")) {
      String privacy = "";
      if (element.hasAttr("privacy")) {
        privacy = element.attr("privacy");
      }
      String uri = element.attr("uri");
      HashSet<String> templatesUsed = new HashSet<>();
      templateUsageByPage.put(uri, templatesUsed);
      check(element, PageEnvironment.newPage(privacy, templates, templatesUnused, templatesUsed));
    }
    for (String unused : templatesUnused) {
      feedback.warn(templates.get(unused), "template '" + unused + "' was not used");
    }
  }
}

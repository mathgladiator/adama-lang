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
package org.adamalang.rxhtml.template;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Escaping;
import org.adamalang.common.Json;
import org.jsoup.nodes.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Base {
  public static final String[] EVENTS = new String[]{
    "click", // buttons
    "mouseenter", "mouseleave", // regions
    "blur", "focus", "change", // inputs
    "rise", "fall", // monitor
    "check", "uncheck", // checkboxes
    "new", // a new item appeared in a list
    "keyup", "keydown", // keyboard antics
    "settle", "settle-once", // RxHTML: fires when the data tree has settled
    "load", // RxHTML: fires after 1ms of creating the DOM
    "ordered", // RxHTML: fires after elements within a DOM have been ordered
    "success", "failure", // RxHTML + forms: success or failure fires when a form action is performed
    "submit", "submitted", "aftersync"};

  private static String xmlnsOf(Environment env) {
    String xmlns = env.element.hasAttr("xmlns") ? env.element.attr("xmlns") : null;
    if (env.element.tagName().equals("svg")) {
      xmlns = "http://www.w3.org/2000/svg";
    }
    if (xmlns == null) {
      xmlns = env.xmlns;
    }
    return xmlns;
  }

  private static boolean testRxCaseIfSoThenOpen(Environment env) {
    boolean hasCase = env.element.hasAttr("rx:case") && env.caseVar != null;
    if (hasCase) {
      env.writer.tab().append("if (").append(env.caseVar).append(" == '").append(env.element.attr("rx:case")).append("') {").tabUp().newline();
      return true;
    }
    return false;
  }

  private static void wrapUpRxCase(Environment env, boolean hasCase) {
    if (hasCase) {
      env.writer.tabDown().tab().append("}").newline();
    }
  }

  private static String[] extractDelayEvents(Environment env) {
    ArrayList<String> delayEvents = new ArrayList<>();
    for (Attribute attr : env.element.attributes().asList()) {
      if (attr.getKey().startsWith("rx:delay")) {
        delayEvents.add(attr.getKey().substring(3));
      }
    }
    return delayEvents.toArray(new String[delayEvents.size()]);
  }

  private static class IntroHandoff {
    public final String eVar;
    public final Attributes rx;

    public IntroHandoff(String eVar, Attributes rx) {
      this.eVar = eVar;
      this.rx = rx;
    }
  }

  private static IntroHandoff writeIntro(Environment env, String xmlns) {
    String eVar = env.pool.ask();
    env.writer.tab().append("var ").append(eVar).append("=$.E('").append(env.element.tagName()).append("'").append(xmlns != null ? ", '" + xmlns + "'" : "").append(");").newline();
    Attributes rx = new Attributes(env, eVar);
    rx._base();
    if (env.element.tagName().equals("form") && env.element.hasAttr("rx:action")) {
      rx._action();
    }
    for (String event : EVENTS) {
      if (env.element.hasAttr("rx:" + event)) {
        rx._event(event);
      }
    }
    for (String delay : extractDelayEvents(env)) {
      rx._delay(delay);
    }
    if (env.element.hasAttr("rx:link")) {
      env.writer.tab().append(eVar).append(".link(").append(env.stateVar).append(",'").append(env.element.attr("rx:link")).append("',$);").newline();
    }
    return new IntroHandoff(eVar, rx);
  }

  private static int countAttr(Element element, String... attrs) {
    int count = 0;
    for (String attr : attrs) {
      if (element.hasAttr(attr)) {
        count++;
      }
    }
    return count;
  }

  private static void body(Environment env, String eVar, ObjectNode config) {
    if (countAttr(env.element, "rx:iterate", "rx:repeat", "rx:if", "rx:ifnot", "rx:wrap", "rx:custom", "rx:switch", "rx:template") > 1) {
      env.feedback.warn(env.element, "Too many incompatible rx:flags");
    }
    Attributes rx = new Attributes(env, eVar);
    if (env.element.hasAttr("rx:iterate")) {
      rx._iterate();
    } else if (env.element.hasAttr("rx:repeat")) {
      rx._repeat();
    } else if (env.element.hasAttr("rx:if")) {
      rx._if();
    } else if (env.element.hasAttr("rx:ifnot")) {
      rx._ifnot();
    } else if (env.element.hasAttr("rx:wrap")) {
      rx._wrap();
    } else if (env.element.hasAttr("rx:custom")) {
      rx._custom(config);
    } else if (env.element.hasAttr("rx:switch")) {
      rx._switch();
    } else if (env.element.hasAttr("rx:template")) {
      rx._template(config);
    } else {
      children(env);
    }
    if (env.element.hasAttr("rx:monitor")) {
      rx._monitor();
    }
  }

  public static ObjectNode extractConfig(Element element) {
    ObjectNode config = Json.newJsonObject();
    ArrayList<String> removal = new ArrayList<>();
    for (Attribute attr : element.attributes()) {
      if (attr.getKey().startsWith("config:")) {
        removal.add(attr.getKey());
        String key = attr.getKey().substring(7).trim().toLowerCase(Locale.ENGLISH);
        String value = "true";
        if (attr.hasDeclaredValue()) {
          value = attr.getValue();
        }
        try {
          config.put(key, Integer.parseInt(value));
        } catch (NumberFormatException nfe1) {
          try {
            config.put(key, Double.parseDouble(value));
          } catch (NumberFormatException nfe2) {
            if (value.equals("true")) {
              config.put(key, true);
            } else if (value.equals("false")) {
              config.put(key, false);
            } else {
              config.put(key, value);
            }
          }
        }
      }
    }
    for (String toRemove : removal) {
      element.removeAttr(toRemove);
    }
    return config;
  }

  public static String write(Environment env, boolean returnVariable) {
    env.writeElementDebugIfTest();

    boolean hasConfigCheck = env.element.hasAttr("config:if");
    if (hasConfigCheck) {
      String configExpr = env.element.attr("config:if").trim();
      env.element.removeAttr("config:if");
      if (env.configVar != null) {
        boolean not = configExpr.startsWith("!");
        if (not) {
          configExpr = configExpr.substring(1).trim();
        }
        int kEq = configExpr.indexOf('=');
        if (kEq >= 0) {
          String key = configExpr.substring(0, kEq).trim().toLowerCase(Locale.ENGLISH);
          String value = Escapes.constantOf(configExpr.substring(kEq + 1).trim());
          env.writer.tab().append("if(").append(not ? "!" : "").append("$.CFGt(").append(env.configVar).append(",'").append(key).append("',").append(value).append(")) ").append("{").tabUp().newline();
        } else {
          env.writer.tab().append("if(").append(not ? "!" : "").append("$.CFGb(").append(env.configVar).append(",'").append(configExpr.toLowerCase(Locale.ENGLISH)).append("')) ").append("{").tabUp().newline();
        }
      } else {
        hasConfigCheck = false;
      }
    }
    // get the namespace of the element
    String xmlns = xmlnsOf(env);

    // does the element have an rx:case
    boolean hasCase = testRxCaseIfSoThenOpen(env);

    // discover if the children has static content
    boolean hasStaticContent = env.element.hasAttr("static:content");
    if (hasStaticContent) {
      env.element.removeAttr("static:content");
    }

    // for rx:case, only inject the children with "children-only" attribute
    boolean hasChildrenOnlyAttribute = env.element.hasAttr("children-only");
    if (hasChildrenOnlyAttribute) {
      env.element.removeAttr("children-only");
    }
    boolean childrenOnly = hasCase && hasChildrenOnlyAttribute && env.parentVariable != null && !returnVariable && !hasStaticContent;

    Integer protectUserScrollDelay = null;
    if (env.element.hasAttr("protect-user-scroll")) {
      protectUserScrollDelay = 10000;
      try {
        protectUserScrollDelay = Integer.parseInt(env.element.attr("protect-user-scroll"));
      } catch (Exception ex) {
      }
      env.element.removeAttr("protect-user-scroll");
    }

    ObjectNode config = extractConfig(env.element);

    // introduce the element
    IntroHandoff handoff = null;
    final String eVar;
    final boolean eVarCreated;
    if (hasCase && childrenOnly) {
      eVar = env.parentVariable;
      eVarCreated = false;
    } else {
      handoff = writeIntro(env, xmlns);
      eVar = handoff.eVar;
      eVarCreated = true;
      if (protectUserScrollDelay != null) {
        env.writer.tab().append("$.pUs(").append(eVar).append(",").append("" + protectUserScrollDelay).append(");").newline();
      }
    }

    if (hasStaticContent) {
      // flush out the static content in an isolate innerHTML set
      String innerHTML = new Escaping(env.element.html()).go().trim();
      if (innerHTML.length() > 0) {
        env.writer.tab().append("$.SIH(").append(eVar).append(",\"").append(innerHTML).append("\");").newline();
      }
      if (env.parentVariable != null) {
        env.writer.tab().append(env.parentVariable).append(".append(").append(eVar).append(");").newline();
      }
      if (returnVariable) {
        return eVar;
      } else {
        if (eVarCreated) {
          env.pool.give(eVar);
        }
        return null;
      }
    }

    // start build a new environment
    Environment next = env.parentVariable(eVar);
    if (xmlns != null) {
      next = next.xmlns(xmlns);
    }
    // apply rx:scope to the environment
    if (env.element.hasAttr("rx:scope")) {
      StatePath path = StatePath.resolve(env.element.attr("rx:scope"), env.stateVar);
      String newStateVar = env.pool.ask();
      if (env.element.hasAttr("rx:expand-view-state")) {
        env.writer.tab().append("var ").append(newStateVar).append("=$.pIE(").append(path.command).append(",'").append(path.name).append("', true);").newline();
      } else {
        env.writer.tab().append("var ").append(newStateVar).append("=$.pI(").append(path.command).append(",'").append(path.name).append("');").newline();
      }
      next = next.stateVar(newStateVar);
    }

    // write the body of the element (the children and more)
    body(next, eVar, config);

    // if we have a parent variable, then append it
    if (env.parentVariable != null) {
      env.writer.tab().append(env.parentVariable).append(".append(").append(eVar).append(");").newline();
    }
    if (handoff != null && env.element.hasAttr("rx:behavior")) {
      handoff.rx._behavior(config);
    }
    wrapUpRxCase(env, hasCase);
    if (hasConfigCheck) {
      env.writer.tabDown().tab().append("}").newline();
    }
    if (returnVariable) {
      return eVar;
    } else {
      if (eVarCreated) {
        env.pool.give(eVar);
      }
      return null;
    }
  }

  private static ArrayList<Node> filtered(Environment env) {
    ArrayList<Node> filtered = new ArrayList<>();
    for (int k = 0; k < env.element.childNodeSize(); k++) {
      Node node = env.element.childNode(k);
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        if (!text.text().trim().equalsIgnoreCase("")) {
          filtered.add(node);
        }
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof org.jsoup.nodes.Element) {
        if (checkEnv((Element) node, env.environment)) {
          filtered.add(node);
        }
      }
    }
    return filtered;
  }

  public static void children(Environment env) {
    children(env, (x) -> true);
  }

  public static boolean checkEnv(Element node, String env) {
    if (node.hasAttr("for-env")) {
      boolean contains = false;
      for (String e : node.attr("for-env").split(Pattern.quote("|"))) {
        if (env.equalsIgnoreCase(e.trim())) {
          contains = true;
        }
      }
      node.removeAttr("for-env");
      return contains;
    } else if (node.hasAttr("for-env-except")) {
      boolean contains = false;
      for (String e : node.attr("for-env-except").split(Pattern.quote("|"))) {
        if(env.equalsIgnoreCase(e.trim())) {
          contains = true;
        }
      }
      node.removeAttr("for-env-except");
      return !contains;
    } else {
      return true;
    }
  }


  public static void children(Environment env, Function<Node, Boolean> filter) {
    ArrayList<Node> nodes = filtered(env);
    for (Node node : nodes) {
      if (!filter.apply(node)) {
        continue;
      }
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        env.writer.tab().append(env.parentVariable).append(".append($.T('").append(new Escaping(text.text()).switchQuotes().go()).append("'));").newline();
      } else if (node instanceof org.jsoup.nodes.Element) {
        org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
        Environment childEnv = env.element(child, nodes.size() == 1);
        try {
          // use reflection to see if Elements has an override for this normalized tag name.
          String tagNameNormal = normalizeTag(child.tagName());
          Method method = Elements.class.getMethod(tagNameNormal, Environment.class);
          method.invoke(null, childEnv);
        } catch (Exception ex) {
          Base.write(childEnv, false);
        }
      }
    }
  }

  public static String normalizeTag(String tag) {
    return tag.replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote("_"), "").toLowerCase(Locale.ENGLISH);
  }
}

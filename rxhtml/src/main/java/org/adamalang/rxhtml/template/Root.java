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

import org.adamalang.rxhtml.acl.commands.Set;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class Root {
  public static void start(Environment env, String custom) {
    env.writer.append("(function($){").tabUp().newline();
    if (custom.length() > 0) {
      env.writer.append("/** BEGIN CUSTOM **/").newline();
      env.writer.append(custom).newline();
      env.writer.append("/** END CUSTOM **/").newline();
    }
  }

  public static void template(Environment env) {
    String parentVar = env.pool.ask();
    String stateVar = env.pool.ask();
    String fragmentFunc = env.pool.ask();
    String name = env.element.attr("name");
    env.writeElementDebugIfTest();
    env.writer.tab().append("$.TP('").append(name).append("', function(").append(parentVar).append(",").append(stateVar).append(",").append(fragmentFunc).append(") {").newline().tabUp();
    String autoVar = env.pool.ask();
    env.writer.tab().append("var ").append(autoVar).append("=$.X();").newline();
    Feedback feedback = env.feedback;
    Base.children(env.stateVar(stateVar).parentVariable(parentVar).fragmentFunc(fragmentFunc).feedback("template " + name, (e, msg) -> feedback.warn(e, "template " + name + ":" + msg)).autoVar(autoVar));
    env.pool.give(parentVar);
    env.pool.give(stateVar);
    env.pool.give(fragmentFunc);
    env.writer.tabDown().tab().append("});").newline();
  }

  public static void page(Environment env, ArrayList<String> defaultRedirects) {
    String stateVar = env.pool.ask();
    String rootVar = env.pool.ask();
    Environment envToUse = env.parentVariable(rootVar).stateVar(stateVar).raiseOptimize();
    String uri = env.element.attr("uri");
    Instructions instructions = uri_to_instructions(uri);
    env.writeElementDebugIfTest();
    env.writer.tab().append("$.PG(").append(instructions.javascript).append(", function(").append(rootVar).append(",").append(stateVar).append(") {").newline().tabUp();
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("init:")) {
        String key = attr.getKey().substring(5);
        String value = attr.getValue();
        env.writer.tab().append(stateVar).append(".view.init['").append(key).append("']=").append(Escapes.constantOf(value)).append(";").newline();
      }
    }
    if (env.element.hasAttr("authenticate")) {
      String identity = env.element.attr("authenticate");
      if (identity == null || identity.trim().equals("")) {
        identity = "default";
      }
      ArrayList<String> failAuthRedirects = new ArrayList<>(defaultRedirects);
      if (env.element.hasAttr("redirect")) {
        String redirectTo = env.element.attr("redirect");
        if (redirectTo != null && !redirectTo.trim().equals("")) {
          failAuthRedirects.add(redirectTo);
        }
      }
      String varForAuthTest = env.pool.ask();
      String zeroArgs = "$.aRDz('/');";
      String pullArgs = null;
      for (String redirect : failAuthRedirects) {
        // parse the redirect
        try {
          Tree tree = Parser.parse(redirect);
          Map<String, String> vars = tree.variables();
          if (tree.hasAuto()) {
            env.feedback.warn(env.element, "redirects can't use auto variable");
          }
          if (vars.size() == 0) {
            zeroArgs = "$.aRDz('" + redirect + "');";
          } else {
            boolean hasAll = true;
            ArrayList<String> varsToPullFromView = new ArrayList<>();
            for (String key : vars.keySet()) {
              varsToPullFromView.add("'" + key + "'");
              if (!instructions.depends.contains(key)) {
                hasAll = false;
                break;
              }
            }
            if (hasAll) {
              pullArgs = "$.aRDp(" + stateVar + ",function(vs) { return " + tree.js(env.contextOf("initial-view-state"), "vs") + ";});";
            }
          }
        } catch (ParseException pe) {
          env.feedback.warn(env.element, "redirect '" + redirect + "' has parser problems; " + pe.getMessage());
        }
      }
      env.writer.tab().append("var ").append(varForAuthTest).append("=");
      if (pullArgs != null) {
        env.writer.append(pullArgs).newline();
      } else {
        env.writer.append(zeroArgs).newline();
      }
      env.writer.tab().append("if($.ID('").append(identity).append("',").append(varForAuthTest).append(").abort) {").tabUp().newline();
      env.writer.tab().append("return;").tabDown().newline();
      env.writer.tab().append("}").newline();
      env.pool.give(varForAuthTest);
    }
    String autoVar = env.pool.ask();
    env.writer.tab().append("var ").append(autoVar).append("=$.X();").newline();
    Feedback prior = env.feedback;
    Base.children(envToUse.feedback("page:" + uri, (e, msg) -> prior.warn(e, uri + ":" + msg)).autoVar(autoVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(rootVar);
    env.pool.give(stateVar);
  }

  /** convert a raw uri to an instruction set */
  public static Instructions uri_to_instructions(String uriRaw) {
    HashSet<String> depends = new HashSet<>();
    String uri = (uriRaw.startsWith("/") ? uriRaw.substring(1) : uriRaw).trim();
    StringBuilder formula = new StringBuilder();
    if (uriRaw.startsWith("/")) {
      formula.append("/");
    }
    TreeMap<String, String> types = new TreeMap<>();
    StringBuilder normalized = new StringBuilder();
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean first = true;
    do {
      int kSlash = uri.indexOf('/');
      String fragment = kSlash >= 0 ? uri.substring(0, kSlash).trim() : uri;
      uri = kSlash >= 0 ? uri.substring(kSlash + 1).trim() : "";
      if (!first) {
        sb.append(",");
      }
      first = false;
      if (fragment.startsWith("$")) {
        int colon = fragment.indexOf(':');
        String type = (colon > 0 ? fragment.substring(colon + 1) : "text").trim().toLowerCase();
        String name = (colon > 0 ? fragment.substring(1, colon) : fragment.substring(1)).trim();
        switch (type) {
          case "text":
          case "string":
          case "str":
            type = "text";
            break;
          case "number":
          case "int":
          case "double":
            type = "number";
        }
        depends.add(name);
        sb.append("'").append(type).append("','").append(name).append("'");
        types.put(name, type);
        formula.append("{").append(name).append("}");
        normalized.append("/$").append(type);
      } else {
        normalized.append("/").append(fragment);
        sb.append("'fixed','").append(fragment).append("'");
        formula.append(fragment);
      }
      if (kSlash >= 0) {
        formula.append("/");
      }
    } while (uri.length() > 0);
    sb.append("]");
    return new Instructions(sb.toString(), depends, formula.toString(), normalized.toString(), types);
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("})(RxHTML);").newline();
    return env.writer.toString();
  }

  public static class Instructions {
    public final String javascript;
    public final HashSet<String> depends;
    public final String formula;
    public final String normalized;
    public final TreeMap<String, String> types;

    public Instructions(final String javascript, HashSet<String> depends, String formula, String normalized, TreeMap<String, String> types) {
      this.javascript = javascript;
      this.depends = depends;
      this.formula = formula;
      this.normalized = normalized;
      this.types = types;
    }
  }
}

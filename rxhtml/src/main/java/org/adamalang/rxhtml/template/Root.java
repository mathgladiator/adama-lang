/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.routing.Instructions;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.nodes.Attribute;

import java.util.ArrayList;
import java.util.Map;

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
    String configVar = env.pool.ask();
    String name = env.element.attr("name");
    env.writeElementDebugIfTest();
    env.writer.tab().append("$.TP('").append(name).append("', function(").append(parentVar).append(",").append(stateVar).append(",").append(fragmentFunc).append(",").append(configVar).append(") {").newline().tabUp();
    String autoVar = env.pool.ask();
    env.writer.tab().append("var ").append(autoVar).append("=$.X();").newline();
    Feedback feedback = env.feedback;
    Base.children(env.stateVar(stateVar).parentVariable(parentVar).fragmentFunc(fragmentFunc).feedback("template " + name, (e, msg) -> feedback.warn(e, "template " + name + ":" + msg)).autoVar(autoVar).configVar(configVar));
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
    Instructions instructions = Instructions.parse(uri);
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

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("})(RxHTML);").newline();
    return env.writer.toString();
  }

}

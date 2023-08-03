/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.template.config.Feedback;

import java.util.ArrayList;
import java.util.HashSet;
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
    String name = env.element.attr("name");
    env.writer.tab().append("$.TP('").append(name).append("', function(").append(parentVar).append(",").append(stateVar).append(",").append(fragmentFunc).append(") {").newline().tabUp();
    Feedback feedback = env.feedback;
    Base.children(env.stateVar(stateVar).parentVariable(parentVar).fragmentFunc(fragmentFunc).feedback((e, msg) -> feedback.warn(e, "template " + name + ":" + msg)));
    env.pool.give(parentVar);
    env.pool.give(stateVar);
    env.pool.give(fragmentFunc);
    env.writer.tabDown().tab().append("});").newline();
  }

  public static void page(Environment env, ArrayList<String> defaultRedirects) {
    String stateVar = env.pool.ask();
    String rootVar = env.pool.ask();
    Environment envToUse = env.parentVariable(rootVar).stateVar(stateVar);
    String uri = env.element.attr("uri");
    Instructions instructions = uri_to_instructions(uri);
    env.writer.tab().append("$.PG(").append(instructions.javascript).append(", function(").append(rootVar).append(",").append(stateVar).append(") {").newline().tabUp();
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
              pullArgs = "$.aRDp(" + stateVar + ", function(vs) { return " + tree.js(env.contextOf("initial-view-state"), "vs") + "; });";
            }
          }
        } catch (ParseException pe) {
          env.feedback.warn(env.element, "redirect '" + redirect + "' has parser problems; " + pe.getMessage());
        }
      }
      env.writer.tab().append("var ").append(varForAuthTest).append(" = ");
      if (pullArgs != null) {
        env.writer.append(pullArgs).newline();
      } else {
        env.writer.append(zeroArgs).newline();
      }
      env.writer.tab().append("if ($.ID('").append(identity).append("',").append(varForAuthTest).append(").abort) {").tabUp().newline();
      env.writer.tab().append("return;").tabDown().newline();
      env.writer.tab().append("}").newline();
      env.pool.give(varForAuthTest);

    }
    Feedback prior = env.feedback;
    Base.children(envToUse.feedback((e, msg) -> prior.warn(e, uri + ":" + msg)));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(rootVar);
    env.pool.give(stateVar);
  }

  /** convert a raw uri to an instruction set */
  public static Instructions uri_to_instructions(String uriRaw) {
    HashSet<String> depends = new HashSet<>();
    String uri = uriRaw.startsWith("/") ? uriRaw.substring(1) : uriRaw;
    StringBuilder formula = new StringBuilder();
    if (uriRaw.startsWith("/")) {
      formula.append("/");
    }
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean first = true;
    do {
      int kSlash = uri.indexOf('/');
      String fragment = kSlash >= 0 ? uri.substring(0, kSlash) : uri;
      uri = kSlash >= 0 ? uri.substring(kSlash + 1) : "";
      if (!first) {
        sb.append(",");
      }
      first = false;
      if (fragment.startsWith("$")) {
        int colon = fragment.indexOf(':');
        String type = colon > 0 ? fragment.substring(colon + 1) : "text";
        String name = colon > 0 ? fragment.substring(1, colon) : fragment.substring(1);
        depends.add(name);
        sb.append("'").append(type).append("','").append(name).append("'");
        formula.append("{").append(name).append("}");
      } else {
        sb.append("'fixed','").append(fragment).append("'");
        formula.append(fragment);
      }
      if (kSlash >= 0) {
        formula.append("/");
      }
    } while (uri.length() > 0);
    sb.append("]");
    return new Instructions(sb.toString(), depends, formula.toString());
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("})(RxHTML);").newline();
    return env.writer.toString();
  }

  public static class Instructions {
    public final String javascript;
    public final HashSet<String> depends;
    public final String formula;

    public Instructions(final String javascript, HashSet<String> depends, String formula) {
      this.javascript = javascript;
      this.depends = depends;
      this.formula = formula;
    }
  }
}

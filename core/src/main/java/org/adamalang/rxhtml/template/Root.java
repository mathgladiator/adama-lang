/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.template;

public class Root {
  public static void start(Environment env) {
    env.writer.append("(function($){").tabUp().newline();
  }

  public static void template(Environment env) {
    String parentVar = env.pool.ask();
    String stateVar = env.pool.ask();
    String fragmentFunc = env.pool.ask();
    env.writer.tab().append("$.TP('").append(env.element.attr("name")).append("', function(").append(parentVar).append(",").append(stateVar).append(",").append(fragmentFunc).append(") {").newline().tabUp();
    Base.children(env.stateVar(stateVar).parentVariable(parentVar).fragmentFunc(fragmentFunc));
    env.pool.give(parentVar);
    env.pool.give(stateVar);
    env.pool.give(fragmentFunc);
    env.writer.tabDown().tab().append("});").newline();
  }

  public static void page(Environment env, String defaultRedirect) {
    String stateVar = env.pool.ask();
    String rootVar = env.pool.ask();
    env.writer.tab().append("$.PG(").append(uri_to_instructions(env.element.attr("uri"))).append(", function(").append(rootVar).append(",").append(stateVar).append(") {").newline().tabUp();
    if (env.element.hasAttr("authenticate")) {
      String identity = env.element.attr("authenticate");
      if (identity == null || identity.trim().equals("")) {
        identity = "default";
      }
      String redirect = defaultRedirect;
      if (env.element.hasAttr("redirect")) {
        String redirectTo = env.element.attr("redirect");
        if (redirectTo != null && !redirectTo.trim().equals("")) {
          redirect = redirectTo;
        }
      }
      env.writer.tab().append("if ($.ID('").append(identity).append("','").append(redirect != null ? redirect : "").append("').abort) {").tabUp().newline();
      env.writer.tab().append("return;").tabDown().newline();
      env.writer.tab().append("}").newline();
    }
    Base.children(env.parentVariable(rootVar).stateVar(stateVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(rootVar);
    env.pool.give(stateVar);
  }

  public static String uri_to_instructions(String uriRaw) {
    String uri = uriRaw.startsWith("/") ? uriRaw.substring(1) : uriRaw;
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
        sb.append("'").append(type).append("','").append(name).append("'");
      } else {
        sb.append("'fixed','").append(fragment).append("'");
      }
    } while (uri.length() > 0);
    sb.append("]");
    return sb.toString();
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("})(RxHTML);").newline();
    return env.writer.toString();
  }
}

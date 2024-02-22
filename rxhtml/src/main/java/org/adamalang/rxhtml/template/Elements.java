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
import org.adamalang.common.Hashing;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Elements {

  private static boolean isElse(Node node) {
    return (node.hasAttr("rx:else") || node.hasAttr("rx:disconnected") || node.hasAttr("rx:failed"));
  }

  private static Function<Node, Boolean> TRUE_BRANCH = (node) -> {
    if (node instanceof Element) {
      return !isElse(node);
    } else {
      return true;
    }
  };

  private static Function<Node, Boolean> FALSE_BRANCH = (node) -> {
    if (node instanceof Element) {
      return isElse(node);
    } else {
      return false;
    }
  };

  public static void template(Environment env) { /* no-op */ }

  public static void page(Environment env) { /* no-op */ }

  public static void shell(Environment env) { /* no-op */ }

  public static void style(Environment env) { /* no-op */ }

  public static void script(Environment env) { /* no-op */ }

  public static void meta(Environment env) { /* no-op */ }

  public static void fragment(Environment env) {
    String caseToUse = "";
    if (env.element.hasAttr("case")) {
      caseToUse = env.element.attr("case");
    }
    env.writer.tab().append(env.fragmentFunc).append("(").append(env.parentVariable).append(",").append(env.stateVar).append(",'").append(caseToUse).append("');").newline();
  }

  public static void monitor(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    int delay = 10;
    if (env.element.hasAttr("delay")) {
      try {
        delay = Integer.parseInt(env.element.attr("delay"));
      } catch (Exception ex) {
      }
    }
    env.writer.tab().append("$.MN(").append(env.parentVariable).append(",").append(path.command).append(",'").append(path.name).append("',").append(env.element.hasAttr("skip-first") ? "true" : "false").append("," + delay).append(");").newline();
    Attributes attributes = new Attributes(env, env.parentVariable);
    attributes._event("rise");
    attributes._event("fall");
  }

  public static void viewwrite(Environment env) {
    StatePath path = StatePath.resolve("view:" + env.element.attr("path"), env.stateVar);
    RxObject obj = new RxObject(env, "value");
    env.writer.tab().append("$.VW(").append(path.command).append(",'").append(path.name).append("',").append(obj.rxObj).append(");").newline();
    obj.finish();
  }

  public static void lookup(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    String transform = env.element.attr("transform");
    if ("html".equals(transform)) { // TOTAL HACK FOR A DEPLOYMENT
      env.writer.tab().append(env.parentVariable).append(".append($.Lh(").append(path.command).append(",'").append(path.name).append("'));").newline();
      return;
    }
    if (transform == null || "".equals(transform)) {
      env.writer.tab().append(env.parentVariable).append(".append($.L(").append(path.command).append(",'").append(path.name).append("'));").newline();
    } else {
      if (env.element.hasAttr("refresh")) {
        int freq = 10000;
        try {
          freq = Integer.parseInt(env.element.attr("refresh"));
        } catch (Exception ex) {
          env.feedback.warn(env.element, env.element.attr("refresh") + " should be a numeric value");
        }
        env.writer.tab().append(env.parentVariable).append(".append($.LTdT(").append(path.command).append(",'").append(path.name).append("',$.TR('").append(transform).append("'),").append("" + freq).append("));").newline();
      } else {
        env.writer.tab().append(env.parentVariable).append(".append($.LT(").append(path.command).append(",'").append(path.name).append("',$.TR('").append(transform).append("')));").newline();
      }
    }
  }

  public static void trustedhtml(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    env.writer.tab().append(env.parentVariable).append(".append($.Lh(").append(path.command).append(",'").append(path.name).append("'));").newline();
  }

  public static String ensureInView(String attr) {
    if (attr.startsWith("view:")) {
      return attr;
    } else {
      return "view:" + attr.replaceAll(Pattern.quote("data:"), "");
    }
  }

  public static void exitgate(Environment env) {
    boolean hasGuard = env.element.hasAttr("guard");
    boolean hasSet = env.element.hasAttr("set");
    if (hasGuard && hasSet) {
      StatePath pathGuard = StatePath.resolve(ensureInView(env.element.attr("guard")), env.stateVar);
      StatePath pathSet = StatePath.resolve(ensureInView(env.element.attr("set")), env.stateVar);
      env.writer.tab().append("$.IG(") //
          .append(pathGuard.command).append(",'").append(pathGuard.name).append("',") //
          .append(pathSet.command).append(",'").append(pathSet.name).append("');").newline(); //
    } else {
      if (!hasGuard) {
        env.feedback.warn(env.element, "needs a guard attribute");
      }
      if (!hasSet) {
        env.feedback.warn(env.element, "needs a set attribute");
      }
    }
  }

  public static void todotask(Environment env) {
    String section = env.section;
    String description = env.element.html();
    MessageDigest md5 = Hashing.md5();
    md5.update(section.getBytes(StandardCharsets.UTF_8));
    md5.update(description.getBytes(StandardCharsets.UTF_8));
    String id = Hashing.finishAndEncode(md5);
    env.tasks.add(new Task(id, section, description));
    env.writer.tab().append("$.TASK(").append(env.parentVariable).append(",'").append(id).append("','").append(section).append("',\"").append(new Escaping(description).go()).append("\");\n");
  }

  public static void title(Environment env) {
    RxObject obj = new RxObject(env, "value");
    env.writer.tab().append("$.ST(").append(obj.rxObj).append(");").newline();
    obj.finish();
  }

  public static void viewstateparams(Environment env) {
    ArrayList<String> params = new ArrayList<>();
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("sync:")) {
        params.add("'" + attr.getKey().substring(5) + "'");
      }
    }
    var sp = StatePath.resolve("view:/root", env.stateVar);
    env.writer.tab().append("$.VSP(").append(env.parentVariable).append(",").append(sp.command).append(",[").append(String.join(",", params)).append("]);").newline();
  }

  @Deprecated
  public static void viewsync(Environment env) {
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.VSy(").append(parentVar).append(",").append(env.stateVar).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }

  private static String soloParent(Environment env) {
    final String parentVar;
    if (env.elementAlone) {
      parentVar = env.parentVariable;
    } else {
      parentVar = env.pool.ask();
      env.writer.tab().append("var ").append(parentVar).append("=$.E('div');").newline();
      env.writer.tab().append(env.parentVariable).append(".append(").append(parentVar).append(");").newline();
    }
    return parentVar;
  }

  public static void connectionstatus(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.CSt(").append(parentVar).append(",").append(env.stateVar).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("});").newline();
  }

  public static void connection(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    if (!env.element.hasAttr("redirect")) {
      env.element.attr("redirect", "/sign-in");
    }
    if (env.element.hasAttr("billing")) {
      // TODO: test this
      RxObject obj = new RxObject(env, "name", "identity", "redirect");
      env.writer.tab().append("$.BCONNECT(") //
          .append(env.stateVar) //
          .append(",").append(obj.rxObj) //
          .append(");").newline();
      obj.finish();
      if (env.element.childNodeSize() > 0) {
        Elements.pick(env);
      }
    } else if (env.element.hasAttr("use-domain")) {
      RxObject obj = new RxObject(env, "name", "identity", "redirect");
      env.writer.tab().append("$.DCONNECT(") //
          .append(env.stateVar) //
          .append(",").append(obj.rxObj) //
          .append(");").newline();
      obj.finish();
      if (env.element.childNodeSize() > 0) {
        Elements.pick(env);
      }
    } else {
      // TODO: validate that space and key are set
      RxObject obj = new RxObject(env, "name", "space", "key", "identity", "redirect");
      env.writer.tab().append("$.CONNECT(") //
          .append(env.stateVar) //
          .append(",").append(obj.rxObj) //
          .append(");").newline();
      obj.finish();
      if (env.element.childNodeSize() > 0) {
        Elements.pick(env);
      }
    }
  }

  public static void localstoragepoll(Environment env) {
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    RxObject obj = new RxObject(env, "key", "ms");
    env.writer.tab().append("$.LoStPo(") //
        .append(parentVar).append(",") //
        .append(env.stateVar) //
        .append(",").append(obj.rxObj).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("}").append(");").newline();
    obj.finish();
    env.pool.give(childStateVar);
    obj.finish();
  }

  public static void documentget(Environment env) {
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    ArrayList<String> parameters = new ArrayList<>();
    parameters.add("url");
    parameters.add("space");
    parameters.add("key");
    parameters.add("identity");
    parameters.add("redirect");
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("search:") || attr.getKey().startsWith("parameter:")) {
        parameters.add(attr.getKey());
      }
    }
    RxObject obj = new RxObject(env, parameters.toArray(new String[parameters.size()]));
    env.writer.tab().append("$.DG(") //
        .append(parentVar).append(",") //
        .append(env.stateVar) //
        .append(",").append(obj.rxObj).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("}").append(");").newline();
    obj.finish();
    env.pool.give(childStateVar);
    obj.finish();
  }

  public static void domainget(Environment env) {
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    ArrayList<String> parameters = new ArrayList<>();
    parameters.add("url");
    parameters.add("identity");
    parameters.add("redirect");
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("search:") || attr.getKey().startsWith("parameter:")) {
        parameters.add(attr.getKey());
      }
    }
    int delayLimit = 250;
    if (env.element.hasAttr("get:delay")) {
      String delayStr = env.element.attr("get:delay");
      try {
        delayLimit = Integer.parseInt(delayStr);
      } catch (NumberFormatException nfe1) {
        env.feedback.warn(env.element, "get:delay not parsed as integer '" + delayStr + "'");
        try {
          delayLimit = (int) Math.round(Double.parseDouble(delayStr));
        } catch (NumberFormatException nfe2) {
          env.feedback.warn(env.element, "get:delay not parsed as double! '" + delayStr + "'");
        }
      }
      env.element.removeAttr("get:delay");
    }

    RxObject obj = new RxObject(env, parameters.toArray(new String[parameters.size()]));
    env.writer.tab().append("$.DG(") //
        .append(parentVar).append(",") //
        .append(env.stateVar) //
        .append(",").append(obj.rxObj).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("},").append("" + delayLimit).append(");").newline();
    obj.finish();
    env.pool.give(childStateVar);
    obj.finish();
  }

  public static void pick(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    RxObject obj = new RxObject(env, "name");
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), TRUE_BRANCH);
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), FALSE_BRANCH);
    env.writer.tabDown().tab().append("}").append(env.element.hasAttr("keep-open") ? ",true" : ",false").append(");").newline();
    obj.finish();
    env.pool.give(childStateVar);
  }

  private static void __finish() {

  }

  public static void customdata(Environment env) {
    ArrayList<String> parameters = new ArrayList<>();
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("parameter:")) {
        parameters.add(attr.getKey());
      }
    }
    RxObject obj = new RxObject(env, parameters.toArray(new String[parameters.size()]));
    String sVar = env.pool.ask();
    String parentVar = soloParent(env);

    env.writer.tab().append("$.CUDA(") //
        .append(parentVar) //
        .append(",").append(env.stateVar) //
        .append(",'").append(env.element.attr("src")).append("',").append(obj.rxObj) //
        .append(",'").append(env.val("redirect", "/sign-in")) //
        .append("',function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }

  public static void textarea(Environment env) {
    input(env);
  }

  public static void input(Environment env) {
    String inputVar = Base.write(env, true);
    for (Attribute attr : env.element.attributes()) {
      if (attr.hasDeclaredValue() && (attr.getKey().equals("rx:sync") || attr.getKey().startsWith("rx:sync:"))) {
        String path = attr.getValue();
        double ms = _rxdebounce(env);
        StatePath _path = StatePath.resolve(("view:" + path), env.stateVar);
        env.writer.tab().append("$.SY(").append(inputVar).append(",").append(_path.command).append(",'").append(_path.name).append("',").append("" + ms).append(");").newline();
      }
    }
    env.pool.give(inputVar);
  }

  private static double _rxdebounce(Environment env) {
    if (env.element.hasAttr("rx:debounce")) {
      try {
        return Double.parseDouble(env.element.attr("rx:debounce"));
      } catch (IllegalArgumentException nfe) {
        env.feedback.warn(env.element, env.element.attr("rx:debounce") + " should be a numeric value");
        return 50;
      }
    }
    return 100;
  }

  public static void signout(Environment env) {
    env.writer.tab().append("$.SIGNOUT();").newline();
  }

  public static void select(Environment env) {
    input(env);
  }

  public static void inlinetemplate(Environment env) {
    ObjectNode config = Base.extractConfig(env.element);
    String eVar = env.parentVariable;
    String name = env.element.attr("name");
    String parentVar = env.pool.ask();
    String childStateVar = env.pool.ask();
    String caseVar = env.pool.ask();
    env.writer.tab().append("$.UT(").append(eVar).append(",").append(env.stateVar).append(",'").append(name).append("', function(").append(parentVar).append(",").append(childStateVar).append(",").append(caseVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).caseVar(caseVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("},").append(config.toString()).append(");").newline();
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
    env.pool.give(caseVar);
  }
}

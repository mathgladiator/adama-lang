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

import org.adamalang.rxhtml.acl.commands.BulkCommand;
import org.adamalang.rxhtml.acl.commands.Command;
import org.adamalang.rxhtml.acl.commands.Set;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.function.Function;

public class Attributes {

  public final Environment env;
  public final String eVar;
  public final boolean expand;

  public Attributes(Environment env, String eVar) {
    this.env = env;
    this.eVar = eVar;
    this.expand = env.element.hasAttr("rx:expand-view-state");
  }

  public void _if() {
    commonBetweenIfAndIfNot("rx:if");
  }

  public void commonBetweenIfAndIfNot(String version) {
    String value = env.element.attr(version);
    String childStateVar = env.pool.ask();
    String parentVar = env.pool.ask();
    boolean hide = false;
    if (env.element.hasAttr("force-hiding")) {
      hide = true;
      env.element.removeAttr("force-hiding");
    }
    if (value.startsWith("decide:") || value.startsWith("choose:") || value.startsWith("chosen:")) {
      String channel = value.substring(7);
      String key = env.element.hasAttr("key") ? env.element.attr("key") : "id";
      StatePath pathName = StatePath.resolve(env.element.hasAttr("name") ? env.element.attr(version) : "id", env.stateVar);
      boolean de = !(value.startsWith("chosen:"));
      env.writer.tab().append(de ? "$.DE(" : "$.CSEN(").append(eVar).append(",").append(env.stateVar).append(",").append(pathName.command);
      env.writer.append(",'").append(channel).append("','").append(key).append("'");
      env.writer.append(",'").append(pathName.name).append("',").append(version.equals("rx:if") ? "true" : "false").append(",").append(expand ? "true" : "false").append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    } else if (value.startsWith("finalize:")) {
      String channel = value.substring(9);
      env.writer.tab().append("$.FIN(").append(eVar).append(",").append(env.stateVar).append(",'").append(channel).append("',");
      env.writer.append(version.equals("rx:if") ? "true" : "false").append(",").append(expand ? "true" : "false").append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    } else {
      int kEq = value.indexOf('=');
      if (kEq > 0) {
        String left = value.substring(0, kEq);
        String right = value.substring(kEq + 1);
        StatePath pathLeft = StatePath.resolve(left, env.stateVar);
        StatePath pathRight = StatePath.resolve(right, env.stateVar);
        // TODO, we should really re-use the attribute language here to some degree
        env.writer.tab().append("$.IFeq(").append(eVar).append(",").append(env.stateVar).append(",");
        env.writer.append(pathLeft.command).append(",'").append(pathLeft.name).append("',");
        env.writer.append(pathRight.command).append(",'").append(pathRight.name).append("',");
        env.writer.append(version.equals("rx:if") ? "true" : "false").append(",").append(expand ? "true" : "false").append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
      } else {
        StatePath path = StatePath.resolve(value, env.stateVar);
        env.writer.tab().append("$.IF(").append(eVar).append(",").append(env.stateVar).append(",").append(path.command);
        env.writer.append(",'").append(path.name).append("',").append(version.equals("rx:if") ? "true" : "false").append(",").append(expand ? "true" : "false").append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
      }
    }
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return !node.hasAttr("rx:else");
      } else {
        return true;
      }
    });
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return node.hasAttr("rx:else");
      } else {
        return false;
      }
    });
    env.writer.tabDown().tab().append("}," + (hide ? "true" : "false") + ");").newline();
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
  }

  public void _ifnot() {
    commonBetweenIfAndIfNot("rx:ifnot");
  }

  public void _monitor() {
    StatePath path = StatePath.resolve(env.element.attr("rx:monitor"), env.stateVar);
    env.writer.tab().append("$.MN(").append(eVar).append(",").append(path.command).append(",'").append(path.name).append("');").newline();
    env.element.removeAttr("rx:monitor");
  }

  public void _repeat() {
    StatePath path = StatePath.resolve(env.element.attr("rx:repeat"), env.stateVar);
    String childStateVar = env.pool.ask();
    env.writer.tab().append("$.RP(").append(eVar).append(",").append(path.command).append(",'").append(path.name).append("',").append(expand ? "true" : "false").append(",function(").append(childStateVar).append(") {").tabUp().newline();
    Element soloChild = env.soloChildIfPossible();
    if (soloChild == null) {
      soloChild = new Element("div");
      soloChild.appendChildren(env.element.children());
    }
    String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(soloChild, true), true);
    env.writer.tab().append("return ").append(childDomVar).append(";").newline();
    env.pool.give(childDomVar);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }

  public void _iterate() {
    StatePath path = StatePath.resolve(env.element.attr("rx:iterate"), env.stateVar);
    String childStateVar = env.pool.ask();
    env.writer.tab().append("$.IT(").append(eVar).append(",").append(path.command).append(",'").append(path.name).append("',").append(expand ? "true" : "false").append(",function(").append(childStateVar).append(") {").tabUp().newline();
    Element soloChild = env.soloChildIfPossible();
    if (soloChild == null) {
      soloChild = new Element("div");
      soloChild.appendChildren(env.element.children());
    }
    String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(soloChild, true), true);
    env.writer.tab().append("return ").append(childDomVar).append(";").newline();
    env.pool.give(childDomVar);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }

  public void _switch() {
    StatePath path = StatePath.resolve(env.element.attr("rx:switch"), env.stateVar);
    String childStateVar = env.pool.ask();
    String caseVar = env.pool.ask();
    String parentVar = env.pool.ask();
    env.writer.tab().append("$.SW(").append(eVar).append(",").append(path.command);
    env.writer.append(",'").append(path.name).append("',function(").append(parentVar).append(",").append(childStateVar).append(",").append(caseVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).caseVar(caseVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(caseVar);
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
  }

  public void _wrap() {
    String childStateVar = env.pool.ask();
    String parentVar = env.pool.ask();
    String caseVar = env.pool.ask();
    RxObject obj = new RxObject(env, RxObject.pullParameters(env.element));
    env.writer.tab().append("$.WP(").append(eVar).append(",").append(env.stateVar).append(",'").append(env.element.attr("rx:wrap")).append("',").append(obj.rxObj);
    env.writer.append(",function(").append(parentVar).append(",").append(childStateVar).append(",").append(caseVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).caseVar(caseVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(caseVar);
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
    obj.finish();
  }

  public void _template() {
    String name = env.element.attr("rx:template");
    String parentVar = env.pool.ask();
    String childStateVar = env.pool.ask();
    String caseVar = env.pool.ask();
    env.writer.tab().append("$.UT(").append(eVar).append(",").append(env.stateVar).append(",'").append(name).append("', function(").append(parentVar).append(",").append(childStateVar).append(",").append(caseVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).caseVar(caseVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
    env.pool.give(caseVar);
  }

  public void _base() {
    TreeSet<String> features = new TreeSet<>();
    if (env.element.tagName().equalsIgnoreCase("A")) {
      if (env.element.hasAttr("merge")) {
        features.add("merge");
        env.element.removeAttr("merge");
      }
    }
    for (Attribute attr : env.element.attributes().asList()) {
      if (attr.getKey().equals("xmlns") || attr.getKey().startsWith("rx:") || "ln:ch".equals(attr.getKey())) {
        continue;
      }
      if (attr.hasDeclaredValue()) {
        try {
          Tree tree = Parser.parse(attr.getValue());
          Map<String, String> vars = tree.variables();
          if (vars.size() > 0 || tree.hasAuto()) {
            var oVar = env.pool.ask();
            var computeFoo = env.pool.ask();
            env.writer.tab().append("{").tabUp().newline();
            env.writer.tab().append("var ").append(oVar).append("={};").newline();
            env.writer.tab().append(oVar).append(".__dom=").append(eVar).append(";").newline();
            if (tree.hasAuto()) {
              env.writer.tab().append(oVar).append(".__x=").append(env.autoVar).append(";").newline();
            }
            env.writer.tab().append("var ").append(computeFoo).append("=(function() {").tabUp().newline();
            writeDomSetter(env.stateVar,"this.__dom", attr.getKey(), tree.js(env.attributeContext(attr.getKey()), "this"), features);
            env.writer.tabDown().tab().append("}).bind(").append(oVar).append(");").newline();
            for (Map.Entry<String, String> ve : vars.entrySet()) {
              StatePath path = StatePath.resolve(ve.getValue(), env.stateVar);
              env.writer.tab().append("$.Y(").append(path.command).append(",").append(oVar).append(",'").append(path.name).append("',").append(computeFoo).append(");").newline();
            }
            env.pool.give(oVar);
            env.pool.give(computeFoo);
            env.writer.tab().append(computeFoo).append("();").newline();
            env.writer.tabDown().tab().append("}").newline();
          } else {
            writeDomSetter(env.stateVar, eVar, attr.getKey(), tree.js(env.contextOf(attr.getKey()), "this"), features);
          }
        } catch (ParseException pe) {
          env.feedback.warn(env.element, "has parser problems, attr=" + attr.getKey() + "; problem=" + pe.getMessage());
        }
      } else {
        writeDomSetter(env.stateVar, eVar, attr.getKey(), "true", features);
      }
    }
  }

  private boolean isBooleanInputValue(String value) {
    switch (value.trim().toLowerCase(Locale.ENGLISH)) {
      case "disabled":
      case "hidden":
      case "required":
      case "readonly":
      case "checked":
        return true;
    }
    return false;
  }

  private String wrapBoolValue(String expr) {
    if ("\"true\"".equals(expr) || "true".equals(expr)) {
      return "true";
    }
    if ("\"false\"".equals(expr) || "false".equals(expr)) {
      return "false";
    }
    return "$.B(" + expr + ")";
  }

  private void writeDomSetter(String stateVar, String var, String key, String expr, TreeSet<String> features) {
    String tagName = env.element.tagName();
    boolean isSelect = tagName.equalsIgnoreCase("select");
    boolean isOption = tagName.equalsIgnoreCase("option");
    boolean hasValue = tagName.equalsIgnoreCase("textarea") ||
                       tagName.equalsIgnoreCase("input") ||
                       isSelect ||
                       isOption;
    if (key.equalsIgnoreCase("href")) {
      env.writer.tab().append("$.HREF(").append(var).append(",").append(stateVar).append(",").append(expr).append(",").append(features.contains("merge") ? "true" : "false").append(");").newline();
    } else if (key.equalsIgnoreCase("class")) {
      env.writer.tab().append("$.ACLASS(").append(var).append(",").append(expr).append(");").newline();
    } else if (key.equalsIgnoreCase("src")) {
      env.writer.tab().append("$.ASRC(").append(var).append(",").append(expr).append(");").newline();
    } else if (hasValue && key.equalsIgnoreCase("value")) {
      env.writer.tab().append("$.SV(").append(var).append(",").append(expr).append(");").newline();
    } else if (hasValue && isBooleanInputValue(key)) {
      env.writer.tab().append("$.FV(").append(var).append(",'").append(key).append("',").append(wrapBoolValue(expr)).append(");").newline();
    } else {
      if (isOption && "label".equalsIgnoreCase(key)) {
        env.writer.tab().append("$.SL(").append(var).append(",").append(expr).append(");").newline();
      } else {
        env.writer.tab().append("$.SA(").append(var).append(",'").append(key).append("',").append(expr).append(");").newline();
      }
    }
  }

  public void _event(String event) {
    if (!env.element.hasAttr("rx:" + event)) {
      return;
    }
    try {
      ArrayList<Command> commands = org.adamalang.rxhtml.acl.Parser.parse(env.element.attr("rx:" + event));
      env.element.removeAttr("rx:" + event);
      String arrVar = null;

      for (Command command : commands) {
        if (command instanceof BulkCommand) {
          if (env.optimizeForPageLoad && "load".equals(event) && command instanceof Set && ((Set) command).constant) {
            env.writer.tab().append(env.stateVar).append(".view.init['").append(((Set) command).name).append("']=").append(Escapes.constantOf(((Set) command).value)).append(";").newline();
          } else {
            if (arrVar == null) {
              arrVar = env.pool.ask();
              env.writer.tab().append("var ").append(arrVar).append("=[];").newline();
            }
            ((BulkCommand) command).writeBulk(env, eVar, arrVar);
          }
        } else {
          command.write(env, event, eVar);
        }
      }
      if (arrVar != null) {
        env.writer.tab().append("$.onB(").append(eVar).append(",'").append(event).append("',").append(env.stateVar).append(",").append(arrVar).append(");").newline();
      }
    } catch (ParseException ex) {
      env.feedback.warn(env.element, "event '" + event + "' has parser problems:" + ex.getMessage());
    }
  }

  public void _delay(String event) {
    try {
      ArrayList<Command> commands = org.adamalang.rxhtml.acl.Parser.parse(env.element.attr("rx:" + event));
      int kColon = event.indexOf(':');
      if (kColon < 4) {
        env.feedback.warn(env.element, "delay '" + event + "' is not a well formed delay");
      } else {
        String delayTime = event.substring(kColon + 1);
        try {
          Integer.parseInt(delayTime);
        } catch (NumberFormatException nfe) {
          env.feedback.warn(env.element, "delay '" + event + "' is not a well formed delay (failed to parse " + delayTime + " as int)");
        }
      }
      env.element.removeAttr("rx:" + event);
      env.pool.ask();
      for (Command command : commands) {
        command.write(env, event, eVar);
      }
    } catch (ParseException ex) {
      env.feedback.warn(env.element, "event '" + event + "' has parser problems:" + ex.getMessage());
    }
  }

  private void walkAndValidateAndWitness(Element element, Function<Element, Boolean> check, HashSet<String> seen) {
    if (element.tagName().equalsIgnoreCase("input")) {
      if (check.apply(element)) {
        seen.add(element.attr("name"));
      }
    } else {
      for (Element child : element.children()) {
        walkAndValidateAndWitness(child, check, seen);
      }
    }
  }

  private boolean walkAndValidateAndCheck(Environment env, Function<Element, Boolean> test, String... checks) {
    HashSet<String> seen = new HashSet<>();
    walkAndValidateAndWitness(env.element, test, seen);
    boolean result = true;
    for (String check : checks) {
      if (!seen.contains(check)) {
        env.feedback.warn(env.element, "Failed to find an input for '" + check + "'");
        result = false;
      }
    }
    return result;
  }

  @Deprecated
  private void check_action_sign_in() {
    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      String type = el.attr("type");
      if ("password".equals(name)) {
        if (!("password".equals(type) || "hidden".equals(type))) {
          env.feedback.warn(el, "Passwords should have type 'password' or 'hidden'.");
        }
        return true;
      }
      if ("email".equals(name)) {
        if (!("email".equals(type))) {
          env.feedback.warn(el, "Emails should have type 'email'.");
        }
        return true;
      }
      if ("remember".equals(name)) {
        return true;
      }
      if ("submit".equals(type)) {
        return true;
      }
      env.feedback.warn(el, "The input '" + name + "' is excessive.");
      return false;
    }, "email", "password");
  }

  private void check_authorization(boolean domain) {
    String[] checks = domain ? new String[]{"password"} : new String[]{"password", "space", "key"};

    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      String type = el.attr("type");
      if ("password".equals(name)) {
        return true;
      }
      if ("password".equals(type) && name.equals("confirm-password")) {
        return true;
      }
      if ("password".equals(type)) {
        env.feedback.warn(el, "The input '" + name + "' has a type of password which is not allowed beyond.");
        return true;
      }
      return false;
    }, checks);
  }

  @Deprecated
  private void check_action_document_sign_in(boolean domain) {
    String[] checks = domain ? new String[]{"username", "password"} : new String[]{"username", "password", "space", "key"};
    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      String type = el.attr("type");
      if ("password".equals(name)) {
        if (!("password".equals(type) || "hidden".equals(type))) {
          env.feedback.warn(el, "Passwords should have type 'password' or 'hidden'.");
        }
        return true;
      }
      if ("username".equals(name)) {
        return true;
      }
      if (!domain) {
        if ("space".equals(name)) {
          return true;
        }
        if ("key".equals(name)) {
          return true;
        }
      }
      if ("remember".equals(name)) {
        return true;
      }
      if ("submit".equals(type)) {
        return true;
      }
      env.feedback.warn(el, "The input '" + name + "' is excessive.");
      return false;
    }, checks);
  }

  @Deprecated
  private void check_action_document_sign_in_reset(boolean domain) {
    String[] checks = domain ? new String[]{"username", "password", "new_password"} : new String[]{"username", "password", "new_password", "space", "key"};
    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      String type = el.attr("type");
      if ("password".equals(name)) {
        if (!("password".equals(type) || "hidden".equals(type))) {
          env.feedback.warn(el, "Passwords should have type 'password' or 'hidden'.");
        }

        return true;
      }
      if ("new_password".equals(name)) {
        if (!("password".equals(type))) {
          env.feedback.warn(el, "New password should have type 'password'.");
        }
        return true;
      }
      if ("username".equals(name)) {
        return true;
      }
      if (!domain) {
        if ("space".equals(name)) {
          return true;
        }
        if ("key".equals(name)) {
          return true;
        }
      }
      if ("remember".equals(name)) {
        return true;
      }
      if ("submit".equals(type)) {
        return true;
      }
      env.feedback.warn(el, "The input '" + name + "' is excessive.");
      return false;
    }, checks);
  }

  private void check_action_upload(boolean domain) {
    String[] checks = domain ? new String[]{"files"} : new String[]{"space", "key", "files"};
    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      String type = el.attr("type");
      if ("space".equals(name)) {
        return true;
      }
      if (!domain) {
        if ("key".equals(name)) {
          return true;
        }
      }
      if ("submit".equals(type)) {
        return true;
      }
      if ("files".equals(name)) {
        return true;
      }
      if ("channel".equals(name)) {
        return true;
      }
      return false;
    }, checks);
  }

  private void check_action_sign_up() {
    walkAndValidateAndCheck(env, (el) -> {
      if ("email".equals(el.attr("name"))) {
        if (!("email".equals(el.attr("type")))) {
          env.feedback.warn(el, "Emails should have type 'email'");
        }
        return true;
      }
      return false;
    }, "email");
  }

  private void check_set_password() {
    walkAndValidateAndCheck(env, (el) -> {
      String name = el.attr("name");
      if ("password".equals(name)) {
        if (!("password".equals(el.attr("type")))) {
          env.feedback.warn(el, "Passwords should have type 'password'.");
        }
        return true;
      }
      if ("email".equals(name)) {
        if (!("email".equals(el.attr("type")))) {
          env.feedback.warn(el, "Emails should have type 'email'");
        }
        return true;
      }
      return "code".equals(name);
    }, "password", "code");
  }

  private void convertFailureVariableToEvents(Element element, String defaultVar) {
    String failureVariable = defaultVar;
    if (element.hasAttr("rx:failure-variable")) {
      failureVariable = element.attr("rx:failure-variable");
    }
    if (!element.hasAttr("rx:failure")) {
      element.attr("rx:failure", "set:" + failureVariable + "=true");
    }
    if (!element.hasAttr("rx:success")) {
      element.attr("rx:success", "set:" + failureVariable + "=false");
    }
  }

  public void _action() {
    String action = env.element.attr("rx:action").trim();
    boolean isDocAuthorization = "document:authorize".equalsIgnoreCase(action);
    boolean isDomainAuthorization = "domain:authorize".equalsIgnoreCase(action);

    boolean isSignIn = "document:sign-in".equalsIgnoreCase(action);
    boolean isDomainSignIn = "domain:sign-in".equalsIgnoreCase(action);
    boolean isSignInAndReset = "document:sign-in-reset".equalsIgnoreCase(action);
    boolean isDomainSignInAndReset = "domain:sign-in-reset".equalsIgnoreCase(action);
    boolean documentPut = "document:put".equalsIgnoreCase(action);
    boolean domainDocumentPut = "domain:put".equalsIgnoreCase(action);

    if (isDocAuthorization || isDomainAuthorization) {
      check_authorization(isDomainAuthorization);
      if (!env.element.hasAttr("rx:forward")) {
        env.element.attr("rx:forward", "/");
      }
      RxObject obj = new RxObject(env, "rx:forward");
      env.writer.tab().append(isDomainAuthorization ? "$.aDOM(" : "$.aDOC(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if (isSignInAndReset || isDomainSignInAndReset) {
      check_action_document_sign_in_reset(isDomainSignInAndReset);
      if (!env.element.hasAttr("rx:forward")) {
        env.element.attr("rx:forward", "/");
      }
      convertFailureVariableToEvents(env.element, "sign_in_failed");
      RxObject obj = new RxObject(env, "rx:forward");
      env.writer.tab().append(isDomainSignInAndReset ? "$.adDSOr(" : "$.aDSOr(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if (isSignIn || isDomainSignIn) { // sign in as an Adama user
      check_action_document_sign_in(isDomainSignIn);
      if (!env.element.hasAttr("rx:forward")) {
        env.element.attr("rx:forward", "/");
      }
      convertFailureVariableToEvents(env.element, "sign_in_failed");
      RxObject obj = new RxObject(env, "rx:forward");
      env.writer.tab().append(isDomainSignIn ? "$.adDSO(" : "$.aDSO(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if (documentPut || domainDocumentPut) { // sign in as an Adama user
      if (!env.element.hasAttr("rx:forward")) {
        env.element.attr("rx:forward", "/");
      }
      RxObject obj = domainDocumentPut ? new RxObject(env, "path", "rx:forward") : new RxObject(env, "space", "key", "path", "rx:forward");
      env.writer.tab().append(domainDocumentPut ? "$.adDPUT(" : "$.aDPUT(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if ("adama:sign-in".equalsIgnoreCase(action)) { // sign in as an Adama user
      if (!env.element.hasAttr("rx:forward")) {
        env.element.attr("rx:forward", "/");
      }
      convertFailureVariableToEvents(env.element, "sign_in_failed");
      RxObject obj = new RxObject(env, "rx:forward");
      check_action_sign_in();
      env.writer.tab().append("$.aSO(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if (action.startsWith("custom:")) { // execute custom logic
      convertFailureVariableToEvents(env.element, "sign_in_failed");
      String customCommandName = action.substring(7).trim();
      env.writer.tab().append("$.aCC(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(customCommandName) //
          .append("');").newline();
    } else if ("adama:upload-asset".equalsIgnoreCase(action) || "document:upload-asset".equalsIgnoreCase(action)) { // upload an asset
      convertFailureVariableToEvents(env.element, "asset_upload_failed");
      check_action_upload(false);
      RxObject obj = new RxObject(env, "rx:forward");
      env.writer.tab().append("$.aUP(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if ("domain:upload-asset".equalsIgnoreCase(action)) { // upload an asset
      convertFailureVariableToEvents(env.element, "asset_upload_failed");
      check_action_upload(true);
      RxObject obj = new RxObject(env, "rx:forward");
      env.writer.tab().append("$.aDUP(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:identity", "default")) //
          .append("',").append(obj.rxObj) //
          .append(");").newline();
    } else if ("adama:sign-up".equalsIgnoreCase(action)) { // sign up as an Adama user
      convertFailureVariableToEvents(env.element, "sign_up_failed");
      check_action_sign_up();
      env.writer.tab().append("$.aSU(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:forward", "/")) //
          .append("');").newline();
    } else if ("adama:set-password".equalsIgnoreCase(action)) { // set the password for the connection
      convertFailureVariableToEvents(env.element, "set_password_failed");
      check_set_password();
      env.writer.tab().append("$.aSP(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(env.val("rx:forward", "/")) //
          .append("'").append(");").newline();
    } else if (action.startsWith("send:")) { // Send a message on the given channel
      convertFailureVariableToEvents(env.element, "send_failed");
      String channel = action.substring(5);
      env.writer.tab().append("$.aSD(").append(eVar) //
          .append(",").append(env.stateVar) //
          .append(",'").append(channel) //
          .append("');").newline();
    } else if (action.startsWith("copy-form:")) {
      String targetFormId = action.substring(10);
      env.writer.tab().append("$.aCF(").append(eVar) //
          .append(",'").append(targetFormId) //
          .append("');").newline();
    } else if (action.startsWith("copy:")) { // Copy the form object into the view
      String path = action.substring(5);
      boolean tuned = path.startsWith("view:") | path.startsWith("data:");
      StatePath _path = StatePath.resolve(tuned ? path : ("view:" + path), env.stateVar);
      env.writer.tab().append("$.aCP(").append(eVar).append(",").append(_path.command).append(",'").append(_path.name).append("');").newline();
    }
  }
}

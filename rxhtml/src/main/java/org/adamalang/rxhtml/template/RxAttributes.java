/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.template;

public class RxAttributes {

  public final Environment env;
  public final String eVar;
  public final boolean expand;

  public RxAttributes(Environment env, String eVar) {
    this.env = env;
    this.eVar = eVar;
    this.expand = env.element.hasAttr("rx:expand-view-state");
  }

  public void commonBetweenIfAndIfNot(String version) {
    String value = env.element.attr(version);
    if (value.startsWith("decide:")) {
      // We do something completely different, but... not really
    } else {
      StatePath path = StatePath.resolve(env.element.attr(version), env.stateVar);
      String childStateVar = env.pool.ask();
      String parentVar = env.pool.ask();
      env.writer.tab().append("$.IF(").append(eVar).append(",").append(path.command);
      env.writer.append(",'").append(path.name).append("', ").append(version.equals("rx:if") ? "true" : "false").append(",").append(expand ? "true" : "false").append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
      Base.children(env.stateVar(childStateVar).parentVariable(parentVar));
      env.writer.tabDown().tab().append("});").newline();
      env.pool.give(childStateVar);
      env.pool.give(parentVar);
    }
  }

  public void _if() {
    commonBetweenIfAndIfNot("rx:if");
  }

  public void _ifnot() {
    commonBetweenIfAndIfNot("rx:ifnot");
  }

  public void _iterate() {
    StatePath path = StatePath.resolve(env.element.attr("rx:iterate"), env.stateVar);
    String childStateVar = env.pool.ask();
    env.writer.tab().append("$.IT(").append(eVar).append(",").append(path.command).append(",'").append(path.name).append("',").append(expand ? "true" : "false").append(",function(").append(childStateVar).append(") {").tabUp().newline();
    String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(env.soloChild()), true);
    env.writer.tab().append("return ").append(childDomVar).append(";").newline();
    env.pool.give(childDomVar);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }

  public void _scope() {
    StatePath path = StatePath.resolve(env.element.attr("rx:scope"), env.stateVar);
    String newStateVar = env.pool.ask();
    env.writer.tab().append("var ").append(newStateVar).append(" = $.pI(").append(path.command).append(",'").append(path.name).append("');").newline();
    Base.children(env.stateVar(newStateVar));
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

  public void _template() {
    String name = env.element.attr("rx:template");
    String parentVar = env.pool.ask();
    String childStateVar = env.pool.ask();
    env.writer.tab().append("$.UT(").append(eVar).append(",").append(env.stateVar).append(",'").append(name).append("', function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
    env.pool.give(parentVar);
  }
}

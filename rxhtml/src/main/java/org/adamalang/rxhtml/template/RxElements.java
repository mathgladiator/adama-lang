package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.template.elements.Decide;
import org.adamalang.rxhtml.template.elements.Execute;
import org.adamalang.rxhtml.template.elements.Input;
import org.adamalang.rxhtml.template.elements.Message;

public class RxElements {
  public static void template(Environment env) { /* no-op */ }
  public static void page(Environment env) { /* no-op */ }
  public static void fragment(Environment env) {
    env.writer.tab().append(env.fragmentFunc).append("(").append(env.parentVariable).append(",").append(env.stateVar).append(");").newline();
  }
  public static void input(Environment env) {
    String inputVar = Base.write(env, true);
  }
  public static void button(Environment env) {
    String inputVar = Base.write(env, true);
  }
  public static void lookup(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    // TODO: assert static path
    String transform = env.element.attr("transform");
    if (transform == null || "".equals(transform)) {
      env.writer.tab().append(env.parentVariable).append(".append($.L(").append(path.command).append(", '").append(path.name).append("'));").newline();
    } else {
      // TODO sort out transforms
    }
  }
  public static void connection(Environment env) {
    RxAttributeObject obj = new RxAttributeObject(env, "name", "space", "key", "identity");
    env.writer.tab().append("$.CONNECT(").append(env.stateVar).append(",").append(obj.rxObj).append(");").newline();
    obj.finish();
    if (env.element.childNodeSize() > 0) {
      RxElements.pick(env);
    }
  }
  public static void decide(Environment env) {
    env.assertHasParent();
    env.parent.assertSoloParent();
    String channel = env.element.attr("channel");
    String key = env.element.attr("key");
    String path = env.element.attr("path");
    if (key.equals("")) {
      key = "id";
    }
    if (path.equals("")) {
      path = "id";
    }
    String stateVarToUse = env.stateVar;
    // TODO: parse the path to get a state variable
    String childStateVar = env.pool.ask();
    env.writer.tab().append("$.DE(").append(env.parentVariable).append(", ").append(stateVarToUse);
    env.writer.append(", '").append(channel).append("'");
    env.writer.append(", '").append(key).append("'");
    env.writer.append(", '").append(path).append("', function(").append(childStateVar).append(") {").tabUp().newline();
    String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(env.soloChild()), true);
    env.writer.tab().append("return ").append(childDomVar).append(";").newline();
    env.pool.give(childDomVar);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }
  public static void pick(Environment env) {
    RxAttributeObject obj = new RxAttributeObject(env, "name");
    String sVar = env.pool.ask();
    final String parentVar;
    if (env.singleParent) {
      parentVar = env.parentVariable;
    } else {
      parentVar = env.pool.ask();
      env.writer.tab().append("var ").append(parentVar).append("=$.E('div');").newline();
      env.writer.tab().append(env.parentVariable).append(".append(").append(parentVar).append(");").newline();
    }
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }
}

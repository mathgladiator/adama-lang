package org.adamalang.rxhtml.template.elements;

import org.adamalang.rxhtml.template.Base;
import org.adamalang.rxhtml.template.Environment;

public class Decide {
  public static void write(Environment env) {
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
    env.writer.tab().append("$.D(").append(env.parentVariable).append(", ").append(stateVarToUse);
    env.writer.append(", '").append(channel).append("'");
    env.writer.append(", '").append(key).append("'");
    env.writer.append(", '").append(path).append("', function(").append(childStateVar).append(") {").tabUp().newline();
    String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(env.soloChild()), true);
    env.writer.tab().append("return ").append(childDomVar).append(";").newline();
    env.pool.give(childDomVar);
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
  }
}

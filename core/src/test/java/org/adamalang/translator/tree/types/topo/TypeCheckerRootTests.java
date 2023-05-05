package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.junit.Test;

import java.util.Collections;

public class TypeCheckerRootTests {
  @Test
  public void sanity() {
    TypeCheckerRoot tcr = new TypeCheckerRoot();
    tcr.issueError(DocumentPosition.ZERO, "message", "Here");
    tcr.define(Token.WRAP("x"), Collections.emptySet(), (env) -> {});
    tcr.register(Collections.singleton("x"), (env) -> {});
    tcr.alias("::x", "x");
    EnvironmentState es = new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make());
    tcr.check(Environment.fresh(new Document(), es));
  }
}

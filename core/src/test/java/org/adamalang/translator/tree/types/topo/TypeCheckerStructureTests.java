/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.junit.Test;

import java.util.Collections;

public class TypeCheckerStructureTests {
  @Test
  public void sanity() {
    TypeCheckerStructure tcs = new TypeCheckerStructure();
    tcs.issueError(DocumentPosition.ZERO, "message", "Here");
    tcs.define(Token.WRAP("x"), Collections.emptySet(), (env) -> {});
    tcs.register(Collections.singleton("x"), (env) -> {});
    TypeCheckerRoot root = new TypeCheckerRoot();
    tcs.transferInto("R", StorageSpecialization.Record, root);
    EnvironmentState es = new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make());
    root.check(Environment.fresh(new Document(), es));
  }
}

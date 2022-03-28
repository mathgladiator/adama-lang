/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CodeGenFunctionsTests {
  @Test
  public void multiargway1() {
    final var sb = new StringBuilder();
    final var env =
        Environment.fresh(
            new Document(),
            new EnvironmentState(
                GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), false, false);
    CodeGenFunctions.writeArgsJava(sb, env, false, new ArrayList<>(), foi);
    Assert.assertEquals("", sb.toString());
  }

  @Test
  public void multiargway2() {
    final var sb = new StringBuilder();
    final var env =
        Environment.fresh(
            new Document(),
            new EnvironmentState(
                GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), false, false);
    foi.hiddenSuffixArgs.add("X");
    CodeGenFunctions.writeArgsJava(sb, env, false, new ArrayList<>(), foi);
    Assert.assertEquals(", X", sb.toString());
  }

  @Test
  public void multiargway3() {
    final var sb = new StringBuilder();
    final var env =
        Environment.fresh(
            new Document(),
            new EnvironmentState(
                GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), false, false);
    foi.hiddenSuffixArgs.add("X");
    CodeGenFunctions.writeArgsJava(sb, env, true, new ArrayList<>(), foi);
    Assert.assertEquals("X", sb.toString());
  }
}

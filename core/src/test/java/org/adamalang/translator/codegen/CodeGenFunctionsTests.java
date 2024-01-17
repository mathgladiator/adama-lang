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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.*;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
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
                GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), FunctionPaint.NORMAL);
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
                GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), FunctionPaint.NORMAL);
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
                GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling), CompilerOptions.start().make()));
    final var foi =
        new FunctionOverloadInstance("foo", new TyNativeVoid(), new ArrayList<>(), FunctionPaint.NORMAL);
    foi.hiddenSuffixArgs.add("X");
    CodeGenFunctions.writeArgsJava(sb, env, true, new ArrayList<>(), foi);
    Assert.assertEquals("X", sb.toString());
  }
}

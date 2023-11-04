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
package org.adamalang.translator.tree.privacy;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.topo.TypeChecker;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;

import java.util.function.Consumer;

/** used within a record to define a custom policy */
public class DefineCustomPolicy extends DocumentPosition {
  public final Block code;
  public final Token definePolicy;
  public final Token name;
  public final TyNativeBoolean policyType;

  public DefineCustomPolicy(final Token definePolicy, final Token name, final Block code) {
    this.definePolicy = definePolicy;
    this.name = name;
    this.code = code;
    policyType = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, name);
    ingest(definePolicy);
    ingest(code);
    policyType.ingest(name);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(definePolicy);
    yielder.accept(name);
    code.emit(yielder);
  }

  public void format(Formatter formatter) {
    code.format(formatter);
  }

  public Environment scope(final Environment environment, DocumentPosition position) {
    Environment env = environment.scopeAsPolicy().scopeWithComputeContext(ComputeContext.Computation);
    TyType returnType = policyType;
    if (position != null) {
      returnType = policyType.makeCopyWithNewPosition(position, policyType.behavior);
    }
    env.setReturnType(returnType);
    return env;
  }

  public void typing(TypeChecker checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.define(Token.WRAP("policy:" + name), fe.free, (environment -> {
      final var flow = code.typing(scope(environment, null));
      if (flow == ControlFlow.Open) {
        environment.document.createError(this, String.format("Policy '%s' does not return in all cases", name.text));
      }
    }));
  }
}

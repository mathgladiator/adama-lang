/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.topo.TypeChecker;

import java.util.function.Consumer;

/** used within a record to define a custom policy */
public class DefineViewFilter extends DocumentPosition {
  public final Block code;
  public final Token defineFilter;
  public final Token name;
  public final TyNativeBoolean policyType;

  public DefineViewFilter(final Token definePolicy, final Token name, final Block code) {
    this.defineFilter = definePolicy;
    this.name = name;
    this.code = code;
    policyType = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, name);
    ingest(definePolicy);
    ingest(code);
    policyType.ingest(name);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(defineFilter);
    yielder.accept(name);
    code.emit(yielder);
  }

  public void format(Formatter formatter) {
    formatter.startLine(defineFilter);
    code.format(formatter);
  }

  public Environment scope(final Environment environment, DocumentPosition position) {
    Environment env = environment.scopeAsFilter().scopeWithComputeContext(ComputeContext.Computation);
    TyType returnType = policyType;
    if (position != null) {
      returnType = policyType.makeCopyWithNewPosition(position, policyType.behavior);
    }
    env.setReturnType(returnType);
    return env;
  }

  public void typing(Environment environment) {
    final var flow = code.typing(scope(environment, null));
    if (flow == ControlFlow.Open) {
      environment.document.createError(this, String.format("Filter '%s' does not return in all cases", name.text));
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.privacy;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;

import java.util.function.Consumer;

/** used within a record to define a custom policy */
public class DefineCustomPolicy extends DocumentPosition {
  public final TyNativePrincipal clientType;
  public final Token clientVar;
  public final Block code;
  public final Token definePolicy;
  public final Token endParen;
  public final Token name;
  public final Token openParen;
  public final TyNativeBoolean policyType;

  public DefineCustomPolicy(final Token definePolicy, final Token name, final Token openParen, final Token clientVar, final Token endParen, final Block code) {
    this.definePolicy = definePolicy;
    this.name = name;
    this.openParen = openParen;
    this.clientVar = clientVar;
    this.endParen = endParen;
    this.code = code;
    policyType = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, name);
    clientType = new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, clientVar != null ? clientVar : definePolicy);
    ingest(name);
    ingest(code);
    clientType.ingest(clientVar != null ? clientVar : definePolicy);
    policyType.ingest(name);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(definePolicy);
    yielder.accept(name);
    if (openParen != null) {
      yielder.accept(openParen);
      yielder.accept(clientVar);
      yielder.accept(endParen);
    }
    code.emit(yielder);
  }

  public Environment scope(final Environment environment, DocumentPosition position) {
    Environment env = environment.scopeAsPolicy().scopeWithComputeContext(ComputeContext.Computation);
    if (clientVar != null) {
      env.define(clientVar.text, clientType, true, clientType);
    }
    TyType returnType = policyType;
    if (position != null) {
      returnType = policyType.makeCopyWithNewPosition(position, policyType.behavior);
    }
    env.setReturnType(returnType);
    return env;
  }

  public void typeCheck(final Environment environment) {
    final var flow = code.typing(scope(environment, null));
    if (flow == ControlFlow.Open) {
      environment.document.createError(this, String.format("Policy '%s' does not return in all cases", name.text), "PolicyDefine");
    }
  }
}

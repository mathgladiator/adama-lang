/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.privacy;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
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

  public Environment scope(final Environment environment, DocumentPosition position) {
    Environment env = environment.scopeAsPolicy().scopeWithComputeContext(ComputeContext.Computation);
    TyType returnType = policyType;
    if (position != null) {
      returnType = policyType.makeCopyWithNewPosition(position, policyType.behavior);
    }
    env.setReturnType(returnType);
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (environment -> {
      final var flow = code.typing(scope(environment, null));
      if (flow == ControlFlow.Open) {
        environment.document.createError(this, String.format("Policy '%s' does not return in all cases", name.text), "PolicyDefine");
      }
    }));
  }
}

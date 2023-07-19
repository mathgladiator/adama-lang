/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyInternalReadonlyClass;

import java.util.function.Consumer;

/** a constant to leverage the context within a static policy */
public class ContextConstant extends Expression {
  public final Token token;

  public ContextConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (environment.state.isStatic() || environment.state.isConstructor() || environment.state.isDocumentEvent() || environment.state.isMessageHandler() || environment.state.isAuthorize()) {
      environment.mustBeComputeContext(this);
      TyType type = new TyInternalReadonlyClass(CoreRequestContext.class);
      environment.useSpecial(type, "__context");
      return type;
    } else {
      environment.document.createError(this, "@context is only available within static policies, constructors, document events, authorize handler, message handlers");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__context");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeSecurePrincipal;

import java.util.function.Consumer;

public class WhoClientConstant extends Expression {
  public final Token token;

  public WhoClientConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (environment.state.isStatic() || environment.state.isMessageHandler() || environment.state.isPolicy() || environment.state.isBubble() || environment.state.isWeb()) {
      environment.mustBeComputeContext(this);
      TyType type = new TyNativeSecurePrincipal(TypeBehavior.ReadOnlyNativeValue, null, token, token, token, token).withPosition(this);
      environment.useSpecial(type, "__who");
      return type;
    } else {
      environment.document.createError(this, "@who is only available from static policies, document policies, privacy policies, bubbles, web paths, and message handlers", "WHO");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__who");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}

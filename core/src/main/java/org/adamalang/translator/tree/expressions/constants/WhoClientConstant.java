/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;

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
      return new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this);
    } else {
      environment.document.createError(this, "@who is only available from static policies, document policies, privacy policies, bubbles, web paths, and message handlers", "WHO");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__who");
  }
}

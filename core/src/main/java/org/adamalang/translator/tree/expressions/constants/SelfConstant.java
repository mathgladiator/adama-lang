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

public class SelfConstant extends Expression {
  public final Token token;

  public SelfConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    TyType selfType = environment.getSelfType();
    if (selfType != null) {
      TyType resolvedType = environment.rules.Resolve(selfType, false);
      environment.useSpecial(resolvedType, "__this");
      return resolvedType;
    } else {
      environment.document.createError(this, "@self requires being inside a record");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__this");
  }

  @Override
  public void free(FreeEnvironment environment) {
    environment.require("__this");
  }
}

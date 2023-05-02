/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

public abstract class Expression extends DocumentPosition {
  protected TyType cachedType = null;

  public abstract void emit(Consumer<Token> yielder);

  public TyType getCachedType() {
    return cachedType;
  }

  public boolean passedTypeChecking() {
    return cachedType != null;
  }

  public TyType typing(final Environment environment, final TyType suggestion) {
    if (cachedType == null) {
      cachedType = typingInternal(environment, suggestion);
    }
    return cachedType;
  }

  protected abstract TyType typingInternal(Environment environment, TyType suggestion);

  public Expression withPosition(final DocumentPosition position) {
    ingest(position);
    return this;
  }

  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    final var child = new StringBuilder();
    writeJava(child, environment);
    sb.append(child.toString());
  }

  public abstract void free(FreeEnvironment environment);

  public abstract void writeJava(StringBuilder sb, Environment environment);
}

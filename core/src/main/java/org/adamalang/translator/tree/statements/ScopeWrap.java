/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.statements;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

import java.util.function.Consumer;

/** scope a statement */
public class ScopeWrap extends Statement {
  private final Statement wrapped;

  public ScopeWrap(Statement wrapped) {
    this.wrapped = wrapped;
    ingest(wrapped);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    wrapped.emit(yielder);
  }

  @Override
  public ControlFlow typing(Environment environment) {
    return wrapped.typing(environment.scope());
  }

  @Override
  public void free(FreeEnvironment environment) {
    wrapped.free(environment);
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    wrapped.writeJava(sb, environment.scope());
  }
}

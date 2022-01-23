/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;

import java.util.function.Consumer;

/** there are operational parameters which are embedded with the document */
public class DocumentConfig extends Definition {
  public final Token name;
  public final Token equals;
  public final Expression value;
  public final Token semicolon;

  public DocumentConfig(Token name, Token equals, Expression value, Token semicolon) {
    this.name = name;
    this.equals = equals;
    this.value = value;
    this.semicolon = semicolon;
    ingest(name, equals, semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(name);
    yielder.accept(equals);
    value.emit(yielder);
    yielder.accept(semicolon);
  }

  @Override
  public void typing(Environment environment) {
    Environment next = environment.scopeWithComputeContext(ComputeContext.Computation);
    next.rules.IsInteger(value.typing(next, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
  }
}

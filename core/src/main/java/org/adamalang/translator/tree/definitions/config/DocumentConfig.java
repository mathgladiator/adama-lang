/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions.config;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;

import java.util.function.Consumer;

/** there are operational parameters which are embedded with the document */
public class DocumentConfig extends StaticPiece {
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
    switch (name.text) {
      case "maximum_history":
        next.rules.IsInteger(value.typing(next, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
        return;
      case "delete_on_close":
        next.rules.IsBoolean(value.typing(next, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
        return;
    }
  }
}

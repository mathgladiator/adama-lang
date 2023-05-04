/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeCheckerProxy;

import java.util.Collections;
import java.util.function.Consumer;

public class AugmentViewerState extends Definition {
  public final Token viewToken;
  public final TyType type;
  public final Token name;
  public final Token semicolon;

  public AugmentViewerState(Token viewToken, TyType type, Token name, Token semicolon) {
    this.viewToken = viewToken;
    this.type = type;
    this.name = name;
    this.semicolon = semicolon;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(viewToken);
    type.emit(yielder);
    yielder.accept(name);
    yielder.accept(semicolon);
  }

  public void typing(TypeCheckerProxy checker) {
    checker.define(name, Collections.EMPTY_SET, (env) -> type.typing(env));
  }
}

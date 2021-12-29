/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions.linq;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;

public class Shuffle extends LinqExpression {
  public final Token shuffleToken;

  public Shuffle(final Token shuffleToken, final Expression sql) {
    super(sql);
    this.shuffleToken = shuffleToken;
    ingest(shuffleToken);
    ingest(sql);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(shuffleToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var type = sql.typing(environment, null);
    if (environment.rules.IsNativeListOfStructure(type, false)) { return type; }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sql.writeJava(sb, environment);
    sb.append(".shuffle(").append(intermediateExpression ? "false, " : "true, ").append("__random)");
  }
}

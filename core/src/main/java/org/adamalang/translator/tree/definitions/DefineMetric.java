/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** define a metric to emit a numeric value periodically */
public class DefineMetric extends Definition {
  public final Token metricToken;
  public final Token nameToken;
  public final Token equalsToken;
  public final Expression expression;
  public final Token semicolonToken;
  public TyType metricType;

  public DefineMetric(Token metricToken, Token nameToken, Token equalsToken, Expression expression, Token semicolonToken) {
    this.metricToken = metricToken;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    this.metricType = null;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(metricToken);
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    expression.free(fe);
    checker.register(fe.free, (environment) -> {
      metricType = expression.typing(environment, new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this));
      // we only support numeric types (for now))
      environment.rules.IsNumeric(metricType, false);
    });
  }
}

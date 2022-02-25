/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.checking;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.operators.BinaryOperatorResult;
import org.adamalang.translator.tree.expressions.operators.BinaryOperatorTable;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;
import org.adamalang.translator.tree.types.checking.properties.CanTestEqualityResult;

public class LocalTypeAlgebraResult {
  private final Environment environment;
  private final Expression left;
  private final Expression right;
  public TyType typeLeft = null;
  public TyType typeRight = null;
  CanTestEqualityResult equalityResult = CanTestEqualityResult.No;

  public LocalTypeAlgebraResult(final Environment environment, final Expression left, final Expression right) {
    this.environment = environment;
    this.left = left;
    this.right = right;
  }

  public boolean equals() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    equalityResult = environment.rules.CanTestEquality(typeLeft, typeRight, false);
    return equalityResult != CanTestEqualityResult.No;
  }
}

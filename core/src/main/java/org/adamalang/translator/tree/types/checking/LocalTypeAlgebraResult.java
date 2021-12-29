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
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;
import org.adamalang.translator.tree.types.checking.properties.CanTestEqualityResult;

public class LocalTypeAlgebraResult {
  private final Environment environment;
  CanTestEqualityResult equalityResult = CanTestEqualityResult.No;
  private final Expression left;
  public CanMathResult mathResult = CanMathResult.No;
  private final Expression operation;
  private final Expression right;
  public TyType typeLeft = null;
  public TyType typeRight = null;

  public LocalTypeAlgebraResult(final Environment environment, final Expression operation, final Expression left, final Expression right) {
    this.environment = environment;
    this.operation = operation;
    this.left = left;
    this.right = right;
  }

  public TyType add() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    mathResult = environment.rules.CanAdd(typeLeft, typeRight, false);
    if (mathResult != CanMathResult.No) { return environment.rules.InventMathType(typeLeft, typeRight, mathResult); }
    return null;
  }

  public boolean compare() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    return environment.rules.CanCompare(typeLeft, typeRight, false);
  }

  public TyType divide() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    mathResult = environment.rules.CanDivide(typeLeft, typeRight, false);
    if (mathResult != CanMathResult.No) { return environment.rules.InventMathType(typeLeft, typeRight, mathResult); }
    return null;
  }

  public boolean equals() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    equalityResult = environment.rules.CanTestEquality(typeLeft, typeRight, false);
    return equalityResult != CanTestEqualityResult.No;
  }

  public boolean logic() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    return environment.rules.CanUseLogic(typeLeft, typeRight, false);
  }

  public TyType mod() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    mathResult = environment.rules.CanMod(typeLeft, typeRight, false);
    if (mathResult != CanMathResult.No) { return environment.rules.InventMathType(typeLeft, typeRight, mathResult); }
    return null;
  }

  public TyType multiply() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    mathResult = environment.rules.CanMultiply(typeLeft, typeRight, false);
    if (mathResult != CanMathResult.No) { return environment.rules.InventMathType(typeLeft, typeRight, mathResult); }
    return null;
  }

  public TyType subtract() {
    typeLeft = left.typing(environment, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);
    mathResult = environment.rules.CanSubstract(typeLeft, typeRight, false);
    if (mathResult != CanMathResult.No) { return environment.rules.InventMathType(typeLeft, typeRight, mathResult); }
    return null;
  }
}

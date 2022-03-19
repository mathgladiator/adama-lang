/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetEnums;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.traits.IsEnum;

import java.util.function.Consumer;

/** binary operation (left $OP right) */
public class BinaryExpression extends Expression {
  public final Expression left;
  public final BinaryOp op;
  public final Token opToken;
  public final Expression right;
  public BinaryOperatorResult operatorResult;

  public BinaryExpression(final Expression left, final Token opToken, final Expression right) {
    this.left = left;
    this.opToken = opToken;
    op = BinaryOp.fromText(opToken.text);
    this.right = right;
    this.ingest(left);
    this.ingest(right);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    left.emit(yielder);
    yielder.accept(opToken);
    right.emit(yielder);
  }

  private boolean areBothTypesEnums(final Environment environment, TyType left, TyType right) {
    final var aEnum = RuleSetEnums.IsEnum(environment, left, true);
    final var bEnum = RuleSetEnums.IsEnum(environment, right, true);
    if (aEnum && bEnum) {
      if (((IsEnum) left).name().equals(((IsEnum) right).name())) {
        return true;
      } else {
        environment.document.createError(DocumentPosition.sum(left, right), String.format("Type check failure: enum types are incompatible '%s' vs '%s'.", left.getAdamaType(), right.getAdamaType()), "RuleSetEquality");
        return false;
      }
    }
    return false;
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    Environment leftEnv = op.leftAssignment ? environment.scopeWithComputeContext(ComputeContext.Assignment) : environment;
    TyType typeLeft = left.typing(leftEnv, null);
    typeLeft = environment.rules.Resolve(typeLeft, false);
    TyType typeRight = right.typing(environment, null);
    typeRight = environment.rules.Resolve(typeRight, false);

    if (op == BinaryOp.Equal || op == BinaryOp.NotEqual) {
      TyType boolType = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, opToken).withPosition(this);
      if (areBothTypesEnums(environment, typeLeft, typeRight)) {
        operatorResult = new BinaryOperatorResult(boolType, "%s " + op.javaOp + " %s", false);
        return boolType;
      }
      // other special rules here; like, what? messages, tuples? anything with .equals(), where is the logic from strings
    }

    operatorResult = BinaryOperatorTable.INSTANCE.find(typeLeft, op.javaOp, typeRight, environment);
    if (operatorResult != null) {
      return operatorResult.type.makeCopyWithNewPosition(typeLeft, TypeBehavior.ReadOnlyNativeValue).withPosition(typeRight);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var leftStr = new StringBuilder();
    final var rightStr = new StringBuilder();
    Environment leftEnv = op.leftAssignment ? environment.scopeWithComputeContext(ComputeContext.Assignment) : environment;
    left.writeJava(leftStr, leftEnv);
    right.writeJava(rightStr, environment);
    if (operatorResult != null) {
      if (operatorResult.reverse) {
        sb.append(String.format("%s", String.format(operatorResult.javaPattern, rightStr, leftStr)));
      } else {
        sb.append(String.format("%s", String.format(operatorResult.javaPattern, leftStr, rightStr)));
      }
    }
  }
}

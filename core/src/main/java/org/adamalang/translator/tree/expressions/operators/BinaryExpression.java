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

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.LocalTypeAlgebraResult;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.traits.details.DetailComparisonTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;

import java.util.function.Consumer;

/** binary operation (left $OP right) */
public class BinaryExpression extends Expression {
  public final Expression left;
  public final BinaryOp op;
  public final Token opToken;
  public final Expression right;
  private LocalTypeAlgebraResult typingResult;

  public BinaryExpression(final Expression left, final Token opToken, final Expression right) {
    this.left = left;
    this.opToken = opToken;
    op = BinaryOp.fromText(opToken.text);
    this.right = right;
    this.ingest(left);
    this.ingest(right);
    typingResult = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    left.emit(yielder);
    yielder.accept(opToken);
    right.emit(yielder);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    typingResult = new LocalTypeAlgebraResult(environment, left, right);
    if (op != null) {
      switch (op) {
        case Add:
          return typingResult.table("+");
        case Multiply:
          return typingResult.table("*");
        case Divide:
          return typingResult.table("/");
        case Mod:
          return typingResult.mod();
        case Subtract:
          return typingResult.table("-");
        case LessThan:
        case GreaterThan:
        case GreaterThanOrEqual:
        case LessThanOrEqual:
          if (typingResult.compare()) {
            return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, opToken).withPosition(this);
          }
          return null;
        case Equal:
        case NotEqual:
          if (typingResult.equals()) {
            return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, opToken).withPosition(this);
          }
          return null;
        case LogicalAnd:
        case LogicalOr:
          if (typingResult.logic()) {
            return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, opToken).withPosition(this);
          }
      }
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var leftStr = new StringBuilder();
    final var rightStr = new StringBuilder();
    left.writeJava(leftStr, environment);
    right.writeJava(rightStr, environment);
    if (typingResult.operatorResult != null) {
      if (typingResult.operatorResult.reverse) {
        sb.append(String.format("%s", String.format(typingResult.operatorResult.javaPattern, rightStr, leftStr)));
      } else {
        sb.append(String.format("%s", String.format(typingResult.operatorResult.javaPattern, leftStr, rightStr)));
      }
      return;
    }
    final var typeLeft = typingResult.typeLeft;
    if (typingResult.typeLeft instanceof DetailEqualityTestingRequiresWrapping) {
      switch (op) {
        case Equal:
          sb.append(String.format("%s", String.format(((DetailEqualityTestingRequiresWrapping) typeLeft).getEqualityTestingBinaryPattern(), leftStr, rightStr)));
          return;
        case NotEqual:
          sb.append(String.format("!%s", String.format(((DetailEqualityTestingRequiresWrapping) typeLeft).getEqualityTestingBinaryPattern(), leftStr, rightStr)));
          return;
      }
    }
    if (typingResult.typeLeft instanceof DetailComparisonTestingRequiresWrapping) {
      switch (op) {
        case LessThan:
          sb.append(String.format("%s < 0", String.format(((DetailComparisonTestingRequiresWrapping) typeLeft).getComparisonTestingBinaryPattern(), leftStr, rightStr)));
          return;
        case LessThanOrEqual:
          sb.append(String.format("%s <= 0", String.format(((DetailComparisonTestingRequiresWrapping) typeLeft).getComparisonTestingBinaryPattern(), leftStr, rightStr)));
          return;
        case GreaterThan:
          sb.append(String.format("%s > 0", String.format(((DetailComparisonTestingRequiresWrapping) typeLeft).getComparisonTestingBinaryPattern(), leftStr, rightStr)));
          return;
        case GreaterThanOrEqual:
          sb.append(String.format("%s >= 0", String.format(((DetailComparisonTestingRequiresWrapping) typeLeft).getComparisonTestingBinaryPattern(), leftStr, rightStr)));
          return;
      }
    }
    sb.append(leftStr).append(" ").append(op.javaOp).append(" ").append(rightStr);
  }
}

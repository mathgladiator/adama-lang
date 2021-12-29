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

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.WrapInstruction;
import org.adamalang.translator.tree.types.traits.SupportsTwoPhaseTyping;

/** ternary operator / inline condition (bool ? tExpr : fExpr) */
public class InlineConditional extends Expression implements SupportsTwoPhaseTyping {
  public final Token colonToken;
  public final Expression condition;
  public final Expression falseValue;
  public final Token questionToken;
  public final Expression trueValue;
  private WrapInstruction wrapInstruction;

  /** ternary operator (https://en.wikipedia.org/wiki/%3F:)
   *
   * @param condition     the condition to check
   * @param questionToken the token for the ?
   * @param trueValue     value when condition is true
   * @param colonToken    the token for the :
   * @param falseValue    value when condition is false */
  public InlineConditional(final Expression condition, final Token questionToken, final Expression trueValue, final Token colonToken, final Expression falseValue) {
    this.condition = condition;
    this.questionToken = questionToken;
    this.trueValue = trueValue;
    this.colonToken = colonToken;
    this.falseValue = falseValue;
    this.ingest(condition);
    this.ingest(trueValue);
    this.ingest(falseValue);
    wrapInstruction = WrapInstruction.None;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    condition.emit(yielder);
    yielder.accept(questionToken);
    trueValue.emit(yielder);
    yielder.accept(colonToken);
    falseValue.emit(yielder);
  }

  @Override
  public TyType estimateType(final Environment environment) {
    return typingReal(environment, null, false);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return typingReal(environment, suggestion, true);
  }

  protected TyType typingReal(final Environment environment, final TyType suggestion, final boolean commit) {
    final var conditionType = condition.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(conditionType, false);
    TyType trueType;
    TyType falseType;
    if (trueValue instanceof SupportsTwoPhaseTyping) {
      trueType = ((SupportsTwoPhaseTyping) trueValue).estimateType(environment);
    } else {
      trueType = trueValue.typing(environment, suggestion);
    }
    if (falseValue instanceof SupportsTwoPhaseTyping) {
      falseType = ((SupportsTwoPhaseTyping) falseValue).estimateType(environment);
    } else {
      falseType = falseValue.typing(environment, suggestion);
    }
    wrapInstruction = environment.rules.GetMaxTypeBasedWrappingInstruction(trueType, falseType);
    var result = environment.rules.GetMaxType(trueType, falseType, false);
    if (commit) {
      result = environment.rules.EnsureRegisteredAndDedupe(result, false);
      if (trueValue instanceof SupportsTwoPhaseTyping) {
        ((SupportsTwoPhaseTyping) trueValue).upgradeType(environment, result);
      }
      if (falseValue instanceof SupportsTwoPhaseTyping) {
        ((SupportsTwoPhaseTyping) falseValue).upgradeType(environment, result);
      }
      return result;
    }
    return result;
  }

  @Override
  public void upgradeType(final Environment environment, final TyType newType) {
    cachedType = newType;
    if (trueValue instanceof SupportsTwoPhaseTyping) {
      ((SupportsTwoPhaseTyping) trueValue).upgradeType(environment, newType);
    }
    if (falseValue instanceof SupportsTwoPhaseTyping) {
      ((SupportsTwoPhaseTyping) falseValue).upgradeType(environment, newType);
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("(");
    condition.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(" ? ");
    if (wrapInstruction == WrapInstruction.WrapAWithMaybe) {
      sb.append("new NtMaybe<>(");
      trueValue.writeJava(sb, environment);
      sb.append(")");
    } else {
      trueValue.writeJava(sb, environment);
    }
    sb.append(" : ");
    if (wrapInstruction == WrapInstruction.WrapBWithMaybe) {
      sb.append("new NtMaybe<>(");
      falseValue.writeJava(sb, environment);
      sb.append(")");
    } else {
      falseValue.writeJava(sb, environment);
    }
    sb.append(")");
  }
}

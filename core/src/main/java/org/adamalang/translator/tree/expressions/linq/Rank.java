/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.natives.TyNativeList;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Rank extends LinqExpression implements LatentCodeSnippet {
  public final Token rank;
  public final Token name;
  public final Token colon;
  public final Expression expressionRank;
  public final Token threshold;
  public final Expression thresholdValue;
  private final TreeMap<String, String> closureTypes;
  private final TreeMap<String, TyType> closureTyTypes;
  private int generatedClassId;
  private TyType elementType;
  private TyType rankType;
  private final StringBuilder exprRankCode;
  private final StringBuilder exprThresholdCode;
  private String iterType;

  public Rank(Expression sql, Token rank, Token name, Token colon, Expression expressionRank, Token threshold, Expression thresholdValue) {
    super(sql);
    this.rank = rank;
    this.name = name;
    this.colon = colon;
    this.expressionRank = expressionRank;
    this.threshold = threshold;
    this.thresholdValue = thresholdValue;
    ingest(sql);
    ingest(expressionRank);
    ingest(thresholdValue);
    this.closureTypes = new TreeMap<>();
    this.closureTyTypes = new TreeMap<>();
    this.generatedClassId = -1;
    exprRankCode = new StringBuilder();
    exprThresholdCode = new StringBuilder();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(rank);
    yielder.accept(name);
    yielder.accept(colon);
    expressionRank.emit(yielder);
    if (threshold != null) {
      yielder.accept(threshold);
      thresholdValue.emit(yielder);
    }
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
    expressionRank.format(formatter);
    if (thresholdValue != null) {
      thresholdValue.format(formatter);
    }
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType base = sql.typing(environment, suggestion);
    if (base != null && environment.rules.IsNativeListOfStructure(base, false)) {
      environment.document.add(this);
      generatedClassId = environment.document.inventClassId();
      elementType = environment.rules.Resolve(((TyNativeList) environment.rules.Resolve(base, false)).elementType, false);
      Environment next = environment.scopeAsReadOnlyBoundary().scopeWithComputeContext(ComputeContext.Computation);
      final var watch = next.watch((name, tyUn) -> {
        TyType ty = environment.rules.Resolve(tyUn, false);
        if (GlobalObjectPool.ignoreCapture(name, ty)) {
          return;
        }
        if (!closureTypes.containsKey(name) && ty != null) {
          closureTyTypes.put(name, ty);
          closureTypes.put(name, ty.getJavaConcreteType(environment));
        }
      }).captureSpecials();
      if (thresholdValue != null) {
        TyType thresholdType = thresholdValue.typing(watch, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, threshold));
        environment.rules.IsNumeric(thresholdType, false);
      }
      watch.define(name.text, elementType, true, expressionRank);
      HashMap<String, TyType> specialsUsed = watch.specials();
      rankType = expressionRank.typing(watch, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, rank));
      for (Map.Entry<String, TyType> entry : specialsUsed.entrySet()) {
        closureTyTypes.put(entry.getKey(), entry.getValue());
        closureTypes.put(entry.getKey(), entry.getValue().getJavaConcreteType(environment));
      }
      if (environment.rules.IsNumeric(rankType, false)) {
        iterType = elementType.getJavaBoxType(environment);
        return base.makeCopyWithNewPosition(this, base.behavior);
      }
    }
    return null;
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    FreeEnvironment child = environment.push();
    child.define(name.text);
    expressionRank.free(child);
    if (thresholdValue != null) {
      thresholdValue.free(environment);
    }
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    if (passedTypeChecking() && rankType != null) {
      sql.writeJava(sb, environment);
      sb.append(".rank(new __CLOSURE_Ranker" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getKey());
      }
      sb.append("))");
      {
        Environment next = environment.scopeAsReadOnlyBoundary().scopeWithComputeContext(ComputeContext.Computation);
        next.define(name.text, elementType, true, expressionRank);
        expressionRank.writeJava(exprRankCode, next);
      }
      if (thresholdValue != null) {
        Environment next = environment.scopeAsReadOnlyBoundary().scopeWithComputeContext(ComputeContext.Computation);
        thresholdValue.writeJava(exprThresholdCode, next);
      }
    }
  }

  @Override
  public void writeLatentJava(StringBuilderWithTabs sb) {
    sb.append("private class __CLOSURE_Ranker" + generatedClassId + " implements Ranker<" + iterType + "> {").tabUp().writeNewline();
    for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
      sb.append("private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";").writeNewline();
    }
    if (closureTypes.size() > 0) {
      sb.append("private __CLOSURE_Ranker" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getValue()).append(" ").append(entry.getKey());
      }
      sb.append(") {").tabUp().writeNewline();
      var untilTabDown = closureTypes.size();
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        sb.append("this." + entry.getKey() + " = " + entry.getKey() + ";");
        if (--untilTabDown <= 0) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public double rank(").append(iterType).append(" ").append(name.text).append(") {").tabUp().writeNewline();
    sb.append(String.format("__code_cost ++;")).writeNewline();
    sb.append("return (double) (" + exprRankCode.toString() + ");").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    if (threshold != null) {
      sb.append("public double threshold() {").tabUp().writeNewline();
      sb.append(String.format("__code_cost ++;")).writeNewline();
      sb.append("return (double) (" + exprThresholdCode.toString() + ");").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("public double threshold() { return -Double.MAX_VALUE; }").writeNewline();
    }
    sb.append("}").writeNewline();
  }
}

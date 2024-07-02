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
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;

import java.util.function.Consumer;

/** provides 1 <= x <= 3 for numeric types */
public class RangeOperator extends Expression {
  public final Expression low;
  public final Token opLow;
  public final Expression squeeze;
  public final Token opHi;
  public final Expression high;

  public RangeOperator(Expression low, Token opLow, Expression squeeze, Token opHi, Expression high) {
    this.low = low;
    this.opLow = opLow;
    this.squeeze = squeeze;
    this.opHi = opHi;
    this.high = high;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    low.emit(yielder);
    yielder.accept(opLow);
    squeeze.emit(yielder);
    yielder.accept(opHi);
    high.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    low.format(formatter);
    squeeze.format(formatter);
    high.format(formatter);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType lowTy = low.typing(environment, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null));
    TyType sqeeuzeTy = squeeze.typing(environment, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null));
    TyType highTy = high.typing(environment, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null));
    environment.rules.IsNumeric(lowTy, false);
    environment.rules.IsNumeric(sqeeuzeTy, false);
    environment.rules.IsNumeric(highTy, false);
    return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null);
  }

  @Override
  public void free(FreeEnvironment environment) {
    low.free(environment);
    squeeze.free(environment);
    high.free(environment);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    String a = opLow.isSymbolWithTextEq("<") ? "E" : "I";
    String b = opHi.isSymbolWithTextEq("<") ? "E" : "I";
    sb.append("LibMath.dRange").append(a).append(b).append("(");
    low.writeJava(sb, environment);
    sb.append(",");
    squeeze.writeJava(sb, environment);;
    sb.append(",");
    high.writeJava(sb, environment);
    sb.append(")");
  }
}

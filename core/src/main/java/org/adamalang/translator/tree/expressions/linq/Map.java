/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetLists;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.util.ArrayList;
import java.util.function.Consumer;

/** map a function over the elements in a list to produce a new list L' = f(L) */
public class Map extends LinqExpression {
  public final Token mapToken;
  public final Expression func;
  private FunctionOverloadInstance functionInstance;
  private TyNativeFunctional functionalType;

  public Map(final Expression sql, final Token mapToken, final Expression func) {
    super(sql);
    ingest(sql);
    this.mapToken = mapToken;
    this.func = func;
    ingest(func);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(mapToken);
    func.emit(yielder);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    final var typeSql = sql.typing(environment, null);
    if (typeSql != null && RuleSetLists.IsNativeList(environment, typeSql, false)) {
      ArrayList<TyType> guessInputTypes = new ArrayList<>();
      guessInputTypes.add(RuleSetCommon.ExtractEmbeddedType(environment, typeSql, false));
      FunctionOverloadInstance guess = new FunctionOverloadInstance("unknown", null, guessInputTypes, FunctionPaint.READONLY_NORMAL);
      TyType guessType = new TyNativeFunctional("unknown", FunctionOverloadInstance.WRAP(guess), FunctionStyleJava.None);
      TyType funcType = func.typing(environment, guessType);
      if (environment.rules.IsFunction(funcType, false)) {
        functionalType = (TyNativeFunctional) funcType;
        functionInstance = functionalType.find(this, guessInputTypes, environment);
        if (functionInstance.aborts) {
          environment.document.createError(this, String.format("Function '%s' must not abort within a map function", funcType.getAdamaType()));
        }
        if (functionInstance.returnType == null) {
          environment.document.createError(this, String.format("Function '%s' must return value", funcType.getAdamaType()));
        }
        return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(functionInstance.returnType));
      }
    }
    return null;
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sql.writeJava(sb, environment);
    sb.append(".mapFunction(");
    if (functionalType != null) {
      switch (functionalType.style) {
        case ExpressionThenArgs:
        case ExpressionThenNameWithArgs:
          func.writeJava(sb, environment);
          break;
        default:
          sb.append("(__item) -> ").append(functionInstance.javaFunction).append("(__item)");
          break;
      }
    } else {
      sb.append("(__list) -> (__list)");
    }
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    func.free(environment);
  }
}

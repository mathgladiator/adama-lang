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

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Reduce extends LinqExpression {
  public final Token fieldToken;
  private FunctionOverloadInstance functionInstance;
  public final Expression functionToReduceWith;
  public final Token onToken;
  public final Token reduceToken;
  private boolean requireGet;
  public final Token viaToken;

  public Reduce(final Expression sql, final Token reduceToken, final Token onToken, final Token fieldToken, final Token viaToken, final Expression functionToReduceWith) {
    super(sql);
    this.reduceToken = reduceToken;
    this.onToken = onToken;
    this.fieldToken = fieldToken;
    this.viaToken = viaToken;
    this.functionToReduceWith = functionToReduceWith;
    functionInstance = null;
    requireGet = false;
    ingest(sql);
    ingest(fieldToken);
    ingest(functionToReduceWith);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(reduceToken);
    if (onToken != null) {
      yielder.accept(onToken);
    }
    yielder.accept(fieldToken);
    yielder.accept(viaToken);
    functionToReduceWith.emit(yielder);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var typeSql = sql.typing(environment, null);
    final var isGoodSql = environment.rules.IsNativeListOfStructure(typeSql, false);
    final var funcType = functionToReduceWith.typing(environment, null);
    TyType resultType = null;
    if (isGoodSql && environment.rules.IsFunction(funcType, false)) {
      final var functionalType = (TyNativeFunctional) funcType;
      final var expectedArgs = new ArrayList<TyType>();
      expectedArgs.add(typeSql);
      functionInstance = functionalType.find(this, expectedArgs, environment);
      if (functionInstance != null) {
        if (!functionInstance.pure) {
          environment.document.createError(this, String.format("Function '%s' must be a pure function a value", funcType.getAdamaType()), "Reduce");
        }
        if (functionInstance.returnType == null) {
          environment.document.createError(this, String.format("Function '%s' must return value", funcType.getAdamaType()), "Reduce");
        }
        final var elementType = (IsStructure) environment.rules.ExtractEmbeddedType(typeSql, false);
        final var fd = elementType.storage().fields.get(fieldToken.text);
        if (fd != null) {
          var fieldType = environment.rules.Resolve(fd.type, false);
          if (fieldType instanceof DetailComputeRequiresGet) {
            requireGet = true;
            fieldType = environment.rules.Resolve(((DetailComputeRequiresGet) fieldType).typeAfterGet(environment), false);
          }
          if (fieldType != null && functionInstance.returnType != null) {
            resultType = new TyNativeMap(TypeBehavior.ReadOnlyNativeValue, null, null, fieldType, null, functionInstance.returnType, null);
            resultType.typing(environment);
          }
        } else {
          environment.document.createError(this, String.format("Field '%s' was not found for reduction", fieldToken.text), "Reduce");
        }
      }
    }
    return resultType;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sql.writeJava(sb, environment);
    if (functionInstance != null) {
      sb.append(".reduce((__item) -> __item.").append(fieldToken.text);
      if (requireGet) {
        sb.append(".get()");
      }
      sb.append(", (__list) -> ").append(functionInstance.javaFunction).append("(__list)");
      sb.append(")");
    }
  }
}

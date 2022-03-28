/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyInternalReadonlyClass;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.function.Consumer;

/** field/method look up ($e.field) */
public class FieldLookup extends Expression {
  public final Token dotToken;
  public final Expression expression;
  public final String fieldName;
  public final Token fieldNameToken;
  private boolean addGet;
  private TyType aggregateType;
  private boolean isGlobalObject;
  private boolean makeReactiveList;
  private boolean onlyExpression;
  private String overrideFieldName;
  private boolean requiresMaybeUnpack;
  private boolean doubleMaybeUnpack;
  private String maybeCastType;

  /**
   * @param expression the expression to evaluate
   * @param fieldNameToken the field to look up
   */
  public FieldLookup(final Expression expression, final Token dotToken, final Token fieldNameToken) {
    this.expression = expression;
    this.dotToken = dotToken;
    this.fieldNameToken = fieldNameToken;
    fieldName = fieldNameToken.text;
    this.ingest(expression);
    this.ingest(fieldNameToken);
    addGet = false;
    makeReactiveList = false;
    overrideFieldName = null;
    isGlobalObject = false;
    requiresMaybeUnpack = false;
    maybeCastType = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    expression.emit(yielder);
    yielder.accept(dotToken);
    yielder.accept(fieldNameToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    var eType = expression.typing(environment, null);
    eType = environment.rules.ResolvePtr(eType, false);
    if (eType != null) {
      // global object, find the function
      if (eType instanceof TyNativeGlobalObject) {
        isGlobalObject = true;
        final var func = ((TyNativeGlobalObject) eType).lookupMethod(fieldName, environment);
        if (func == null) {
          environment.document.createError(this, String.format("Global '%s' lacks '%s'", ((TyNativeGlobalObject) eType).globalName, fieldName), "GlobalLookup");
          return null;
        }
      }
      if (eType instanceof TyInternalReadonlyClass) {
        return ((TyInternalReadonlyClass) eType).getLookupType(environment, fieldName);
      }
      if (eType instanceof TyNativeList) {
        final var elementType = environment.rules.ResolvePtr(((TyNativeList) eType).getEmbeddedType(environment), false);
        if (elementType != null && elementType instanceof TyReactiveRecord) {
          final var fd = ((TyReactiveRecord) elementType).storage.fields.get(fieldName);
          if (fd != null) {
            aggregateType = fd.type;
            makeReactiveList = true;
            if (aggregateType instanceof DetailComputeRequiresGet && environment.state.isContextComputation()) {
              addGet = true;
              aggregateType = ((DetailComputeRequiresGet) aggregateType).typeAfterGet(environment);
            }
            return new TyNativeList(TypeBehavior.ReadWriteWithSetGet, null, null, new TokenizedItem<>(aggregateType.makeCopyWithNewPosition(this, aggregateType.behavior))).withPosition(this);
          }
        }
      }
      if (eType instanceof DetailTypeHasMethods) {
        final var functional = ((DetailTypeHasMethods) eType).lookupMethod(fieldName, environment);
        if (functional != null) {
          onlyExpression = functional instanceof TyNativeAggregateFunctional || functional.style.useOnlyExpressionInLookup;
          if (functional instanceof TyNativeFunctionInternalFieldReplacement) {
            overrideFieldName = functional.name;
          }
          return functional;
        }
      }
      if (environment.rules.IsMaybe(eType, true)) {
        requiresMaybeUnpack = true;
        eType = ((DetailContainsAnEmbeddedType) eType).getEmbeddedType(environment);
      }
      if (eType instanceof IsStructure) {
        if (!environment.state.isContextComputation() && eType.behavior == TypeBehavior.ReadOnlyNativeValue) {
          environment.document.createError(this, String.format("The field '%s' is on a readonly message", fieldName), "FieldLookup");
        }
        final var hrs = (IsStructure) eType;
        final var fd = hrs.storage().fields.get(fieldName);
        if (fd != null) {
          TyType actualType = environment.rules.Resolve(fd.type, false);
          if (environment.rules.IsMaybe(actualType, true) && requiresMaybeUnpack) {
            doubleMaybeUnpack = true;
            actualType = ((DetailContainsAnEmbeddedType) actualType).getEmbeddedType(environment);
          }
          if (actualType != null) {
            TyType typeToReturn;
            if (actualType instanceof DetailComputeRequiresGet && environment.state.isContextComputation()) {
              addGet = true;
              typeToReturn = ((DetailComputeRequiresGet) actualType).typeAfterGet(environment);
            } else {
              typeToReturn = actualType.makeCopyWithNewPosition(this, actualType.behavior);
            }
            if (requiresMaybeUnpack) {
              maybeCastType = eType.getJavaBoxType(environment);
              return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("MAYBE"), new TokenizedItem<>(typeToReturn));
            } else {
              return typeToReturn;
            }
          }
        }
      }
      environment.document.createError(this, String.format("Type '%s' lacks field '%s'", eType.getAdamaType(), fieldName), "FieldLookup");
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (passedTypeChecking() && !isGlobalObject) {
      expression.writeJava(sb, environment);
      if (onlyExpression) {
        return;
      }
      sb.append(".");
      var fieldNameToUse = fieldName;
      if (overrideFieldName != null) {
        fieldNameToUse = overrideFieldName;
      }
      if (requiresMaybeUnpack) {
        sb.append("unpack").append(doubleMaybeUnpack ? "Transfer" : "").append("((item) -> ((").append(maybeCastType).append(")").append(" item).").append(fieldNameToUse);
        if (addGet) {
          sb.append(".get()");
        }
        sb.append(")");
      } else if (makeReactiveList && aggregateType != null) {
        sb.append("transform((item) -> item.").append(fieldNameToUse);
        if (addGet) {
          sb.append(".get()");
        }
        sb.append(")");
      } else {
        sb.append(fieldNameToUse);
        if (addGet) {
          sb.append(".get()");
        }
      }
    }
  }
}

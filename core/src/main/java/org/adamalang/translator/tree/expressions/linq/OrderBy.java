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
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.natives.TyNativeRef;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.ArrayList;
import java.util.function.Consumer;

/** order the given sql expression result by the list of keys */
public class OrderBy extends LinqExpression implements LatentCodeSnippet {
  public final Token byToken;
  public final ArrayList<OrderPair> keys;
  public final Token orderToken;
  private final ArrayList<String> compareLines;
  private String comparatorName;
  private IsStructure elementType;

  public OrderBy(final Expression sql, final Token orderToken, final Token byToken, final ArrayList<OrderPair> keys) {
    super(sql);
    this.orderToken = orderToken;
    this.byToken = byToken;
    this.keys = keys;
    ingest(sql);
    ingest(orderToken);
    for (final OrderPair key : keys) {
      ingest(key);
    }
    elementType = null;
    compareLines = new ArrayList<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(orderToken);
    if (byToken != null) {
      yielder.accept(byToken);
    }
    for (final OrderPair op : keys) {
      op.emit(yielder);
    }
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var typeSql = sql.typing(environment, null /* no suggestion makes sense */);
    if (typeSql != null && environment.rules.IsNativeListOfStructure(typeSql, false)) {
      var element = RuleSetCommon.ExtractEmbeddedType(environment, typeSql, false);
      element = RuleSetCommon.ResolvePtr(environment, element, false);
      if (element != null && element instanceof IsStructure) {
        elementType = (IsStructure) element;
        for (final OrderPair key : keys) {
          final var fd = ((IsStructure) element).storage().fields.get(key.name);
          if (fd != null) {
            var fieldType = fd.type;
            if (fieldType instanceof TyReactiveLazy) {
              fieldType = environment.rules.ExtractEmbeddedType(fieldType, false);
            } else {
              fieldType = RuleSetCommon.Resolve(environment, fieldType, false);
            }
            if (fieldType instanceof TyReactiveMaybe || fieldType instanceof TyNativeMaybe) {
              fieldType = environment.rules.ExtractEmbeddedType(fieldType, false);
            }
            if (!(fieldType instanceof IsOrderable)) {
              environment.document.createError(key, String.format("Typing issue: the structure '%s' has field '%s' but it is not orderable..", element.getAdamaType(), key.name), "RuleSetStructures");
            }
          } else {
            environment.document.createError(key, String.format("Field not found: the structure '%s' does not contain the field '%s'.", element.getAdamaType(), key.name), "RuleSetStructures");
          }
        }
      }
      return typeSql.makeCopyWithNewPosition(this, typeSql.behavior);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var comparatorNameBuilder = new StringBuilder();
    comparatorNameBuilder.append("__ORDER_" + elementType.name());
    sql.writeJava(sb, environment);
    for (final OrderPair key : keys) {
      comparatorNameBuilder.append("_").append(key.name).append(key.asc ? "_a" : "_d");
      final var fd = elementType.storage().fields.get(key.name);
      if (fd != null) {
        final var cmpLine = new StringBuilder();
        var addLazyGet = false;
        var compareType = fd.type;
        if (compareType instanceof TyReactiveLazy || compareType instanceof TyNativeRef) {
          compareType = environment.rules.ExtractEmbeddedType(compareType, false);
          addLazyGet = true;
        } else {
          compareType = RuleSetCommon.Resolve(environment, compareType, false);
        }
        if (compareType instanceof TyReactiveMaybe || compareType instanceof TyNativeMaybe) {
          cmpLine.append(key.asc ? "" : "-").append("__a.").append(key.name).append(addLazyGet ? ".get()" : "").append(".compareValues(__b.").append(key.name).append(addLazyGet ? ".get()" : "").append(", (__x, __y) -> __x.compareTo(__y))");
        } else {
          cmpLine.append(key.asc ? "" : "-").append("__a.").append(key.name).append(addLazyGet ? ".get()" : "").append(".compareTo(__b.").append(key.name).append(addLazyGet ? ".get()" : "").append(")");
        }
        compareLines.add(cmpLine.toString());
      }
    }
    comparatorName = comparatorNameBuilder.toString();
    sb.append(".orderBy(").append(intermediateExpression ? "false, " : "true, ").append(comparatorName).append(")");
    environment.document.add(comparatorName, this);
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    if (elementType != null) {
      sb.append("private final static Comparator<RTx").append(elementType.name()).append("> ").append(comparatorName).append(" = new Comparator<RTx").append(elementType.name()).append(">() {").tabUp().writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public int compare(RTx").append(elementType.name()).append(" __a, RTx").append(elementType.name()).append(" __b) {").tabUp().writeNewline();
      var first = true;
      var n = keys.size();
      for (final String compareLine : compareLines) {
        n--;
        if (n == 0) {
          sb.append("return ").append(compareLine).append(";").tabDown().writeNewline();
        } else {
          if (first) {
            sb.append("int ");
            first = false;
          }
          sb.append("result = ").append(compareLine).append(";").writeNewline();
          sb.append("if (result != 0) return result;").writeNewline();
        }
      }
      sb.append("}").tabDown().writeNewline();
      sb.append("};").writeNewline();
    }
  }
}

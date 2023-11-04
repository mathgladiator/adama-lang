/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
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
  public void format(Formatter formatter) {
    sql.format(formatter);
    for (final OrderPair op : keys) {
      op.format(formatter);
    }
  }

  public static TyType getOrderableType(FieldDefinition fd, Environment environment) {
    var fieldType = fd.type;
    if (fieldType instanceof TyReactiveLazy) {
      fieldType = environment.rules.ExtractEmbeddedType(fieldType, false);
    } else {
      fieldType = RuleSetCommon.Resolve(environment, fieldType, false);
    }
    if (fieldType instanceof TyReactiveMaybe || fieldType instanceof TyNativeMaybe) {
      fieldType = environment.rules.ExtractEmbeddedType(fieldType, false);
    }
    return fieldType;
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
            var fieldType = getOrderableType(fd, environment);
            if (!(fieldType instanceof IsOrderable)) {
              environment.document.createError(key, String.format("Typing issue: the structure '%s' has field '%s' but it is not orderable..", element.getAdamaType(), key.name));
            }
          } else {
            environment.document.createError(key, String.format("Field not found: the structure '%s' does not contain the field '%s'.", element.getAdamaType(), key.name));
          }
        }
      }
      return typeSql.makeCopyWithNewPosition(this, typeSql.behavior);
    }
    return null;
  }

  public static String getCompareLine(FieldDefinition fd, Environment environment, OrderPair key) {
    final var cmpLine = new StringBuilder();
    var compareType = fd.type;
    String nativeCompare = null;
    if (compareType instanceof TyNativeLong) {
      nativeCompare = "Long";
    } else if (compareType instanceof TyNativeInteger || compareType instanceof TyNativeEnum) {
      nativeCompare = "Integer";
    } else if (compareType instanceof TyNativeBoolean) {
      nativeCompare = "Boolean";
    } else if (compareType instanceof TyNativeDouble) {
      nativeCompare = "Double";
    }
    if (nativeCompare != null) {
      cmpLine.append(key.asc ? "" : "-").append(nativeCompare).append(".compare(__a.").append(key.name).append(", __b.").append(key.name).append(")");
      return cmpLine.toString();
    }
    var addLazyGet = false;
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
    return cmpLine.toString();
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
        compareLines.add(getCompareLine(fd, environment, key));
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

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
  }
}

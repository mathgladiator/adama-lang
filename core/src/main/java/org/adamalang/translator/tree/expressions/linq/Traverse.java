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

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.DefineAssoc;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;

import java.util.function.Consumer;

public class Traverse extends LinqExpression{
  private final Token traverse;
  private final Token assocToUse;

  public Traverse(Expression sql, Token traverse, Token assocToUse) {
    super(sql);
    this.traverse = traverse;
    this.assocToUse = assocToUse;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(traverse);
    yielder.accept(assocToUse);
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    DefineAssoc da = environment.document.assocs.get(assocToUse.text);
    if (da == null) {
      environment.document.createError(this, String.format("The assoc '%s' being traversed doesn't exist", assocToUse.text));
    }
    TyType base = sql.typing(environment, suggestion);
    if (base != null && environment.rules.IsNativeListOfStructure(base, false)) {
      TyType embedTest = RuleSetCommon.Resolve(environment, environment.rules.ExtractEmbeddedType(base, false), false);
      if (environment.rules.IsRxStructure(embedTest, false) && da != null) {
        TyReactiveRecord recordType = (TyReactiveRecord) embedTest;
        if (!recordType.name.equals(da.fromTypeName.text)) {
          environment.document.createError(this, String.format("The assoc '%s' has an incompatible domain type; expected '%s', but have '%s'", assocToUse.text, da.fromTypeName.text, recordType.name));
        }
        TyType toType = environment.document.types.get(da.toTypeName.text);
        if (toType != null) {
          return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, traverse, new TokenizedItem<>(toType));
        }
      }
    }
    return null;
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sb.append("___assoc_").append(assocToUse.text).append(".map(");
    sql.writeJava(sb, environment);
    sb.append(")");
  }
}

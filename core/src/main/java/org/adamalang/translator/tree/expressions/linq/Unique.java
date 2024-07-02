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
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetLists;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.DetailCanExtractForUnique;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

public class Unique extends LinqExpression {
  public final Token unique;
  public final Token mode;
  public final Token key;
  private TyType elementType;
  private boolean addGet;
  private String modeToUse = "ListUniqueMode.First";

  public Unique(Expression sql, Token unique, Token mode, Token key) {
    super(sql);
    this.unique = unique;
    this.mode = mode;
    if (mode != null) {
      if ("last".equals(mode.text)) {
        modeToUse = "ListUniqueMode.Last";
      }
    }
    this.key = key;
    ingest(sql);
    ingest(unique);
    if (key != null) {
      ingest(key);
    }
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(unique);
    if (mode != null) {
      yielder.accept(mode);
      yielder.accept(key);
    }
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType typeSql = sql.typing(environment, suggestion);
    if (key != null) {
      if (typeSql != null && environment.rules.IsNativeListOfStructure(typeSql, false)) {
        elementType = RuleSetCommon.ExtractEmbeddedType(environment, typeSql, false);
        elementType = RuleSetCommon.ResolvePtr(environment, elementType, false);
        if (elementType != null && elementType instanceof IsStructure) {
          FieldDefinition fd = ((IsStructure) elementType).storage().fields.get(key.text);
          if (fd != null) {
            TyType fieldType = environment.rules.Resolve(fd.type, false);
            if (fieldType != null) {
              addGet = fieldType instanceof DetailComputeRequiresGet;
              if (addGet) {
                fieldType = ((DetailComputeRequiresGet) fieldType).typeAfterGet(environment);
              }
            }
            if (!(fieldType instanceof DetailCanExtractForUnique)) {
              environment.document.createError(this, "the key '" + key.text + "' must be capable of being compared, hashed, and equality tested for uniqueness");
            }
          } else {
            environment.document.createError(this, "the key '" + key.text + "' is not a field of '" + elementType.getAdamaType() + "'");
          }
        }
      } else {
        environment.document.createError(this, "unique with a key requires the list to contain records or messages");
      }
    } else {
      if (RuleSetLists.IsNativeList(environment, typeSql, false)) {
        elementType = RuleSetCommon.ExtractEmbeddedType(environment, typeSql, false);
        if (elementType != null) {
          addGet = elementType instanceof DetailComputeRequiresGet;
          if (addGet) {
            elementType = ((DetailComputeRequiresGet) elementType).typeAfterGet(environment);
          }
        }
        if (elementType != null && !(elementType instanceof DetailCanExtractForUnique)) {
          environment.document.createError(this, "the element has a type of'" + elementType.getAdamaType() + "' which is not capable of being compared, hashed, and equality tested for uniqueness");
        }
      } else {
        environment.document.createError(this, "unique requires a list");
      }
    }
    return typeSql;
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sql.writeJava(sb, environment);
    sb.append(".unique(").append(modeToUse).append(", (__x) -> ");
    if (key != null) {
      sb.append("__x.").append(key.text);
    } else {
      sb.append("__x");
    }
    if (addGet) {
      sb.append(".get()");
    }
    sb.append(")");
  }
}

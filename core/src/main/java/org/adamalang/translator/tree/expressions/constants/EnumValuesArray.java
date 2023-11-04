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
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.codegen.CodeGenEnums;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.shared.EnumStorage;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class EnumValuesArray extends Expression implements LatentCodeSnippet {
  public final Token colonsToken;
  public final String enumTypeName;
  public final Token enumTypeNameToken;
  public final Token prefixToken;
  public final Token starToken;
  private int prefixCachedID;
  private EnumStorage storage;

  /**
   * The enumeration value
   * @param enumTypeNameToken the token for the type
   */
  public EnumValuesArray(final Token enumTypeNameToken, final Token colonsToken, final Token prefixToken, final Token starToken) {
    this.enumTypeNameToken = enumTypeNameToken;
    this.colonsToken = colonsToken;
    this.prefixToken = prefixToken;
    this.starToken = starToken;
    enumTypeName = enumTypeNameToken.text;
    ingest(enumTypeNameToken);
    ingest(starToken);
    prefixCachedID = 0;
    storage = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(enumTypeNameToken);
    yielder.accept(colonsToken);
    if (prefixToken != null) {
      yielder.accept(prefixToken);
    }
    yielder.accept(starToken);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    final var isEnum = environment.rules.FindEnumType(enumTypeName, this, false);
    if (isEnum != null) {
      if (prefixToken != null) {
        prefixCachedID = environment.autoVariable();
        storage = isEnum.storage();
        environment.document.add(this);
      }
      return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, (TyType) isEnum, null).withPosition(this);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (prefixToken == null) {
      sb.append("__ALL_VALUES_").append(enumTypeName);
    } else {
      sb.append("__").append(prefixToken.text).append(prefixCachedID).append("_").append(enumTypeName);
    }
  }

  public ArrayList<Integer> values(Environment environment) {
    final var isEnum = environment.rules.FindEnumType(enumTypeName, this, false);
    if (isEnum != null) {
      ArrayList<Integer> values = new ArrayList<>();
      for (Map.Entry<String, Integer> entry : isEnum.storage().options.entrySet()) {
        if (prefixToken == null) {
          values.add(entry.getValue());
        } else if (entry.getKey().startsWith(prefixToken.text)) {
          values.add(entry.getValue());
        }
      }
      return values;
    }
    return null;
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    CodeGenEnums.writeEnumArray(sb, enumTypeName, prefixToken.text + prefixCachedID, prefixToken.text, storage);
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}

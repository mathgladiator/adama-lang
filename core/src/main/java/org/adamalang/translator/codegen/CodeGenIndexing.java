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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.Map;

public class CodeGenIndexing {
  public static class IndexClassification {
    public final boolean requiresToInt;
    public final boolean isIntegral;
    public final boolean isPrincipal;
    public final boolean isString;
    public final boolean useHashCode;
    public final boolean good;
    public final String indexValueMethod;
    public final boolean isBoolean;

    public IndexClassification(TyType fieldType) {
      isBoolean = fieldType instanceof TyReactiveBoolean || fieldType instanceof TyNativeBoolean;
      boolean isReactiveIntegral = fieldType instanceof TyReactiveInteger || fieldType instanceof TyReactiveEnum;
      boolean isReactiveRequiresToInt = fieldType instanceof TyReactiveDate || fieldType instanceof TyReactiveTime || fieldType instanceof TyReactiveDateTime;
      boolean isReactiveString = fieldType instanceof TyReactiveString;
      boolean isReactive = isReactiveIntegral || isReactiveRequiresToInt || isReactiveString || fieldType instanceof TyReactiveBoolean || fieldType instanceof TyReactivePrincipal;
      requiresToInt = isReactiveRequiresToInt || fieldType instanceof TyNativeDate || fieldType instanceof TyNativeTime || fieldType instanceof TyNativeDateTime;
      isIntegral = isReactiveIntegral || fieldType instanceof TyNativeInteger || fieldType instanceof TyNativeEnum || requiresToInt;
      isPrincipal = fieldType instanceof TyReactivePrincipal || fieldType instanceof TyNativePrincipal;
      isString = isReactiveString || fieldType instanceof TyNativeString;
      useHashCode = isString || isPrincipal;
      good = isIntegral || isPrincipal || isString || isBoolean;
      if (isReactive) {
        indexValueMethod = "%s.getIndexValue()";
      } else if (isBoolean) {
        indexValueMethod = "((%s) ? 1 : 0)";
      } else if (requiresToInt) {
        indexValueMethod = "%s.toInt()";
      } else if (useHashCode) {
        indexValueMethod = "%s.hashCode()";
      } else {
        indexValueMethod = "%s";
      }
    }
  }

  public static void writeIndices(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    boolean first;
    sb.append("@Override").writeNewline();
    sb.append("public String[] __getIndexColumns() {").tabUp().writeNewline();
    sb.append("return __INDEX_COLUMNS_").append(name).append(";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public int[] __getIndexValues() {").tabUp().writeNewline();
    int indexId = 0;
    sb.append("return new int[] {");
    first = true;
    for (final Map.Entry<String, FieldDefinition> entry : storage.fields.entrySet()) {
      if (!storage.indexSet.contains(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      IndexClassification classification = new IndexClassification(fieldType);
      if (classification.good) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("").append(String.format(classification.indexValueMethod, entry.getKey()));
      }
    }
    sb.append("};").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
  public static void writeIndexConstant(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("private static String[] __INDEX_COLUMNS_").append(name).append(" = new String[] {");
    boolean first = true;
    for (final Map.Entry<String, FieldDefinition> entry : storage.fields.entrySet()) {
      if (!storage.indexSet.contains(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      IndexClassification classification = new IndexClassification(fieldType);
      if (classification.good) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("\"").append(entry.getKey()).append("\"");
      }
    }
    sb.append("};").writeNewline();
  }

}

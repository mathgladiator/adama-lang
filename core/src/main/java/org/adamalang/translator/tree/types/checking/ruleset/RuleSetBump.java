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
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanBumpResult;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.traits.IsNativeValue;

public class RuleSetBump {
  public static CanBumpResult CanBumpBool(final Environment environment, final TyType typeOriginal, final boolean silent) {
    final var type = RuleSetCommon.Resolve(environment, typeOriginal, silent);
    if (type != null) {
      if (RuleSetCommon.IsBoolean(environment, type, true)) {
        return CanBumpResult.YesWithNative;
      }
      if (type instanceof TyNativeList) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, type, silent);
        if (elementType != null) {
          if (RuleSetCommon.IsBoolean(environment, elementType, silent)) {
            return CanBumpResult.YesWithListTransformNative;
          }
        }
        return CanBumpResult.No;
      }
      if (!silent) {
        RuleSetCommon.IsBoolean(environment, type, silent);
      }
    }
    return CanBumpResult.No;
  }

  public static CanBumpResult CanBumpNumeric(final Environment environment, final TyType typeOriginal, final boolean silent) {
    final var type = typeOriginal; // RuleSetCommon.ResolvePastLazy(environment, typeOriginal, silent);
    if (type != null) {
      if (RuleSetCommon.IsInteger(environment, type, true) || RuleSetCommon.IsLong(environment, type, true) || RuleSetCommon.IsDouble(environment, type, true)) {
        if (type instanceof IsNativeValue) {
          return CanBumpResult.YesWithNative;
        } else {
          return CanBumpResult.YesWithSetter;
        }
      }
      if (type instanceof TyNativeList) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, type, silent);
        if (elementType != null) {
          if (RuleSetCommon.IsInteger(environment, elementType, true) || RuleSetCommon.IsLong(environment, elementType, true) || RuleSetCommon.IsDouble(environment, elementType, true)) {
            if (elementType instanceof IsNativeValue) {
              return CanBumpResult.YesWithListTransformNative;
            } else {
              return CanBumpResult.YesWithListTransformSetter;
            }
          }
        }
      }
      if (!silent) {
        environment.document.createError(typeOriginal, String.format("Type check failure: Must have a type of 'int', 'long', 'double', 'list<int>', 'list<long>', 'list<double>'; instead got '%s'", typeOriginal.getAdamaType()));
      }
    }
    return CanBumpResult.No;
  }
}

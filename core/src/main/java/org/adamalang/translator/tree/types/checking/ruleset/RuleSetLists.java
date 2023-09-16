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
import org.adamalang.translator.tree.types.natives.TyNativeList;

public class RuleSetLists {
  public static boolean IsNativeListOfStructure(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    if (tyTypeOriginal != null) {
      final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
      if (IsNativeList(environment, tyType, silent)) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, tyType, silent);
        if (elementType != null) {
          return RuleSetStructures.IsStructure(environment, elementType, silent);
        }
      }
    }
    return false;
  }

  public static boolean IsNativeList(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && tyType instanceof TyNativeList) {
        return true;
      }
      if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: expected an list, but was actually type '%s'.", tyTypeOriginal.getAdamaType()));
      }
    }
    return false;
  }

  static boolean TestReactiveList(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (IsNativeList(environment, tyType, silent)) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, tyType, silent);
        if (elementType != null) {
          return RuleSetCommon.TestReactive(elementType);
        }
      }
    }
    return false;
  }
}

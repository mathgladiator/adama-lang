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
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeSecurePrincipal;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;
import org.adamalang.translator.tree.types.reactive.TyReactivePrincipal;

public class RuleSetAsync {
  public static boolean IsPrincipal(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativePrincipal || tyType instanceof TyReactivePrincipal) {
        return true;
      }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsSecurePrincipal(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeSecurePrincipal) {
        return true;
      }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativeSecurePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null, null, null, null), tyTypeOriginal, silent);
    }
    return false;
  }
}

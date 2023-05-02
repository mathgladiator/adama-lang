/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

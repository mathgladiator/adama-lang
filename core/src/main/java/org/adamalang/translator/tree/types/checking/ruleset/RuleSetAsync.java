/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeClient;
import org.adamalang.translator.tree.types.reactive.TyReactiveClient;

public class RuleSetAsync {
  public static boolean IsClient(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeClient || tyType instanceof TyReactiveClient) { return true; }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativeClient(null), tyTypeOriginal, silent);
    }
    return false;
  }
}

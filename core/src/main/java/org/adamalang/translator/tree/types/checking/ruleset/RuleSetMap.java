/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;

public class RuleSetMap {
  public static boolean IsMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, true);
      if (tyType != null && (tyType instanceof TyNativeMap || tyType instanceof TyReactiveMap)) { return true; }
    }
    return false;
  }

  public static boolean IsNativeMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, true);
      if (tyType != null && (tyType instanceof TyNativeMap)) { return true; }
    }
    return false;
  }

  public static boolean IsReactiveMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, true);
      if (tyType != null && (tyType instanceof TyReactiveMap)) { return true; }
    }
    return false;
  }
}

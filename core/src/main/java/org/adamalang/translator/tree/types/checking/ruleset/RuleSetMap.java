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
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.TyNativePair;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;
import org.adamalang.translator.tree.types.traits.IsMap;

public class RuleSetMap {
  public static boolean IsMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType =  RuleSetCommon.Resolve(environment, tyTypeOriginal, true);
    return tyType != null && (tyType instanceof IsMap);
  }

  public static boolean IsNativeMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType =  RuleSetCommon.Resolve(environment, tyTypeOriginal, true);
    return tyType instanceof TyNativeMap;
  }

  public static boolean IsNativePair(final Environment environment, final TyType tyTypeOriginal) {
    var tyType =  RuleSetCommon.Resolve(environment, tyTypeOriginal, true);
    return tyType instanceof TyNativePair;
  }

  public static boolean IsReactiveMap(final Environment environment, final TyType tyTypeOriginal) {
    var tyType =  RuleSetCommon.Resolve(environment, tyTypeOriginal, true);
    return tyType instanceof TyReactiveMap;
  }
}

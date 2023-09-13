/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;

public class RuleSetIterable {
  public static boolean IsIterable(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && (tyType instanceof TyNativeList || tyType instanceof TyNativeArray || tyType instanceof TyNativeMap || tyType instanceof TyReactiveMap)) {
        return true;
      }
      if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: must be either an array, list, or map to use foreach or index lookup; instead got '%s'.", tyTypeOriginal.getAdamaType()));
      }
    }
    return false;
  }
}

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
import org.adamalang.translator.tree.types.natives.TyNativeTable;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;

public class RuleSetTable {
  static boolean IsNativeTable(final Environment environment, final TyType tyTypeOriginal) {
    var tyType =  RuleSetCommon.Resolve(environment, tyTypeOriginal, true);
    return tyType instanceof TyNativeTable;
  }

  public static boolean IsTable(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && (tyType instanceof TyNativeTable || tyType instanceof TyReactiveTable)) {
        return true;
      } else if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: must have a type of 'table<?>', but got a type of '%s'.", tyTypeOriginal.getAdamaType()), "TypeCheckReferences");
      }
    }
    return false;
  }
}

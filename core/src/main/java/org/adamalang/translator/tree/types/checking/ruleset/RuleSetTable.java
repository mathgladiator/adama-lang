/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeTable;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;

public class RuleSetTable {
  static boolean IsNativeTable(final Environment environment, final TyType tyTypeOriginal) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, true);
      if (tyType != null && tyType instanceof TyNativeTable) { return true; }
    }
    return false;
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

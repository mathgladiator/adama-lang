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
import org.adamalang.translator.tree.types.natives.TyNativeList;

public class RuleSetLists {
  private static boolean IsNativeList(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && tyType instanceof TyNativeList) { return true; }
      if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: expected an list, but was actually type '%s'.", tyTypeOriginal.getAdamaType()), "RuleSetArray");
      }
    }
    return false;
  }

  public static boolean IsNativeListOfStructure(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    if (tyTypeOriginal != null) {
      final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
      if (IsNativeList(environment, tyType, silent)) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, tyType, silent);
        if (elementType != null) { return RuleSetStructures.IsStructure(environment, elementType, silent); }
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
        if (elementType != null) { return RuleSetCommon.TestReactive(elementType); }
      }
    }
    return false;
  }
}

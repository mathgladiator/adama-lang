/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeArray;

public class RuleSetArray {
  public static boolean IsNativeArray(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && tyType instanceof TyNativeArray) {
        return true;
      } else if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: expected an array, but was actually type '%s'.", tyTypeOriginal.getAdamaType()), "RuleSetArray");
      }
    }
    return false;
  }

  public static boolean IsNativeArrayOfStructure(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (IsNativeArray(environment, tyType, silent)) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, tyType, silent);
        if (elementType != null) { return RuleSetStructures.IsStructure(environment, elementType, silent); }
      }
    }
    return false;
  }
}

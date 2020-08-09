/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;

public class RuleSetMod {
  public static CanMathResult CanMod(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
      final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
      if (aInteger && bInteger) { return CanMathResult.YesAndResultIsInteger; }
      if (RuleSetLists.TestReactiveList(environment, typeA, true)) {
        final var subTypeA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
        if (subTypeA != null) {
          final var childToRight = CanMod(environment, subTypeA, typeB, silent);
          if (childToRight != CanMathResult.No) { return RuleSetMath.UpgradeToList(childToRight); }
        }
      }
      if (!silent) {
        environment.document.createError(DocumentPosition.sum(typeA, typeB), String.format("Type check failure: the types '%s' and '%s' are unable to be used with the mod (%%) operator/.", typeA.getAdamaType(), typeB.getAdamaType()), "RuleSetLogic");
      }
    }
    return CanMathResult.No;
  }
}

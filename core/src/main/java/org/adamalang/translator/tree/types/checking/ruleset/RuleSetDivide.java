/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;

public class RuleSetDivide {
  public static CanMathResult CanDivide(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aNumber = RuleSetCommon.IsNumeric(environment, typeA, true);
      final var bNumber = RuleSetCommon.IsNumeric(environment, typeB, true);
      if (aNumber && bNumber) { return CanMathResult.YesAndResultIsDouble; }
      if (RuleSetLists.TestReactiveList(environment, typeA, true)) {
        final var subTypeA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
        if (subTypeA != null) {
          final var childToRight = CanDivide(environment, subTypeA, typeB, silent);
          if (childToRight != CanMathResult.No) { return RuleSetMath.UpgradeToList(childToRight); }
        }
      }
      if (!silent) {
        environment.document.createError(typeA, String.format("Type check failure: the types '%s' and '%s' are unable to be divided with the / operator.", typeA.getAdamaType(), typeB.getAdamaType()), "Divide");
      }
    }
    return CanMathResult.No;
  }
}

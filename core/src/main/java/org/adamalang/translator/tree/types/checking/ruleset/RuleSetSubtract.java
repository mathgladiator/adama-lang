/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;

public class RuleSetSubtract {
  public static CanMathResult CanSubstract(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
      final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
      if (aInteger && bInteger) { return CanMathResult.YesAndResultIsInteger; }
      final var aDouble = RuleSetCommon.IsDouble(environment, typeA, true);
      final var bDouble = RuleSetCommon.IsDouble(environment, typeB, true);
      if (aDouble && bDouble || aDouble && bInteger || aInteger && bDouble) { return CanMathResult.YesAndResultIsDouble; }
      if (RuleSetLists.TestReactiveList(environment, typeA, true)) {
        final var subTypeA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
        if (subTypeA != null) {
          final var childToRight = CanSubstract(environment, subTypeA, typeB, silent);
          if (childToRight != CanMathResult.No) { return RuleSetMath.UpgradeToList(childToRight); }
        }
      }
      if (!silent) {
        environment.document.createError(typeA, String.format("Type check failure: the types '%s' and '%s' are unable to be subtracted with the - operator.", typeA.getAdamaType(), typeB.getAdamaType()), "Subtracted");
      }
    }
    return CanMathResult.No;
  }
}

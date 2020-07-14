/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;

public class RuleSetAddition {
  public static CanMathResult CanAdd(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
      final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
      if (aInteger && bInteger) { return CanMathResult.YesAndResultIsInteger; }
      final var aLong = RuleSetCommon.IsLong(environment, typeA, true);
      final var bLong = RuleSetCommon.IsLong(environment, typeB, true);
      if ((aInteger || aLong) && (bInteger || bLong)) { return CanMathResult.YesAndResultIsLong; }
      final var aDouble = RuleSetCommon.IsDouble(environment, typeA, true);
      final var bDouble = RuleSetCommon.IsDouble(environment, typeB, true);
      if (aDouble && bDouble || aDouble && bInteger || aInteger && bDouble) { return CanMathResult.YesAndResultIsDouble; }
      final var aNumber = aInteger || aDouble || aLong;
      final var bNumber = bInteger || bDouble || bLong;
      final var aString = RuleSetCommon.IsString(environment, typeA, true);
      final var bString = RuleSetCommon.IsString(environment, typeB, true);
      if (aString && bString || aNumber && bString || aString && bNumber) { return CanMathResult.YesAndResultIsString; }
      final var aBool = RuleSetCommon.IsBoolean(environment, typeA, true);
      final var bBool = RuleSetCommon.IsBoolean(environment, typeB, true);
      if (aBool && bString || aString && bBool) { return CanMathResult.YesAndResultIsString; }
      if (RuleSetLists.TestReactiveList(environment, typeA, true)) {
        final var subTypeA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
        if (subTypeA != null) {
          final var childToRight = CanAdd(environment, subTypeA, typeB, silent);
          if (childToRight != CanMathResult.No) { return RuleSetMath.UpgradeToList(childToRight); }
        }
      }
      if (!silent) {
        environment.document.createError(typeA, String.format("Type check failure: the types '%s' and '%s' are unable to be added with the + operator.", typeA.getAdamaType(), typeB.getAdamaType()), "Addition");
      }
    }
    return CanMathResult.No;
  }
}

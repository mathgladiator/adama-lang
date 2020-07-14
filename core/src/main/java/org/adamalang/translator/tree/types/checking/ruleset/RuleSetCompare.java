/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;

public class RuleSetCompare {
  public static boolean CanCompare(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
      final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
      final var aLong = RuleSetCommon.IsLong(environment, typeA, true);
      final var bLong = RuleSetCommon.IsLong(environment, typeB, true);
      final var aDouble = RuleSetCommon.IsDouble(environment, typeA, true);
      final var bDouble = RuleSetCommon.IsDouble(environment, typeB, true);
      final var aNumber = aDouble || aInteger || aLong;
      final var bNumber = bDouble || bInteger || bLong;
      if (aNumber && bNumber) { return true; }
      final var aString = RuleSetCommon.IsString(environment, typeA, true);
      final var bString = RuleSetCommon.IsString(environment, typeB, true);
      if (aString && bString) { return true; }
      if (!silent) {
        environment.document.createError(typeA, String.format("Type check failure: the type '%s' is unable to be compared with type '%s'.", typeA.getAdamaType(), typeB.getAdamaType()), "Compare");
      }
    }
    return false;
  }
}
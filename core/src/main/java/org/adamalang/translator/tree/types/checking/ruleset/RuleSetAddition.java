/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
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
      boolean aReactiveList = RuleSetLists.TestReactiveList(environment, typeA, true);
      if (aReactiveList) {
        final var subTypeA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
        if (subTypeA != null) {
          final var childToRight = CanAdd(environment, subTypeA.makeCopyWithNewPosition(typeA, typeA.behavior), typeB, silent);
          if (childToRight != CanMathResult.No) { return RuleSetMath.UpgradeToList(childToRight); }
        }
      }
      if (!silent) {
        StringBuilder error = new StringBuilder();
        error.append(String.format("The types '%s' and '%s' are unable to be added with the + operator.", typeA.getAdamaType(), typeB.getAdamaType()));
        if (aInteger) {
          error.append("\n\tThe left hand side has a numeric type of 'int' which can be added with types: 'int', 'double', or 'string'.");
        } else if (aDouble) {
          error.append("\n\tThe left hand side has a numeric type of 'double' which can be added with types: 'int, 'double', or 'string'.");
        } else if (aLong) {
          error.append("\n\tThe left hand side has a numeric type of 'long' which can be added with types: 'int', 'long', or 'string'.");
        } else if (aBool) {
          error.append("\n\tThe left hand side has a type of 'bool' which may only be added with a right hand type of 'string'.");
        } else if (aString) {
          error.append("\n\tThe left hand side is a string which may be added with types: bool, int, double ");
        } else {
          error.append("\n\tThe left hand side has a type that is unable to the added.");
        }
        environment.document.createError(DocumentPosition.sum(typeA, typeB), error.toString(), "ADD01");
      }
    }
    return CanMathResult.No;
  }
}

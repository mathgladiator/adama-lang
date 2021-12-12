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
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;

public class RuleSetDivide {
  public static CanMathResult CanDivide(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aNumber = RuleSetCommon.IsNumeric(environment, typeA, true);
      final var bNumber = RuleSetCommon.IsNumeric(environment, typeB, true);
      if (aNumber && bNumber) { return CanMathResult.YesAndResultIsDouble; }
      if (!silent) {
        environment.document.createError(DocumentPosition.sum(typeA, typeB), String.format("Type check failure: the types '%s' and '%s' are unable to be divided with the / operator.", typeA.getAdamaType(), typeB.getAdamaType()), "Divide");
      }
    }
    return CanMathResult.No;
  }
}

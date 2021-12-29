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

public class RuleSetLogic {
  public static boolean CanUseLogic(
      final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aBoolean = RuleSetCommon.IsBoolean(environment, typeA, silent);
      final var bBoolean = RuleSetCommon.IsBoolean(environment, typeB, silent);
      if (aBoolean && bBoolean) {
        return true;
      } else if (!silent) {
        environment.document.createError(
            DocumentPosition.sum(typeA, typeB),
            String.format(
                "The types '%s' and '%s' are unable to be joined with logical operators (&&, ||).\n\tBoth left and right hand side of the operator must be of type 'bool'.",
                typeA.getAdamaType(), typeB.getAdamaType()),
            "RuleSetLogic");
      }
    }
    return false;
  }
}

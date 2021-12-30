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
import org.adamalang.translator.tree.types.checking.properties.CanTestEqualityResult;
import org.adamalang.translator.tree.types.traits.IsEnum;

public class RuleSetEquality {
  public static CanTestEqualityResult CanTestEquality(
      final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    if (typeA != null && typeB != null) {
      final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
      final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
      if (aInteger && bInteger) {
        return CanTestEqualityResult.Yes;
      }
      final var aComplex = RuleSetCommon.IsComplex(environment, typeA, true);
      final var bComplex = RuleSetCommon.IsComplex(environment, typeB, true);
      if (aComplex && bComplex) {
        return CanTestEqualityResult.YesButViaNear;
      }
      final var aLong = RuleSetCommon.IsLong(environment, typeA, true);
      final var bLong = RuleSetCommon.IsLong(environment, typeB, true);
      if ((aInteger || aLong) && (bInteger || bLong)) { return CanTestEqualityResult.Yes; }
      final var aNumber = RuleSetCommon.IsNumeric(environment, typeA, true);
      final var bNumber = RuleSetCommon.IsNumeric(environment, typeB, true);
      if (aNumber && bNumber) {
        // a mix of int/double
        return CanTestEqualityResult.YesButViaNear;
      }
      final var aDynamic = RuleSetCommon.IsDynamic(environment, typeA, true);
      final var bDynamic = RuleSetCommon.IsDynamic(environment, typeB, true);
      if (aDynamic && bDynamic) {
        return CanTestEqualityResult.Yes;
      }
      final var aAsset = RuleSetCommon.IsAsset(environment, typeA, true);
      final var bAsset = RuleSetCommon.IsAsset(environment, typeB, true);
      if (aAsset && bAsset) {
        return CanTestEqualityResult.Yes;
      }
      final var aString = RuleSetCommon.IsString(environment, typeA, true);
      final var bString = RuleSetCommon.IsString(environment, typeB, true);
      if (aString && bString) {
        return CanTestEqualityResult.Yes;
      }
      final var aBool = RuleSetCommon.IsBoolean(environment, typeA, true);
      final var bBool = RuleSetCommon.IsBoolean(environment, typeB, true);
      if (aBool && bBool) {
        return CanTestEqualityResult.Yes;
      }
      final var aStateMachineRef = RuleSetStateMachine.IsStateMachineRef(environment, typeA, true);
      final var bStateMachineRef = RuleSetStateMachine.IsStateMachineRef(environment, typeB, true);
      if (aStateMachineRef && bStateMachineRef) {
        return CanTestEqualityResult.Yes;
      }
      final var aClient = RuleSetAsync.IsClient(environment, typeA, true);
      final var bClient = RuleSetAsync.IsClient(environment, typeB, true);
      if (aClient && bClient) {
        return CanTestEqualityResult.Yes;
      }
      final var aEnum = RuleSetEnums.IsEnum(environment, typeA, true);
      final var bEnum = RuleSetEnums.IsEnum(environment, typeB, true);
      if (aEnum && bEnum) {
        if (((IsEnum) typeA).name().equals(((IsEnum) typeB).name())) {
          return CanTestEqualityResult.Yes;
        } else if (!silent) {
          environment.document.createError(
              DocumentPosition.sum(typeA, typeB),
              String.format(
                  "Type check failure: enum types are incompatible '%s' vs '%s'.",
                  typeA.getAdamaType(), typeB.getAdamaType()),
              "RuleSetEquality");
        }
        return CanTestEqualityResult.No;
      }
      if (!silent) {
        environment.document.createError(
            DocumentPosition.sum(typeA, typeB),
            String.format(
                "Type check failure: unable to compare types '%s' and '%s' for equality.",
                typeA.getAdamaType(), typeB.getAdamaType()),
            "RuleSetEquality");
      }
    }
    return CanTestEqualityResult.No;
  }
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanBumpResult;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.traits.IsNativeValue;

public class RuleSetBump {
  public static CanBumpResult CanBumpBool(final Environment environment, final TyType typeOriginal, final boolean silent) {
    final var type = RuleSetCommon.Resolve(environment, typeOriginal, silent);
    if (type != null) {
      if (RuleSetCommon.IsBoolean(environment, type, true)) { return CanBumpResult.YesWithNative; }
      if (type instanceof TyNativeList) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, type, silent);
        if (elementType != null) {
          if (RuleSetCommon.IsBoolean(environment, elementType, silent)) { return CanBumpResult.YesWithListTransformNative; }
        }
        return CanBumpResult.No;
      }
      if (!silent) {
        RuleSetCommon.IsBoolean(environment, type, silent);
      }
    }
    return CanBumpResult.No;
  }

  public static CanBumpResult CanBumpNumeric(final Environment environment, final TyType typeOriginal, final boolean silent) {
    final var type = typeOriginal;// RuleSetCommon.ResolvePastLazy(environment, typeOriginal, silent);
    if (type != null) {
      if (RuleSetCommon.IsInteger(environment, type, true) || RuleSetCommon.IsLong(environment, type, true) || RuleSetCommon.IsDouble(environment, type, true)) {
        if (type instanceof IsNativeValue) {
          return CanBumpResult.YesWithNative;
        } else {
          return CanBumpResult.YesWithSetter;
        }
      }
      if (type instanceof TyNativeList) {
        final var elementType = RuleSetCommon.ExtractEmbeddedType(environment, type, silent);
        if (elementType != null) {
          if (RuleSetCommon.IsInteger(environment, elementType, true) || RuleSetCommon.IsLong(environment, elementType, true) || RuleSetCommon.IsDouble(environment, elementType, true)) {
            if (elementType instanceof IsNativeValue) {
              return CanBumpResult.YesWithListTransformNative;
            } else {
              return CanBumpResult.YesWithListTransformSetter;
            }
          }
        }
      }
      if (!silent) {
        environment.document.createError(typeOriginal, String.format("Type check failure: Must have a type of 'int', 'long', 'double', 'list<int>', 'list<long>', 'list<double>'; instead got '%s'", typeOriginal.getAdamaType()),
            "CanBumpNumeric");
      }
    }
    return CanBumpResult.No;
  }
}

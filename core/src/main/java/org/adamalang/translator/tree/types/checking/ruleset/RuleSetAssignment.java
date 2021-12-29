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
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.AssignableEmbedType;
import org.adamalang.translator.tree.types.checking.properties.CanAssignResult;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;

public class RuleSetAssignment {
  private static CanAssignResult CanAssignBase(
      final Environment environment, final TyType left, final TyType right, final boolean silent) {
    if (left instanceof AssignmentViaSetter) {
      if (left instanceof TyReactiveMaybe) {
        if (right instanceof TyNativeMaybe || right instanceof TyReactiveMaybe) {
          return CanAssignResult.YesWithSetter;
        } else {
          return CanAssignResult.YesWithMakeThenSetter;
        }
      }
      return CanAssignResult.YesWithSetter;
    }
    return CanAssignResult.No;
  }

  public static CanAssignResult CanAssignWithAdd(
      final Environment environment, final TyType left, final TyType right, final boolean silent) {
    if (left == null || right == null) {
      return CanAssignResult.No;
    }
    var result = TestAssignReactively(environment, left, right);
    if (result != CanAssignResult.No) {
      return result;
    }
    if (left instanceof AssignmentViaNative) {
      return CanAssignResult.YesWithNativeOp;
    }
    result = CanAssignBase(environment, left, right, silent);
    if (result != CanAssignResult.No) {
      return result;
    }
    environment.document.createError(
        left,
        String.format(
            "The type '%s' is not applicable for add based assignment (+=)", left.getAdamaType()),
        "Assignment");
    return CanAssignResult.No;
  }

  public static CanAssignResult CanAssignWithMult(
      final Environment environment, final TyType left, final TyType right, final boolean silent) {
    if (left == null || right == null) {
      return CanAssignResult.No;
    }
    var result = TestAssignReactively(environment, left, right);
    if (result != CanAssignResult.No) {
      return result;
    }
    if (left instanceof AssignmentViaNative) {
      return CanAssignResult.YesWithNativeOp;
    }
    result = CanAssignBase(environment, left, right, silent);
    if (result != CanAssignResult.No) {
      return result;
    }
    environment.document.createError(
        left,
        String.format(
            "The type '%s' is not applicable for multiplication based assignment (*=)",
            left.getAdamaType()),
        "Assignment");
    return CanAssignResult.No;
  }

  public static CanAssignResult CanAssignWithSet(
      final Environment environment, final TyType left, final TyType right, final boolean silent) {
    if (left == null || right == null) {
      return CanAssignResult.No;
    }
    var result = TestAssignReactively(environment, left, right);
    if (result != CanAssignResult.No) {
      return result;
    }
    if (left instanceof AssignmentViaNative || left instanceof AssignmentViaNativeOnlyForSet) {
      return CanAssignResult.YesWithNativeOp;
    }
    result = CanAssignBase(environment, left, right, silent);
    if (result != CanAssignResult.No) {
      return result;
    }
    environment.document.createError(
        left,
        String.format("The type '%s' is not applicable for assignment (=)", left.getAdamaType()),
        "Assignment");
    return CanAssignResult.No;
  }

  public static CanAssignResult CanAssignWithSubtract(
      final Environment environment, final TyType left, final TyType right, final boolean silent) {
    if (left == null || right == null) {
      return CanAssignResult.No;
    }
    var result = TestAssignReactively(environment, left, right);
    if (result != CanAssignResult.No) {
      return result;
    }
    if (left instanceof AssignmentViaNative) {
      return CanAssignResult.YesWithNativeOp;
    }
    result = CanAssignBase(environment, left, right, silent);
    if (result != CanAssignResult.No) {
      return result;
    }
    environment.document.createError(
        left,
        String.format(
            "The type '%s' is not applicable for subtract based assignment (-=)",
            left.getAdamaType()),
        "Assignment");
    return CanAssignResult.No;
  }

  public static boolean CanTypeAStoreTypeB(
      final Environment environment,
      final TyType originalTypeA,
      final TyType originalTypeB,
      final StorageTweak tweak,
      final boolean silent) {
    final var typeA = RuleSetCommon.Resolve(environment, originalTypeA, silent);
    final var typeB = RuleSetCommon.Resolve(environment, originalTypeB, silent);
    if (typeA == null || typeB == null) {
      return false;
    }
    final var aInteger = RuleSetCommon.IsInteger(environment, typeA, true);
    final var bInteger = RuleSetCommon.IsInteger(environment, typeB, true);
    if (aInteger && bInteger) {
      return true;
    }
    final var aLong = RuleSetCommon.IsLong(environment, typeA, true);
    final var bLong = RuleSetCommon.IsLong(environment, typeB, true);
    if (aLong && (bLong || bInteger)) {
      return true;
    }
    final var aDouble = RuleSetCommon.IsDouble(environment, typeA, true);
    final var bDouble = RuleSetCommon.IsDouble(environment, typeB, true);
    if (aDouble && (bDouble || bInteger)) {
      return true;
    }
    final var aComplex = RuleSetCommon.IsComplex(environment, typeA, true);
    final var bComplex = RuleSetCommon.IsComplex(environment, typeB, true);
    if (aComplex && bComplex) {
      return true;
    }
    final var aBoolean = RuleSetCommon.IsBoolean(environment, typeA, true);
    final var bBoolean = RuleSetCommon.IsBoolean(environment, typeB, true);
    if (aBoolean && bBoolean) {
      return true;
    }
    final var aString = RuleSetCommon.IsString(environment, typeA, true);
    final var bString = RuleSetCommon.IsString(environment, typeB, true);
    if (aString && bString) {
      return true;
    }
    if (tweak == StorageTweak.Add) {
      if (aString && (bInteger || bBoolean || bDouble)) {
        return true;
      }
    }
    final var aClient = RuleSetAsync.IsClient(environment, typeA, true);
    final var bClient = RuleSetAsync.IsClient(environment, typeB, true);
    if (aClient && bClient) {
      return true;
    }
    final var aDynamic = RuleSetCommon.IsDynamic(environment, typeA, true);
    final var bDynamic = RuleSetCommon.IsDynamic(environment, typeB, true);
    if (aDynamic && bDynamic) {
      return true;
    }
    final var aAsset = RuleSetCommon.IsAsset(environment, typeA, true);
    final var bAsset = RuleSetCommon.IsAsset(environment, typeB, true);
    if (aAsset && bAsset) {
      return true;
    }
    final var aLabel = RuleSetStateMachine.IsStateMachineRef(environment, typeA, true);
    final var bLabel = RuleSetStateMachine.IsStateMachineRef(environment, typeB, true);
    if (aLabel && bLabel) {
      return true;
    }
    final var aEnum = RuleSetEnums.IsEnum(environment, typeA, true);
    final var bEnum = RuleSetEnums.IsEnum(environment, typeB, true);
    if (aEnum && bEnum) {
      if (((IsEnum) typeA).name().equals(((IsEnum) typeB).name())) {
        return true;
      }
      if (!silent) {
        environment.document.createError(
            originalTypeA,
            String.format(
                "Type check failure: enum types are incompatible '%s' vs '%s'.",
                originalTypeA.getAdamaType(), originalTypeB.getAdamaType()),
            "Assignment");
      }
    }
    if (RuleSetCommon.AreBothChannelTypesCompatible(environment, typeA, typeB)) {
      return true;
    }
    final var aEmbedAssign = TestAssignableWithEmbedd(typeA);
    final var bEmbedAssign = TestAssignableWithEmbedd(typeB);
    if (aEmbedAssign == bEmbedAssign && aEmbedAssign != AssignableEmbedType.None) {
      final var childA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
      final var childB = RuleSetCommon.ExtractEmbeddedType(environment, typeB, silent);
      if (CanTypeAStoreTypeB(environment, childA, childB, tweak, true)
          && CanTypeAStoreTypeB(environment, childB, childA, tweak, true)) {
        return true;
      }
    }
    final var aMap = RuleSetMap.IsNativeMap(environment, typeA);
    final var bMap = RuleSetMap.IsNativeMap(environment, typeB);
    if (aMap && bMap) {
      final var mapA = (IsMap) typeA;
      final var mapB = (IsMap) typeB;
      final var ab1 =
          CanTypeAStoreTypeB(
              environment,
              mapA.getDomainType(environment),
              mapB.getDomainType(environment),
              tweak,
              true);
      final var ab2 =
          CanTypeAStoreTypeB(
              environment,
              mapB.getDomainType(environment),
              mapA.getDomainType(environment),
              tweak,
              true);
      final var ab3 =
          CanTypeAStoreTypeB(
              environment,
              mapA.getRangeType(environment),
              mapB.getRangeType(environment),
              tweak,
              true);
      final var ab4 =
          CanTypeAStoreTypeB(
              environment,
              mapB.getRangeType(environment),
              mapA.getRangeType(environment),
              tweak,
              true);
      if (ab1 && ab2 && ab3 && ab4) {
        return true;
      }
    }
    final var aTable = RuleSetTable.IsNativeTable(environment, typeA);
    final var bTable = RuleSetTable.IsNativeTable(environment, typeB);
    if (aTable && bTable) {
      return ((TyNativeTable) typeA).messageName.equals(((TyNativeTable) typeB).messageName);
    }
    final var aMaybe =
        aEmbedAssign == AssignableEmbedType.Maybe && RuleSetMaybe.IsMaybe(environment, typeA, true);
    if (aMaybe && bEmbedAssign == AssignableEmbedType.None) {
      if (typeA instanceof TyReactiveMaybe
          && (typeB instanceof TyReactiveRecord || typeB instanceof TyNativeReactiveRecordPtr)) {
        environment.document.createError(
            originalTypeA,
            String.format(
                "Type check failure: the type '%s' is unable to store type '%s'.",
                originalTypeA.getAdamaType(), originalTypeB.getAdamaType()),
            "TypeCheckReferencesX");
        return false;
      }
      final var childA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
      if (CanTypeAStoreTypeB(environment, childA, typeB, tweak, silent)) {
        return true;
      }
    }
    final var aReactiveList = RuleSetLists.TestReactiveList(environment, typeA, true);
    if (aReactiveList) {
      final var childA = RuleSetCommon.ExtractEmbeddedType(environment, typeA, silent);
      if (CanTypeAStoreTypeB(environment, childA, typeB, tweak, true)) {
        return true;
      }
    }
    final var pTypeA = RuleSetCommon.ResolvePtr(environment, typeA, true);
    final var pTypeB = RuleSetCommon.ResolvePtr(environment, typeB, true);
    final var aStructure = RuleSetStructures.IsStructure(environment, pTypeA, true);
    final var bStructure = RuleSetStructures.IsStructure(environment, pTypeB, true);
    if (aStructure && bStructure) {
      if (((IsStructure) pTypeA).name().equals(((IsStructure) pTypeB).name())) {
        return true;
      }
    }
    if (!silent) {
      environment.document.createError(
          originalTypeA,
          String.format(
              "Type check failure: the type '%s' is unable to store type '%s'.",
              originalTypeA.getAdamaType(), originalTypeB.getAdamaType()),
          "TypeCheckReferences");
    }
    return false;
  }

  private static AssignableEmbedType TestAssignableWithEmbedd(final TyType type) {
    if (type instanceof TyNativeMaybe || type instanceof TyReactiveMaybe) {
      return AssignableEmbedType.Maybe;
    }
    if (type instanceof TyNativeArray) {
      return AssignableEmbedType.Array;
    }
    if (type instanceof TyNativeList) {
      return AssignableEmbedType.List;
    }
    if (type instanceof TyNativeFuture) {
      return AssignableEmbedType.Future;
    }
    if (type instanceof TyNativeReactiveRecordPtr) {
      return AssignableEmbedType.Ptr;
    }
    return AssignableEmbedType.None;
  }

  private static CanAssignResult TestAssignReactively(
      final Environment environment, final TyType left, final TyType right) {
    //  && !RuleSetLists.TestReactiveList(environment, right, true)
    if (RuleSetLists.TestReactiveList(environment, left, true)) {
      final var childA = RuleSetCommon.ExtractEmbeddedType(environment, left, true);
      if (CanTypeAStoreTypeB(environment, childA, right, StorageTweak.None, true)) {
        if (RuleSetMaybe.IsMaybe(environment, childA, true)) {
          return CanAssignResult.YesWithTransformThenMakeSetter;
        } else {
          return CanAssignResult.YesWithTransformSetter;
        }
      }
    }
    return CanAssignResult.No;
  }
}

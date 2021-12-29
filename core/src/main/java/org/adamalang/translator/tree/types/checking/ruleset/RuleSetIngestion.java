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

import java.util.Map;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeReactiveRecordPtr;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.IsStructure;

public class RuleSetIngestion {
  public static boolean CanAIngestB(final Environment environment, final TyType originalTypeA, final TyType originalTypeB, final boolean silent) {
    final var typeLeft = environment.rules.ResolvePtr(originalTypeA, silent);
    final var typeRight = environment.rules.ResolvePtr(originalTypeB, silent);
    if (typeLeft == null || typeRight == null) { return false; }
    if (RuleSetMap.IsMap(environment, typeLeft) && RuleSetMap.IsMap(environment, typeRight)) {
      if (RuleSetAssignment.CanTypeAStoreTypeB(environment, ((IsMap) typeLeft).getDomainType(environment), ((IsMap) typeRight).getDomainType(environment), StorageTweak.None, silent)) {
        if (RuleSetIngestion.IngestionLeftSidePossible(environment, ((IsMap) typeLeft).getRangeType(environment))) {
          if (CanAIngestB(environment, ((IsMap) typeLeft).getRangeType(environment), ((IsMap) typeRight).getRangeType(environment), silent)) {
            return true;
          } else if (!silent) {
            environment.document.createError(originalTypeA, String.format("Type check failure: ranges are incompatble for ingestion.", typeLeft.getAdamaType()), "RuleSetIngestion");
          }
        } else {
          return RuleSetAssignment.CanTypeAStoreTypeB(environment,  ((IsMap) typeLeft).getRangeType(environment), ((IsMap) typeRight).getRangeType(environment), StorageTweak.None, silent);
        }
        return false;
      } else if (!silent) {
        environment.document.createError(originalTypeA, String.format("Type check failure: domains are incompatble for ingestion %s <- %s.", ((IsMap) typeLeft).getDomainType(environment).getAdamaType(), ((IsMap) typeRight).getDomainType(environment).getAdamaType()), "RuleSetIngestion");
      }
    }
    TyType leftStructureType = null;
    TyType rightStructureType = null;
    if (environment.rules.IsStructure(typeLeft, true)) {
      leftStructureType = typeLeft;
    } else if (environment.rules.IsTable(typeLeft, true) || typeLeft instanceof TyNativeReactiveRecordPtr) {
      leftStructureType = environment.rules.ExtractEmbeddedType(typeLeft, silent);
    } else if (environment.rules.IsMaybe(typeLeft, true)) {
      return CanAIngestB(environment, environment.rules.ExtractEmbeddedType(typeLeft, silent), typeRight, silent);
    } else if (!silent) {
      environment.document.createError(originalTypeA, String.format("Type check failure: unable to ingest into this type '%s'.", typeLeft.getAdamaType()), "RuleSetIngestion");
    }
    if (environment.rules.IsStructure(typeRight, true)) {
      rightStructureType = typeRight;
    } else if (environment.rules.IsNativeArrayOfStructure(typeRight, true)) {
      rightStructureType = environment.rules.ExtractEmbeddedType(typeRight, silent);
    } else if (environment.rules.IsNativeListOfStructure(typeRight, true)) {
      rightStructureType = environment.rules.ExtractEmbeddedType(typeRight, silent);
    } else if (!silent) {
      environment.document.createError(originalTypeB, String.format("Type check failure: '%s' unable to produce data to ingest into '%s'.", typeRight.getAdamaType(), typeLeft.getAdamaType()), "RuleSetIngestion");
    }
    leftStructureType = environment.rules.Resolve(leftStructureType, silent);
    rightStructureType = environment.rules.Resolve(rightStructureType, silent);
    if (rightStructureType == null || leftStructureType == null) { return false; }
    final var leftStorage = ((IsStructure) leftStructureType).storage();
    final var rightStorage = ((IsStructure) rightStructureType).storage();
    var possible = true;
    for (final Map.Entry<String, FieldDefinition> rightFieldEntry : rightStorage.fields.entrySet()) {
      final var leftField = leftStorage.fields.get(rightFieldEntry.getKey());
      final var rightField = rightFieldEntry.getValue();
      if (leftField == null) {
        environment.document.createError(originalTypeB, String.format("Type check failure: The field '%s' was lost during ingestion", rightFieldEntry.getKey()), "RuleSetIngestion");
        possible = false;
      } else {
        if (RuleSetIngestion.IngestionLeftSidePossible(environment, leftField.type)) {
          if (!CanAIngestB(environment, leftField.type, rightField.type, silent)) {
            possible = false;
          }
        } else {
          if (!RuleSetAssignment.CanTypeAStoreTypeB(environment, leftField.type, rightField.type, StorageTweak.None, silent)) {
            possible = false;
          }
        }
      }
    }
    return possible;
  }

  public static boolean IngestionLeftElementRequiresRecursion(final Environment environment, final TyType originalTypeA) {
    final var typeLeft = environment.rules.Resolve(originalTypeA, true);
    if (environment.rules.IsMaybe(typeLeft, true)) { return IngestionLeftElementRequiresRecursion(environment, environment.rules.ExtractEmbeddedType(typeLeft, true)); }
    return environment.rules.IsTable(typeLeft, true) || environment.rules.IsStructure(typeLeft, true) || typeLeft instanceof TyReactiveMap || typeLeft instanceof TyNativeReactiveRecordPtr;
  }

  static boolean IngestionLeftSidePossible(final Environment environment, final TyType originalTypeA) {
    final var typeLeft = environment.rules.Resolve(originalTypeA, true);
    if (environment.rules.IsMaybe(typeLeft, true)) { return IngestionLeftSidePossible(environment, environment.rules.ExtractEmbeddedType(typeLeft, true)); }
    return environment.rules.IsStructure(typeLeft, true) || environment.rules.IsTable(typeLeft, true) || typeLeft instanceof TyReactiveMap || typeLeft instanceof TyNativeReactiveRecordPtr;
  }

  public static boolean IngestionLeftSideRequiresBridgeCreate(final Environment environment, final TyType originalTypeA) {
    final var typeLeft = environment.rules.Resolve(originalTypeA, true);
    return environment.rules.IsTable(typeLeft, true) || environment.rules.IsMaybe(typeLeft, true);
  }

  public static boolean IngestionRightSideRequiresIteration(final Environment environment, final TyType originalTypeB) {
    final var typeRight = environment.rules.Resolve(originalTypeB, true);
    return environment.rules.IsNativeArrayOfStructure(typeRight, true) || environment.rules.IsNativeListOfStructure(typeRight, true);
  }
}

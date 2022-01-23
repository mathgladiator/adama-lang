/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.checking;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineStateTransition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.*;
import org.adamalang.translator.tree.types.checking.ruleset.*;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.traits.IsEnum;

public class Rules {
  private final Environment environment;

  public Rules(final Environment environment) {
    this.environment = environment;
  }

  /** FROM: RuleSetAddition */
  public CanMathResult CanAdd(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetAddition.CanAdd(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetIngestion */
  public boolean CanAIngestB(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetIngestion.CanAIngestB(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetAssignment */
  public CanAssignResult CanAssignWithAdd(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetAssignment.CanAssignWithAdd(environment, tyTypeA, tyTypeB, silent);
  }

  public CanAssignResult CanAssignWithMult(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetAssignment.CanAssignWithMult(environment, tyTypeA, tyTypeB, silent);
  }

  public CanAssignResult CanAssignWithSet(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetAssignment.CanAssignWithSet(environment, tyTypeA, tyTypeB, silent);
  }

  public CanAssignResult CanAssignWithSubtract(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetAssignment.CanAssignWithSubtract(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetBump */
  public CanBumpResult CanBumpBool(final TyType tyType, final boolean silent) {
    return RuleSetBump.CanBumpBool(environment, tyType, silent);
  }

  public CanBumpResult CanBumpNumeric(final TyType tyType, final boolean silent) {
    return RuleSetBump.CanBumpNumeric(environment, tyType, silent);
  }

  /** FROM: RuleSetCompare */
  public boolean CanCompare(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetCompare.CanCompare(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetDivide */
  public CanMathResult CanDivide(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetDivide.CanDivide(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetMod */
  public CanMathResult CanMod(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetMod.CanMod(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetMultiply */
  public CanMathResult CanMultiply(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetMultiply.CanMultiply(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetStructures */
  public boolean CanStructureAProjectIntoStructureB(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetStructures.CanStructureAProjectIntoStructureB(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetSubtract */
  public CanMathResult CanSubstract(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetSubtract.CanSubstract(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetEquality */
  public CanTestEqualityResult CanTestEquality(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetEquality.CanTestEquality(environment, tyTypeA, tyTypeB, silent);
  }

  public boolean CanTypeAStoreTypeB(final TyType tyTypeA, final TyType tyTypeB, final StorageTweak result, final boolean silent) {
    return RuleSetAssignment.CanTypeAStoreTypeB(environment, tyTypeA, tyTypeB, result, silent);
  }

  /** FROM: RuleSetLogic */
  public boolean CanUseLogic(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetLogic.CanUseLogic(environment, tyTypeA, tyTypeB, silent);
  }

  /** FROM: RuleSetCommon */
  public TyType EnsureRegisteredAndDedupe(final TyType tyType, final boolean silent) {
    return RuleSetCommon.EnsureRegisteredAndDedupe(environment, tyType, silent);
  }

  public TyType ExtractEmbeddedType(final TyType tyType, final boolean silent) {
    return RuleSetCommon.ExtractEmbeddedType(environment, tyType, silent);
  }

  /** FROM: RuleSetEnums */
  public IsEnum FindEnumType(final String search, final DocumentPosition position, final boolean silent) {
    return RuleSetEnums.FindEnumType(environment, search, position, silent);
  }

  /** FROM: RuleSetMessages */
  public TyNativeMessage FindMessageStructure(final String search, final DocumentPosition position, final boolean silent) {
    return RuleSetMessages.FindMessageStructure(environment, search, position, silent);
  }

  /** FROM: RuleSetStateMachine */
  public DefineStateTransition FindStateMachineStep(final String search, final DocumentPosition position, final boolean silent) {
    return RuleSetStateMachine.FindStateMachineStep(environment, search, position, silent);
  }

  public TyType GetMaxType(final TyType tyTypeA, final TyType tyTypeB, final boolean silent) {
    return RuleSetCommon.GetMaxType(environment, tyTypeA, tyTypeB, silent);
  }

  public WrapInstruction GetMaxTypeBasedWrappingInstruction(final TyType tyTypeA, final TyType tyTypeB) {
    return RuleSetCommon.GetMaxTypeBasedWrappingInstruction(environment, tyTypeA, tyTypeB);
  }

  public boolean IngestionLeftElementRequiresRecursion(final TyType tyType) {
    return RuleSetIngestion.IngestionLeftElementRequiresRecursion(environment, tyType);
  }

  public boolean IngestionLeftSideRequiresBridgeCreate(final TyType tyType) {
    return RuleSetIngestion.IngestionLeftSideRequiresBridgeCreate(environment, tyType);
  }

  public boolean IngestionRightSideRequiresIteration(final TyType tyType) {
    return RuleSetIngestion.IngestionRightSideRequiresIteration(environment, tyType);
  }

  /** FROM: RuleSetMath */
  public TyType InventMathType(final TyType tyTypeA, final TyType tyTypeB, final CanMathResult result) {
    return RuleSetMath.InventMathType(environment, tyTypeA, tyTypeB, result);
  }

  public boolean IsBoolean(final TyType tyType, final boolean silent) {
    return RuleSetCommon.IsBoolean(environment, tyType, silent);
  }

  /** FROM: RuleSetAsync */
  public boolean IsClient(final TyType tyType, final boolean silent) {
    return RuleSetAsync.IsClient(environment, tyType, silent);
  }

  /** FROM: RuleSetFunctions */
  public boolean IsFunction(final TyType tyType, final boolean silent) {
    return RuleSetFunctions.IsFunction(environment, tyType, silent);
  }

  public boolean IsInteger(final TyType tyType, final boolean silent) {
    return RuleSetCommon.IsInteger(environment, tyType, silent);
  }

  /** FROM: RuleSetIterable */
  public boolean IsIterable(final TyType tyType, final boolean silent) {
    return RuleSetIterable.IsIterable(environment, tyType, silent);
  }

  /** FROM: RuleSetMap */
  public boolean IsMap(final TyType tyType) {
    return RuleSetMap.IsMap(environment, tyType);
  }

  /** FROM: RuleSetMaybe */
  public boolean IsMaybe(final TyType tyType, final boolean silent) {
    return RuleSetMaybe.IsMaybe(environment, tyType, silent);
  }

  /** FROM: RuleSetArray */
  public boolean IsNativeArray(final TyType tyType, final boolean silent) {
    return RuleSetArray.IsNativeArray(environment, tyType, silent);
  }

  public boolean IsNativeArrayOfStructure(final TyType tyType, final boolean silent) {
    return RuleSetArray.IsNativeArrayOfStructure(environment, tyType, silent);
  }

  /** FROM: RuleSetLists */
  public boolean IsNativeListOfStructure(final TyType tyType, final boolean silent) {
    return RuleSetLists.IsNativeListOfStructure(environment, tyType, silent);
  }

  public boolean IsNativeMessage(final TyType tyType, final boolean silent) {
    return RuleSetMessages.IsNativeMessage(environment, tyType, silent);
  }

  public boolean IsNumeric(final TyType tyType, final boolean silent) {
    return RuleSetCommon.IsNumeric(environment, tyType, silent);
  }

  public boolean IsStateMachineRef(final TyType tyType, final boolean silent) {
    return RuleSetStateMachine.IsStateMachineRef(environment, tyType, silent);
  }

  public boolean IsStructure(final TyType tyType, final boolean silent) {
    return RuleSetStructures.IsStructure(environment, tyType, silent);
  }

  /** FROM: RuleSetTable */
  public boolean IsTable(final TyType tyType, final boolean silent) {
    return RuleSetTable.IsTable(environment, tyType, silent);
  }

  public TyType Resolve(final TyType tyType, final boolean silent) {
    return RuleSetCommon.Resolve(environment, tyType, silent);
  }

  public TyType ResolvePtr(final TyType tyType, final boolean silent) {
    return RuleSetCommon.ResolvePtr(environment, tyType, silent);
  }

  /** FROM: RuleSetConversion */
  public void SignalConversionIssue(final TyType tyType, final boolean silent) {
    RuleSetConversion.SignalConversionIssue(environment, tyType, silent);
  }
}

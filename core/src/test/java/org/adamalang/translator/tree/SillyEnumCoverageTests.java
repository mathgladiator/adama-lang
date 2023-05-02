/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.definitions.FunctionSpecialization;
import org.adamalang.translator.tree.definitions.MessageHandlerBehavior;
import org.adamalang.translator.tree.expressions.testing.EnvLookupName;
import org.adamalang.translator.tree.expressions.MessageConversionStyle;
import org.adamalang.translator.tree.expressions.constants.DynamicNullConstant;
import org.adamalang.translator.tree.operands.AssignmentOp;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.operands.PostfixMutateOp;
import org.adamalang.translator.tree.operands.PrefixMutateOp;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.control.AlterControlFlowMode;
import org.adamalang.translator.tree.types.checking.properties.*;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;
import org.junit.Assert;
import org.junit.Test;

public class SillyEnumCoverageTests {
  @Test
  public void coverage() {
    Assert.assertNull(BinaryOp.fromText("NOPEx"));
    PostfixMutateOp.fromText("!x");
    PrefixMutateOp.fromText("!x");
    AssignmentOp.fromText("!x");
    WrapInstruction.None.toString();
    WrapInstruction.valueOf("WrapBWithMaybe");
    MessageHandlerBehavior.EnqueueItemIntoNativeChannel.toString();
    MessageHandlerBehavior.valueOf("EnqueueItemIntoNativeChannel");
    DocumentEvent.ClientConnected.toString();
    DocumentEvent.valueOf("ClientConnected");
    FunctionSpecialization.Pure.toString();
    FunctionSpecialization.valueOf("Pure");
    MessageConversionStyle.Maybe.toString();
    MessageConversionStyle.valueOf("Maybe");
    AlterControlFlowMode.Abort.toString();
    AlterControlFlowMode.valueOf("Abort");
    ControlFlow.Open.toString();
    ControlFlow.valueOf("Open");
  }

  @Test
  public void dyn_null() {
    new DynamicNullConstant(Token.WRAP("null")).emit((t) -> {});
  }

  @Test
  public void coverageSimple() {
    AssignableEmbedType.None.toString();
    CanAssignResult.No.toString();
    CanBumpResult.No.toString();
    CanMathResult.No.toString();
    CanTestEqualityResult.No.toString();
    StorageTweak.None.toString();
    WrapInstruction.None.toString();
    StorageSpecialization.Message.toString();
    ComputeContext.Assignment.toString();
    EnvLookupName.Blocked.toString();
    IndexLookupStyle.ExpressionLookupMethod.toString();
    IndexLookupStyle.ExpressionGetOrCreateMethod.toString();
    ComputeContext.Computation.toString();
  }
}

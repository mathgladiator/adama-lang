/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.definitions.FunctionSpecialization;
import org.adamalang.translator.tree.definitions.MessageHandlerBehavior;
import org.adamalang.translator.tree.expressions.testing.EnvLookupName;
import org.adamalang.translator.tree.expressions.ConversionStyle;
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
    ConversionStyle.Maybe.toString();
    ConversionStyle.valueOf("Maybe");
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
